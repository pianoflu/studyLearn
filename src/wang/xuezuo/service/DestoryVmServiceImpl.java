package wang.xuezuo.service;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.virtualbox_5_0.IMachine;
import org.virtualbox_5_0.IVirtualBox;
import org.virtualbox_5_0.VBoxException;
import org.virtualbox_5_0.VirtualBoxManager;

import com.alibaba.fastjson.JSONObject;

import wang.xuezuo.commons.VboxCommon;
import wang.xuezuo.dao.DestoryVmDao;

/**
 * 虚拟机销毁service实现
 * @author lijiarui
 *
 */
@Service
@WebService(endpointInterface = "wang.xuezuo.service.DestoryVmService")
public class DestoryVmServiceImpl implements DestoryVmService{
	
	private static final Logger logger = Logger.getLogger(DestoryVmServiceImpl.class);

	@Autowired 
	private DestoryVmDao destoryVmDao;
	
	@Override
	public JSONObject destoryVm(String uuid) {
		logger.info("uuid:"+uuid+"执行destoryVm方法");
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
		
		//销毁操作
		try {
			if (vbox != null) {
				IMachine machine = vbox.findMachine(uuid);
				VboxCommon.deleteVm(mgr, machine);
			}
		} catch (VBoxException e) {
			logger.error(uuid+":VBoxException error: " + e.getMessage() + ".Error cause: " + e.getCause());
			jsonObject.put("error", "释放资源失败");
		} catch (Exception e) {
			logger.error(uuid+":销毁操作失败: " + e.getMessage());
			jsonObject.put("error", "释放资源失败");
		}
		//释放连接
		try {
			VboxCommon.disconnectVm(mgr);
        } catch (VBoxException e) {
        	logger.error("释放连接失败: " + e.getMessage());
        }
		
		//删除虚拟机信息
		try {
			destoryVmDao.deleteVm(uuid);
		} catch (Exception e) {
			logger.error(uuid+":删除虚拟机信息失败: " + e.getMessage());
		}
		logger.info("uuid:"+uuid+"结束destoryVm方法");
		return jsonObject;
	}
}
