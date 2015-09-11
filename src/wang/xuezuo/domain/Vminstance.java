package wang.xuezuo.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author lijiarui 说明描述: 新建虚拟机信息表(VMInstance) POJO
 */
public class Vminstance implements Serializable {

	private static final long serialVersionUID = 1L;

	// id(主键)
	private int id;
	// 用户id
	private int userId;
	// 课程id
	private int courseId;
	// 申请时间
	private Date applyTime;
	// 虚拟机uuid
	private String uuid;
	// 虚拟机名称
	private String name;
	// 虚拟机占用内存(M)
	private int memory;
	// 虚拟机占用硬盘(M)
	private int disk;
	// Cpu核个数
	private int cpu;
	// 虚拟机状态0：关闭，1：已启动
	private int status;
	//最近操作时间
	private Date operateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public int getDisk() {
		return disk;
	}

	public void setDisk(int disk) {
		this.disk = disk;
	}

	public int getCpu() {
		return cpu;
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
}
