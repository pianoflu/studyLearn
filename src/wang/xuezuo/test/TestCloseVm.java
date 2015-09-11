package wang.xuezuo.test;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.alibaba.fastjson.JSONObject;

import wang.xuezuo.service.CloseVmService;

public class TestCloseVm {

	public static void main(String[] args) {
		JaxWsProxyFactoryBean cm = new JaxWsProxyFactoryBean();  
	    cm.setServiceClass(CloseVmService.class);
	    cm.setAddress("http://localhost:8080/VBoxService/webservice/closeVm");
	    CloseVmService client =(CloseVmService) cm.create();
	    String uuid ="37dfbd77-e514-4a0b-8ca7-021f94f6b560";
	    JSONObject result=client.closeVm(uuid);
	    if(result.get("error") == null){
	    	System.out.println("uuid:"+ uuid + "===:");
	    }else{
	    	System.out.println(result.get("error").toString());
	    }
	}

}
