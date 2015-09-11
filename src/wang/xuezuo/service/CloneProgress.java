package wang.xuezuo.service;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.alibaba.fastjson.JSONObject;

/**
 * 虚拟机克隆进度返回接口
 * @author lijiarui
 *
 */
@WebService
public interface CloneProgress {

	public JSONObject ProgressBar(@WebParam(name="uuid") String uuid);
}
