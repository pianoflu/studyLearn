package wang.xuezuo.service;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.alibaba.fastjson.JSONObject;

/**
 * 虚拟机重启web服务service接口
 * @author lijiarui
 *
 */
@WebService
public interface ResetVmService {

	public JSONObject resetVm(@WebParam(name="uuid") String uuid);
}
