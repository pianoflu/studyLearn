package wang.xuezuo.test;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.alibaba.fastjson.JSONObject;

import wang.xuezuo.service.StartVmService;

public class TestStartVm {

	public static void main(String[] args) {
		JaxWsProxyFactoryBean cm = new JaxWsProxyFactoryBean();  
	    cm.setServiceClass(StartVmService.class);
	    cm.setAddress("http://localhost:8080/VBoxService/webservice/startVm");
	    StartVmService client =(StartVmService) cm.create();
	    String uuid ="37dfbd77-e514-4a0b-8ca7-021f94f6b560";
	    JSONObject result=client.startVm(uuid);
	    if(result.get("error") == null){
	    	String ip = result.get("ip").toString();
	    	System.out.println("uuid:"+ uuid + "===ip:" +ip);
	    }else{
	    	System.out.println(result.get("error").toString());
	    }
	}

}
