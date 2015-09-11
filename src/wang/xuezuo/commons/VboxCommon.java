package wang.xuezuo.commons;

import java.util.ArrayList;
import java.util.List;

import org.virtualbox_5_0.CleanupMode;
import org.virtualbox_5_0.CloneMode;
import org.virtualbox_5_0.CloneOptions;
import org.virtualbox_5_0.IConsole;
import org.virtualbox_5_0.IMachine;
import org.virtualbox_5_0.IMedium;
import org.virtualbox_5_0.IProgress;
import org.virtualbox_5_0.ISession;
import org.virtualbox_5_0.IVirtualBox;
import org.virtualbox_5_0.IVirtualBoxErrorInfo;
import org.virtualbox_5_0.LockType;
import org.virtualbox_5_0.MachineState;
import org.virtualbox_5_0.VirtualBoxManager;

/**
 * 虚拟机通用类
 * 
 * @author lijiarui
 *
 */
public class VboxCommon {
	// 连接虚拟机的url
	private static String url = null;
	// 主机用户名
	private static String username = null;
	// 主机密码
	private static String password = null;
	// 设置虚拟机空闲时间  ms
	private static String idleTime = null;
	
	/**
	 * 进度控制
	 * 
	 * @param mgr
	 * @param p
	 * @param waitMillis
	 *            等待时间
	 */
	public static boolean progressBar(VirtualBoxManager mgr, IProgress p, long waitMillis) {
		long end = System.currentTimeMillis() + waitMillis;
		while (!p.getCompleted()) {
			mgr.waitForEvents(0);
			System.out.println("." + p.getPercent());
			if (System.currentTimeMillis() >= end)
				return false;
		}
		if (p.getCompleted()) {
			System.out.println("finish:" + p.getResultCode());
			IVirtualBoxErrorInfo info = p.getErrorInfo();
			if (info != null)
				System.out.println(info.getText());
		}
		return true;
	}
	
	public static boolean cloneProgressBar(VirtualBoxManager mgr, IProgress p, long waitMillis){
		long end = System.currentTimeMillis() + waitMillis;
		while (!p.getCompleted()) {
//			String url ="http://localhost:8080/WebTest/abc";
//			System.out.println("." + p.getPercent());
//			HttpClient httpclient = new HttpClient();
//        	PostMethod postMethod = new PostMethod(url);
//        	NameValuePair[] datas = {
//					new NameValuePair("type", "clone"),
//					new NameValuePair("precent", ""+p.getPercent()+"")};
//			postMethod.setRequestBody(datas);
//        	try {
//				httpclient.executeMethod(postMethod);
//				//System.out.println("responseCode:"+responseCode);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
        	mgr.waitForEvents(0);
			if (System.currentTimeMillis() >= end)
				return false;
		}
		if (p.getCompleted()) {
			System.out.println("finish:" + p.getResultCode());
			IVirtualBoxErrorInfo info = p.getErrorInfo();
			if (info != null)
				System.out.println(info.getText());
		}
		return true;
	}

	/**
	 * 连接到虚拟机webservice
	 */
	public static IVirtualBox connectVm(VirtualBoxManager mgr) {
		mgr.connect(url, username, password);
		IVirtualBox vbox = mgr.getVBox();
		return vbox;
	}
	
	/**
	 * 释放虚拟机连接
	 * @param mgr
	 */
	public static void disconnectVm(VirtualBoxManager mgr){
		mgr.waitForEvents(0);
        mgr.disconnect();
        mgr.cleanup();
	}

	/**
	 * 虚拟机克隆操作
	 * 
	 * @param mgr
	 * @param vbox
	 * @param modelVmName
	 *            虚拟机模板名称
	 * @param newVmName
	 *            新建虚拟机名称
	 */
	public static String cloneVm(VirtualBoxManager mgr, IVirtualBox vbox, String modelVmName, String newVmName) {
		IMachine oldMachine = vbox.findMachine(modelVmName);
		String settingsFile = vbox.composeMachineFilename(newVmName, null, null, "D:\\VirtualLabs\\VMInstance");
		IMachine newMachine = vbox.createMachine(settingsFile, newVmName, null, oldMachine.getOSTypeId(), null);
		//IMachine newMachine = vbox.createMachine("", newVmName, null, oldMachine.getOSTypeId(), null);
		List<CloneOptions> options = new ArrayList<CloneOptions>();
		options.add(CloneOptions.KeepNATMACs);
		IProgress prog = oldMachine.cloneTo(newMachine, CloneMode.MachineState, options);
		cloneProgressBar(mgr, prog, 240000);
		newMachine.setGuestProperty("/VirtualBox/GuestAdd/VBoxService/--vminfo-user-idle-threshold", idleTime,"RDONLYGUEST");
		newMachine.saveSettings();
		vbox.registerMachine(newMachine);
		mgr.waitForEvents(0);
		return newMachine.getId();
	}

