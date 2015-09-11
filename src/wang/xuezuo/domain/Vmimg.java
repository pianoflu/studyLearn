package wang.xuezuo.domain;

import java.io.Serializable;

/**
 * 
 * @author lijiarui 说明描述: 虚拟机模板表(VMImg) POJO
 */
public class Vmimg implements Serializable {

	private static final long serialVersionUID = 1L;
	// id(主键)
	private int id;
	// 课程id
	private int courseId;
	// 课程名称
	private String courseName;
	// 虚拟机名称
	private String name;
	// 虚拟机uuid
	private String uuid;
	// 虚拟机占用内存(M)
	private int memory;
	// 虚拟机占用硬盘(M)
	private int disk;
	// cpu核个数
	private int cpu;
	//使用状态 0：已停用，1：已启用
	private int status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

}
