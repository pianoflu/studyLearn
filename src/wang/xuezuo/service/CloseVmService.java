package wang.xuezuo.service;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.alibaba.fastjson.JSONObject;

/**
 * 虚拟机关闭web服务service接口
 * @author lijiarui
 *
 */
@WebService
public interface CloseVmService {
	/**
	 * 关闭虚拟机
	 * @param uuid
	 * @return
	 */
	public JSONObject closeVm(@WebParam(name="uuid") String uuid);
}
