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

import wang.xuezuo.commons.VboxCommon;
import wang.xuezuo.dao.CloseVmDao;

/**
 * 虚拟机关闭service实现
 * 
 * @author lijiarui
 *
 */
@Service
@WebService(endpointInterface = "wang.xuezuo.service.CloseVmService")
public class CloseVmServiceImpl implements CloseVmService {

	private static final Logger logger = Logger.getLogger(CloseVmServiceImpl.class);

	@Autowired
	private CloseVmDao closeVmDao;

	@Override
	public JSONObject closeVm(String uuid) {
		logger.info("uuid:"+uuid+"执行closeVm方法");
		JSONObject jsonObject = new JSONObject();
		// 连接虚拟机webservice
		VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);
		IVirtualBox vbox = null;
		try {
			vbox = VboxCommon.connectVm(mgr);
		} catch (VBoxException e) {
			logger.error("虚拟机webserver未开启");
			jsonObject.put("error", "服务器出错");
		}
		// 关闭操作
		try {
			if (vbox != null) {
				IMachine machine = vbox.findMachine(uuid);
				if (machine.getState() == MachineState.PoweredOff) {
					jsonObject.put("error", "资源已关闭");
					logger.info("uuid:"+uuid+"已关闭");
				} else {
					VboxCommon.poweroffVm(mgr, machine);
				}
			}
		} catch (VBoxException e) {
			logger.error(uuid+":VBoxException error: " + e.getMessage() + ".Error cause: " + e.getCause());
			jsonObject.put("error", "关闭资源失败");
		} catch (Exception e) {
			logger.error(uuid+":关闭操作失败: " + e.getMessage());
			jsonObject.put("error", "关闭资源失败");
		}
		//释放连接
		try {
			VboxCommon.disconnectVm(mgr);
        } catch (VBoxException e) {
        	logger.error("释放连接失败: " + e.getMessage());
        }
		// 更新虚拟机的状态 0：关闭，1：已启动
		try {
			closeVmDao.updateVmStatus(0, uuid);
		} catch (Exception e) {
			logger.error("更新虚拟机状态失败: " + e.getMessage());
		}
		logger.info("uuid:"+uuid+"结束closeVm方法");
		return jsonObject;
	}

}
