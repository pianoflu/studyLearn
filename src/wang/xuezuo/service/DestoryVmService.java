package wang.xuezuo.service;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.alibaba.fastjson.JSONObject;
/**
 * 虚拟机销毁web服务service接口
 * @author lijiarui
 *
 */
@WebService
public interface DestoryVmService {

	/**
	 * 销毁虚拟机
	 * @param uuid
	 * @return
	 */
	public JSONObject destoryVm(@WebParam(name="uuid") String uuid);
}
