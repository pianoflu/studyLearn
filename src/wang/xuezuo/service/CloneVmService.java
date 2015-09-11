package wang.xuezuo.service;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.alibaba.fastjson.JSONObject;

/**
 * 虚拟机克隆service接口
 * @author lijiarui
 *
 */
@WebService
public interface CloneVmService {
	
	/**
	 * 虚拟机克隆操作
	 * @param userId
	 * @param courseId
	 * @return
	 */
	public JSONObject cloneVm(@WebParam(name="userId") int userId,@WebParam(name="courseId") int courseId);
}
