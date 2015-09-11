package wang.xuezuo.service;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.virtualbox_5_0.IMachine;
import org.virtualbox_5_0.IVirtualBox;
import org.virtualbox_5_0.MachineState;
import org.virtualbox_5_0.VBoxException;
import org.virtualbox_5_0.VirtualBoxManager;

import com.alibaba.fastjson.JSONObject;

import wang.xuezuo.commons.Utils;
import wang.xuezuo.commons.VboxCommon;
import wang.xuezuo.dao.ResourceCommonDao;
import wang.xuezuo.dao.StartVmDao;
import wang.xuezuo.domain.Host;

/**
 * 虚拟机开启service实现
 * 
 * @author lijiarui
 *
 */

@Service
@WebService(endpointInterface = "wang.xuezuo.service.StartVmService")
public class StartVmServiceImpl implements StartVmService {

	private static final Logger logger = Logger.getLogger(StartVmServiceImpl.class);

	@Autowired
	private ResourceCommonDao resourceCommonDao;

	@Autowired
	private StartVmDao startVmDao;

	@Override
	public JSONObject startVm(String uuid) {
		logger.info(uuid+"开始启动虚拟机");
		JSONObject jsonObject = new JSONObject();
		if (ifCanStartVm()) {
			// 连接虚拟机webservice
			VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);
			IVirtualBox vbox = null;
			try {
				vbox = VboxCommon.connectVm(mgr);
			} catch (VBoxException e) {
				logger.error("虚拟机webserver未开启");
				jsonObject.put("error", "服务器出错");
			}
			// 开启虚拟机
			try {
				if (vbox != null) {
					IMachine m = vbox.findMachine(uuid);
					if (m.getState() == MachineState.Running) {
						jsonObject.put("error", "您的资源已开启");
					} else {
						String ip = VboxCommon.startVm(mgr, m);
						logger.info(uuid+":ip为"+ip);
						if (ip == null || "".equals(ip)) {
							jsonObject.put("error", "打开资源失败");
						} else {
							jsonObject.put("ip", ip);
							// 可能需要VM里VNC的端口号和密码，不确定
						}
					}
				}
			} catch (VBoxException e) {
				logger.error(uuid+":VBoxException error: " + e.getMessage() + ".Error cause: " + e.getCause());
				jsonObject.put("error", "打开资源失败");
			} catch (Exception e) {
				logger.error(uuid+":打开资源失败: " + e.getMessage());
				jsonObject.put("error", "打开资源失败");
			}
			//释放连接
			try {
				VboxCommon.disconnectVm(mgr);
	        } catch (VBoxException e) {
	        	logger.error(uuid+":释放连接失败: " + e.getMessage());
	        }
			// 更新虚拟机的状态 0：关闭，1：已启动
			try {
				startVmDao.updateVmStatus(1, uuid);
			} catch (Exception e) {
				logger.error(uuid+":更新虚拟机状态失败: " + e.getMessage());
			}
		} else {
			jsonObject.put("error", "对不起，服务器资源不足");
		}
		logger.info(uuid+"结束调用startVm");
		return jsonObject;
	}

	/**
	 * 根据资源占用情况判断是否可以开启虚拟机
	 * 
	 * @return
	 */
	public boolean ifCanStartVm() {
		Host hostInfo = resourceCommonDao.getHostInfo();
		// 已经为虚拟机分配磁盘资源，不判断磁盘资源
		double memoryTopLimitRate = (double) hostInfo.getMemoryToplimit() / (double) hostInfo.getMemory();
		double cpuTopLimitRate = hostInfo.getCpuUsageToplimit();
		logger.info("当前内存使用率:"+Utils.getMemeryUsedRateForWindows());
		if (memoryTopLimitRate > Utils.getMemeryUsedRateForWindows() && cpuTopLimitRate > Utils.getCpuRatioForWindows()) {
			return true;
		} else {
			return false;
		}
	}

}
