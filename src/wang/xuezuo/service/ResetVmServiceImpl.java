package wang.xuezuo.service;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.virtualbox_5_0.IMachine;
import org.virtualbox_5_0.IVirtualBox;
import org.virtualbox_5_0.MachineState;
import org.virtualbox_5_0.VBoxException;
import org.virtualbox_5_0.VirtualBoxManager;

import com.alibaba.fastjson.JSONObject;

import wang.xuezuo.commons.VboxCommon;

/**
 * 虚拟机重启service实现
 * 
 * @author lijiarui
 *
 */
@Service
@WebService(endpointInterface = "wang.xuezuo.service.ResetVmService")
public class ResetVmServiceImpl implements ResetVmService {

	private static final Logger logger = Logger.getLogger(ResetVmServiceImpl.class);

	@Override
	public JSONObject resetVm(String uuid) {
		logger.info("uuid:"+uuid+"执行resetVm方法");
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
		// 重启操作
		try {
			if (vbox != null) {
				IMachine machine = vbox.findMachine(uuid);
				if (machine.getState() == MachineState.PoweredOff) {
					jsonObject.put("error", "资源未开启");
				} else {
					VboxCommon.resetVm(mgr, machine);
				}
			}
		} catch (VBoxException e) {
			logger.error(uuid+":VBoxException error: " + e.getMessage() + ".Error cause: " + e.getCause());
			jsonObject.put("error", "重启资源失败");
		} catch (Exception e) {
			logger.error(uuid+":重启操作失败: " + e.getMessage());
			jsonObject.put("error", "重启资源失败");
		}
		// 释放连接
		try {
			VboxCommon.disconnectVm(mgr);
		} catch (VBoxException e) {
			logger.error("释放连接失败: " + e.getMessage());
		}
		logger.info("uuid:"+uuid+"结束resetVm方法");
		return jsonObject;
	}

}
