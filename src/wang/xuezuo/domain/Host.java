package wang.xuezuo.domain;

import java.io.Serializable;

/**
 * 
 * @author lijiarui 说明描述: 主机信息表（Host） POJO
 */
public class Host implements Serializable {

	private static final long serialVersionUID = 1L;

	// 主机总内存(M)
	private int memory;
	// 主机总硬盘(M)
	private int disk;
	// 主机cpu核心数
	private int cpu;
	// 预警内存(M)
	private int memoryWarningLimit;
	// 内存使用上限(M)
	private int memoryToplimit;
	// 预警硬盘(M)
	private int diskWarningLimit;
	// 硬盘使用上限(M)
	private int diskToplimit;
	// cpu预警使用率
	private double cpuUsageWarningLimit;
	// cpu使用率上限
	private double cpuUsageToplimit;

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

	public int getMemoryWarningLimit() {
		return memoryWarningLimit;
	}

	public void setMemoryWarningLimit(int memoryWarningLimit) {
		this.memoryWarningLimit = memoryWarningLimit;
	}

	public int getMemoryToplimit() {
		return memoryToplimit;
	}

	public void setMemoryToplimit(int memoryToplimit) {
		this.memoryToplimit = memoryToplimit;
	}

	public int getDiskWarningLimit() {
		return diskWarningLimit;
	}

	public void setDiskWarningLimit(int diskWarningLimit) {
		this.diskWarningLimit = diskWarningLimit;
	}

	public int getDiskToplimit() {
		return diskToplimit;
	}

	public void setDiskToplimit(int diskToplimit) {
		this.diskToplimit = diskToplimit;
	}

	public double getCpuUsageWarningLimit() {
		return cpuUsageWarningLimit;
	}

	public void setCpuUsageWarningLimit(double cpuUsageWarningLimit) {
		this.cpuUsageWarningLimit = cpuUsageWarningLimit;
	}

	public double getCpuUsageToplimit() {
		return cpuUsageToplimit;
	}

	public void setCpuUsageToplimit(double cpuUsageToplimit) {
		this.cpuUsageToplimit = cpuUsageToplimit;
	}
}