	/**
	 * 销毁虚拟机(已知虚拟机已注册)
	 * @param mgr
	 * @param machine
	 */
	public static void deleteVm(VirtualBoxManager mgr, IMachine machine){
		//如果虚拟机未关闭则不能销毁，所以需要先关机
		if (machine.getState() != MachineState.PoweredOff){
			ISession session = mgr.getSessionObject();
			machine.lockMachine(session, LockType.Shared);
			IConsole console = session.getConsole();
			IProgress prog = console.powerDown();
			progressBar(mgr, prog, 10000);
			session.unlockMachine();
			mgr.waitForEvents(0);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//销毁
		List<IMedium> mediums = machine.unregister(CleanupMode.Full);
		IProgress progress = machine.deleteConfig(mediums);
		progressBar(mgr, progress, 100000);
		mgr.waitForEvents(0);
	}
	
	/**
	 * 删除克隆虚拟机失败时的虚拟机
	 * @param mgr
	 * @param vbox
	 * @param vmName
	 */
	public static void deleteCloneVm(VirtualBoxManager mgr, IVirtualBox vbox, String vmName) {
		List<IMachine> machineList = vbox.getMachines();
		boolean flag = false;
		for (int i = 0; i < machineList.size(); i++) {
			if (vmName.equals(machineList.get(i).getName())) {
				flag = true;
				break;
			}
		}
		if (flag) {
			IMachine machine = vbox.findMachine(vmName);
			List<IMedium> mediums = machine.unregister(CleanupMode.Full);
			IProgress progress = machine.deleteConfig(mediums);
			progressBar(mgr, progress, 100000);
		}
		mgr.waitForEvents(0);
	}

	/**
	 * 开启虚拟机操作
	 * 
	 * @param mgr
	 * @param machine
	 */
	public static String startVm(VirtualBoxManager mgr, IMachine machine) {
		ISession session = mgr.getSessionObject();
		IProgress p = machine.launchVMProcess(session, "gui", "");
		p.waitForCompletion(-1);
		String ip = null;
		if (p.getResultCode() == 0) { // check success
			//需要虚拟机开启一段时间才能获取ip
			while("".equals(ip) || ip == null){
				ip = machine.getGuestPropertyValue("/VirtualBox/GuestInfo/Net/0/V4/IP");
			}
		}
		mgr.waitForEvents(0);
		return ip;
	}

	/**
	 * 关闭虚拟机
	 * @param mgr
	 * @param machine
	 */
	public static void poweroffVm(VirtualBoxManager mgr, IMachine machine) {
		ISession session = mgr.getSessionObject();
		machine.lockMachine(session, LockType.Shared);
		IConsole console = session.getConsole();
		IProgress progress = console.powerDown();
		progressBar(mgr, progress, 10000);
		progress.waitForCompletion(-1);
		session.unlockMachine();
		mgr.waitForEvents(0);
	}
	
	/**
	 * 重启虚拟机
	 * @param mgr
	 * @param machine
	 */
	public static void resetVm(VirtualBoxManager mgr, IMachine machine){
    	ISession session = mgr.getSessionObject();
    	machine.lockMachine(session, LockType.Shared);
    	//IMachine mutable = session.getMachine();
    	IConsole console = session.getConsole();
    	System.out.println(machine.getState());
    	console.reset();
    	//mutable.saveSettings();
    	session.unlockMachine();
    	mgr.waitForEvents(0);
	}
	
	public static void setUrl(String url) {
		VboxCommon.url = url;
	}

	public static void setUsername(String username) {
		VboxCommon.username = username;
	}

	public static void setPassword(String password) {
		VboxCommon.password = password;
	}

	public static void setIdleTime(String idleTime) {
		VboxCommon.idleTime = idleTime;
	}

}
