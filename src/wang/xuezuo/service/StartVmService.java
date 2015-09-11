package wang.xuezuo.service;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.alibaba.fastjson.JSONObject;

/**
 * 虚拟机开启web服务service接口
 * @author lijiarui
 *
 */
@WebService
public interface StartVmService {
	/**
	 * 启动虚拟机操作
	 * @param uuid
	 * @return
	 */
	public JSONObject startVm(@WebParam(name="uuid") String uuid);
}
