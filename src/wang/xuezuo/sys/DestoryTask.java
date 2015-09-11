package wang.xuezuo.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.virtualbox_5_0.IMachine;
import org.virtualbox_5_0.IVirtualBox;
import org.virtualbox_5_0.VBoxException;
import org.virtualbox_5_0.VirtualBoxManager;

import wang.xuezuo.commons.VboxCommon;
import wang.xuezuo.dao.DestoryVmDao;
import wang.xuezuo.domain.Vminstance;
/**
 * 虚拟机定时销毁任务
 * @author lijiarui
 *
 */
public class DestoryTask extends TimerTask{

private static boolean isRunning = false;
	
	private static final Logger logger = Logger.getLogger(DestoryTask.class);
	//新建虚拟机超时销毁时间 单位 : h
	private static int overTime = 5;
	
	public static void setOverTime(int overTime) {
		DestoryTask.overTime = overTime;
	}

	@Autowired
	private DestoryVmDao destoryVmDao;
	
	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
			logger.info("开始执行定时销毁任务...");
			List<Vminstance> vms = new ArrayList<Vminstance>();
			vms = destoryVmDao.findOverTimeVm(overTime);
			if(vms.size() > 0){
				VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);
    			IVirtualBox vbox = null;
    			try {
    				vbox = VboxCommon.connectVm(mgr);
    			} catch (VBoxException e) {
    				logger.error("定时销毁时虚拟机webserver未开启");
    			}
				for(int i = 0;i<vms.size();i++){
        			//销毁操作
        			try {
        				if (vbox != null) {
        					IMachine machine = vbox.findMachine(vms.get(i).getUuid());
        					VboxCommon.deleteVm(mgr, machine);
        					logger.info("machineName:"+machine.getName()+"被定时销毁...");
        				}
        			} catch (VBoxException e) {
        				logger.error("VBoxException error: " + e.getMessage() + ".Error cause: " + e.getCause());
        			} catch (Exception e) {
        				logger.error("定时销毁时销毁操作失败: " + e.getMessage());
        			}
        			//删除虚拟机信息
        			try {
        				destoryVmDao.deleteVm(vms.get(i).getUuid());
        			} catch (Exception e) {
        				logger.error("定时销毁时删除虚拟机信息失败: " + e.getMessage());
        			}
				}
				//释放连接
    			try {
    				VboxCommon.disconnectVm(mgr);
    	        } catch (VBoxException e) {
    	        	logger.error("定时销毁时释放连接失败: " + e.getMessage());
    	        }
			}
			logger.info("执行定时销毁任务完成...");
			isRunning = false;
	}
		}

}
