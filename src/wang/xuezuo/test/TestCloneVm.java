package wang.xuezuo.test;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.alibaba.fastjson.JSONObject;

import wang.xuezuo.service.CloneVmService;

public class TestCloneVm {
	public static void main(String[] args) {
		System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(3000000));// （单位：毫秒）  
		System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(3000000)); 
	   
		JaxWsProxyFactoryBean cm = new JaxWsProxyFactoryBean();  
	    cm.setServiceClass(CloneVmService.class);
	    cm.setAddress("http://localhost:8080/VBoxService/webservice/cloneVm");
	    CloneVmService client =(CloneVmService) cm.create();
	    
	    JSONObject result=client.cloneVm(8,1);
	    if(result.get("error") == null){
	    	String uuid = result.get("uuid").toString();
	    	String vmname = result.get("vmname").toString();
	    	System.out.println("uuid:"+ uuid + "\n vmname:" +vmname);
	    }else{
	    	System.out.println(result.get("error").toString());
	    }
	    
	    
	}
	
}
