package wang.xuezuo.dao;

import org.apache.ibatis.annotations.Param;

/**
 * 关闭虚拟机操作Dao层
 * @author lijiarui
 *
 */
public interface CloseVmDao {

	/**
	 * 更新虚拟机状态
	 * @param status 0：关闭，1：已启动
	 * @param uuid
	 */
	public void updateVmStatus(@Param("status") int status,@Param("uuid") String uuid);
}
