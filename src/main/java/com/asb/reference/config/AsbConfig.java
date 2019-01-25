package com.asb.reference.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

@Configuration
public class AsbConfig {
	
	
	Logger log = LoggerFactory.getLogger(AsbConfig.class);
	
	@Autowired
	private Environment env;
	
	//Create ASB Queue Client for listener. This is autowired to Service class and used in the Post-Contruct method.
	@Bean
	public QueueClient getQueueClient(){
		
		log.info("Inside getQueueClient");
		try{
			log.info("Endpoint: "+env.getProperty("ASB_ENDPOINT"));
			log.info("Queue Name: "+env.getProperty("ASB_QUEUENAME"));
			
			String AsbEndpoint = env.getProperty("ASB_ENDPOINT");
			String queueName = env.getProperty("ASB_QUEUENAME");
			
			if(null != AsbEndpoint && !AsbEndpoint.isEmpty() && null != queueName && !queueName.isEmpty()){
				QueueClient receiveClient = new QueueClient(new ConnectionStringBuilder(AsbEndpoint, queueName), ReceiveMode.PEEKLOCK);
				return receiveClient;
			} else {
				return null;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		
		
		
	}

}
