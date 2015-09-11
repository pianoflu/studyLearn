package wang.xuezuo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import wang.xuezuo.domain.Vminstance;

/**
 * 销毁虚拟机操作Dao层
 * @author lijiarui
 *
 */
public interface DestoryVmDao {

	/**
	 * 销毁虚拟机
	 * @param uuid
	 */
	public void deleteVm(@Param("uuid") String uuid);
	
	/**
	 * 查询出所有超时时间  限时overTime    单位:小时
	 * @return
	 */
	public List<Vminstance> findOverTimeVm(@Param("overTime") int overTime);
}
