package wang.xuezuo.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import wang.xuezuo.domain.Host;
import wang.xuezuo.domain.Vmimg;
import wang.xuezuo.domain.Vminstance;

/**
 * 获取资源信息dao
 * @author lijiarui
 *
 */
public interface ResourceCommonDao {

	/**
	 * 获取内存已使用量(M)
	 * @return
	 */
	public Integer getUsedMemory();
	
	/**
	 * 获取磁盘已使用量(M)
	 * @return
	 */
	public Integer getUsedDisk();
	
	/**
	 * 获取主机配置信息
	 * @return
	 */
	public Host getHostInfo();
	
	/**
	 * 获取虚拟机模板表信息
	 * @return
	 */
	public Vmimg getVmimgInfo(@Param("courseId") int courseId);
	
	/**
	 * 判断用户是否已存在相应虚拟机
	 * @param userId
	 * @return
	 */
	public Vminstance ifUserExistVm(@Param("userId") int userId);
	
	/**
	 * 获取虚拟机最近一次操作时间
	 * @return
	 */
	public Date getVmOperateTime(@Param("uuid") String uuid);
	
	/**
	 * 获取虚拟机模板状态
	 * @param uuid
	 * @return
	 */
	public Integer ifModelVmCanUse(@Param("courseId") int courseId);
	
	
}
