package wang.xuezuo.sys;

import java.util.Arrays;
import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.virtualbox_5_0.IEvent;
import org.virtualbox_5_0.IEventListener;
import org.virtualbox_5_0.IEventSource;
import org.virtualbox_5_0.IGuestPropertyChangedEvent;
import org.virtualbox_5_0.IMachine;
import org.virtualbox_5_0.IVirtualBox;
import org.virtualbox_5_0.VBoxEventType;
import org.virtualbox_5_0.VBoxException;
import org.virtualbox_5_0.VirtualBoxManager;

import wang.xuezuo.commons.VboxCommon;
import wang.xuezuo.dao.ResourceCommonDao;

/**
 * 虚拟机长时间不操作关机任务
 * @author lijiarui
 *
 */
public class SuspendVmTask extends TimerTask {

	private static final Logger logger = Logger.getLogger(SuspendVmTask.class);
	private static boolean isRunning = false;
	private static VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);
	private static IVirtualBox vbox = null;
	
	// 设置虚拟机空闲时间 ms
	private static long idleTime = 0;

	public static void setIdleTime(long idleTime) {
		SuspendVmTask.idleTime = idleTime;
	}

	@Autowired
	private ResourceCommonDao resourceCommonDao;

	SuspendVmTask() {
		// 连接虚拟机webservice
		try {
			vbox = VboxCommon.connectVm(mgr);
		} catch (VBoxException e) {
			logger.error("虚拟机webserver未开启");
		}
	}

	@Override
	public void run() {
		if (!isRunning) {
			isRunning = true;
			logger.info("挂起操作:开始监听事件...");
			// 监听事件
			try {
				if (vbox != null) {
					IEventSource es = vbox.getEventSource();
					IEventListener listener = es.createListener();
					es.registerListener(listener, Arrays.asList(VBoxEventType.Any), false);
					try {
						while (true) {
							IEvent ev = es.getEvent(listener, 1000);
							if (ev != null) {
								// System.out.println("type:" + ev.getType());
								if (ev.getType() == VBoxEventType.OnGuestPropertyChanged) {
									IGuestPropertyChangedEvent gpce = IGuestPropertyChangedEvent.queryInterface(ev);
									System.out.println(
											gpce.getMachineId() + ":" + gpce.getName() + ":" + gpce.getValue());
									if ("Idle".equals(gpce.getValue())) {
										String uuid = gpce.getMachineId();
										Date date = resourceCommonDao.getVmOperateTime(uuid);
										if(date != null){
											long betweenTime = (new Date()).getTime() - date.getTime();
											if (betweenTime > idleTime) {
												IMachine machine = vbox.findMachine(uuid);
												VboxCommon.poweroffVm(mgr, machine);
												logger.info("machineName:"+machine.getName()+"被挂起任务关闭");
											}
										}
									}
									System.out.println("============================================");
								}
								es.eventProcessed(listener, ev);
							}
							mgr.waitForEvents(0);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					es.unregisterListener(listener);
				}
			} catch (VBoxException e) {
				logger.error("VBoxException error: " + e.getMessage() + ".Error cause: " + e.getCause());
			} catch (Exception e) {
				logger.error("error: " + e.getMessage());
			}
			// 释放连接
			logger.info("挂起操作:结束监听事件...");
			isRunning = false;
		}

	}

}
