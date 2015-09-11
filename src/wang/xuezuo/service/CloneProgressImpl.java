package wang.xuezuo.service;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.virtualbox_5_0.IProgress;
import org.virtualbox_5_0.IVirtualBox;
import org.virtualbox_5_0.VBoxException;
import org.virtualbox_5_0.VirtualBoxManager;

import com.alibaba.fastjson.JSONObject;

import wang.xuezuo.commons.VboxCommon;
/**
 * 虚拟机克隆进度返回接口实现
 * @author lijiarui
 *
 */
@Service
@WebService(endpointInterface = "wang.xuezuo.service.CloneProgress")
public class CloneProgressImpl implements CloneProgress{

	private static final Logger logger = Logger.getLogger(CloseVmServiceImpl.class);
	
	@Override
	public JSONObject ProgressBar(String uuid) {
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
		// 查询进度
		try {
			List<IProgress> progs = new ArrayList<IProgress>();
			progs=vbox.getProgressOperations();
			if(progs.size() != 0){
				for(int i=0;i<progs.size();i++){
					if(uuid.equals(progs.get(i).getId())){
						jsonObject.put("precent",progs.get(i).getPercent());
						break;
					}
				}
			}else{
				jsonObject.put("precent",100);
			}
		} catch (VBoxException e) {
			logger.error("VBoxException error: " + e.getMessage() + ".Error cause: " + e.getCause());
			jsonObject.put("error", "查询进度失败");
		} catch (Exception e) {
			logger.error("查询进度失败: " + e.getMessage());
			jsonObject.put("error", "查询进度失败");
		}
		//释放连接
		try {
			VboxCommon.disconnectVm(mgr);
        } catch (VBoxException e) {
        	logger.error("释放连接失败: " + e.getMessage());
        }

		return jsonObject;
	}

}
