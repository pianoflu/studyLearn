package wang.xuezuo.service;

import java.util.Date;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.virtualbox_5_0.IVirtualBox;
import org.virtualbox_5_0.VBoxException;
import org.virtualbox_5_0.VirtualBoxManager;

import com.alibaba.fastjson.JSONObject;

import wang.xuezuo.commons.Utils;
import wang.xuezuo.commons.VboxCommon;
import wang.xuezuo.dao.CloneVmDao;
import wang.xuezuo.dao.ResourceCommonDao;
import wang.xuezuo.domain.Host;
import wang.xuezuo.domain.Vmimg;
import wang.xuezuo.domain.Vminstance;

/**
 * 虚拟机克隆service实现
 * 
 * @author lijiarui
 *
 */
@Service
@WebService(endpointInterface = "wang.xuezuo.service.CloneVmService")
public class CloneVmServiceImpl implements CloneVmService {

	private static final Logger logger = Logger.getLogger(CloneVmServiceImpl.class);

	@Autowired
	private CloneVmDao cloneVmDao;

	@Autowired
	private ResourceCommonDao resourceCommonDao;

	@Override
	public JSONObject cloneVm(int userId, int courseId) {
		logger.info("userId:"+userId+"申请资源,"+"courseId:"+courseId);
		JSONObject jsonObject = new JSONObject();
		Vmimg vmimg = resourceCommonDao.getVmimgInfo(courseId);

		if (!ifUserExistVm(userId)) {
			if(resourceCommonDao.ifModelVmCanUse(courseId) == 1){
				if (ifCanCloneVm(courseId)) {
					// 连接虚拟机webservice
					VirtualBoxManager mgr = VirtualBoxManager.createInstance(null);
					IVirtualBox vbox = null;
					try {
						vbox = VboxCommon.connectVm(mgr);
					} catch (VBoxException e) {
						logger.error("虚拟机webserver未开启");
						jsonObject.put("error", "服务器出错");
					}
					// 克隆操作
					String newVmName = createNewVmName(userId, courseId);
					String uuid = null;
					try {
						if (vbox != null) {
							uuid = VboxCommon.cloneVm(mgr, vbox, vmimg.getName(), newVmName);
						}
					} catch (VBoxException e) {
						logger.error(uuid+":VBoxException error: " + e.getMessage() + ".Error cause: " + e.getCause());
						VboxCommon.deleteCloneVm(mgr, vbox, newVmName);
						jsonObject.put("error", "申请资源失败");
					} catch (Exception e) {
						logger.error(uuid+":克隆操作失败: " + e.getMessage());
						VboxCommon.deleteCloneVm(mgr, vbox, newVmName);
						jsonObject.put("error", "申请资源失败");
					}
					//释放连接
					try {
						VboxCommon.disconnectVm(mgr);
			        } catch (VBoxException e) {
			        	logger.error(uuid+":释放连接失败: " + e.getMessage());
			        }
					// 数据库操作
					try {
						Vminstance cloneInfo = new Vminstance();
						cloneInfo.setUserId(userId);
						cloneInfo.setCourseId(courseId);
						cloneInfo.setApplyTime(new Date());
						cloneInfo.setUuid(uuid);
						cloneInfo.setName(newVmName);
						cloneInfo.setMemory(vmimg.getMemory());
						cloneInfo.setDisk(vmimg.getDisk());
						cloneInfo.setCpu(vmimg.getCpu());
						cloneInfo.setStatus(0);
						cloneVmDao.addVminstanceInfo(cloneInfo);
						jsonObject.put("uuid", uuid);
						jsonObject.put("vmname", newVmName);
					} catch (Exception e) {
						logger.error(uuid+":克隆数据存入数据库失败: " + e.getMessage());
						VboxCommon.deleteCloneVm(mgr, vbox, newVmName);
						jsonObject.put("error", "申请资源失败");
						logger.info("userId:"+userId+"申请资源失败");
					}
				} else {
					jsonObject.put("error", "对不起，服务器资源不足");
					logger.info("userId:"+userId+"服务器资源不足");
				}
			}else{
				jsonObject.put("error", "该资源不可用");
				logger.info("userId:"+userId+"虚拟机模板不可用");
			}
		} else {
			jsonObject.put("error", "您已申请过课程");
			logger.info("userId:"+userId+"已申请过课程");
		}
		logger.info("userId:"+userId+"结束cloneVm方法");
		return jsonObject;
	}

	/**
	 * 判断是否可以克隆
	 */
	public boolean ifCanCloneVm(int courseId) {
		Host hostInfo = resourceCommonDao.getHostInfo();
		Vmimg vmimg = resourceCommonDao.getVmimgInfo(courseId);
		int usedDisk = Utils.getusedSpaceForWindows();
		double memoryTopLimitRate = (double) hostInfo.getMemoryToplimit() / (double) hostInfo.getMemory();
		int totalUsedDisk = usedDisk + vmimg.getDisk();
		logger.info("当前内存使用率:"+Utils.getMemeryUsedRateForWindows()+"当前占用磁盘:"+totalUsedDisk);
		if (Utils.getMemeryUsedRateForWindows() > memoryTopLimitRate || totalUsedDisk > hostInfo.getDiskToplimit()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断此用户是否已注册过虚拟机
	 */
	public boolean ifUserExistVm(int userId) {
		Vminstance vminstance = resourceCommonDao.ifUserExistVm(userId);
		if (vminstance == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 为新虚拟机创建名称 命名规则：userId + courseId 可以唯一确定一个name
	 */
	public String createNewVmName(int userId, int courseId) {
		return String.valueOf(userId) + String.valueOf(courseId);
	}
}