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
		logger.info("判断系统资源是否足够开启虚拟机");
		if (ifCanStartVm()) {
			// 连接虚拟机webservice
			logger.info("系统资源足够，能够开启虚拟机");
			logger.info("开始连接webservice");
			VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);
			IVirtualBox vbox = null;
			try {
				vbox = VboxCommon.connectVm(mgr);
				logger.info("成功连接webservice");
			} catch (VBoxException e) {
				logger.error("虚拟机webserver未开启");
				jsonObject.put("error", "服务器出错");
			}
			// 开启虚拟机
			logger.info("开始开启虚拟机"+uuid);
			try {
					IMachine m = vbox.findMachine(uuid);
					if (m.getState() == MachineState.Running) {
						logger.error("您的资源已开启");
						jsonObject.put("error", "您的资源已开启");
					} else {
						String ip = VboxCommon.startVm(mgr, m);
						if (ip == null || "".equals(ip)) {
							logger.error("不能打开资源，资源可能不可用");
							jsonObject.put("error", "打开资源失败");
						} else {
							jsonObject.put("ip", ip);
							logger.info("成功开启虚拟机"+uuid+".ip为"+ip);
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
			logger.info("开始释放webservice连接...");
			try {
				VboxCommon.disconnectVm(mgr);
				logger.info("成功释放webservice连接...");
	        } catch (VBoxException e) {
	        	logger.error(uuid+":释放连接失败: " + e.getMessage());
	        }
			// 更新虚拟机的状态 0：关闭，1：已启动
			logger.info("开始更改数据库中虚拟机状态.status设为1");
			try {
				startVmDao.updateVmStatus(1, uuid);
				logger.info("成功更改数据库中虚拟机状态.");
			} catch (Exception e) {
				logger.error(uuid+":更新虚拟机状态失败: " + e.getMessage());
			}
		} else {
			jsonObject.put("error", "对不起，服务器资源不足");
			logger.info("服务器资源不足，不能开启");
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
