package com.fspt.asb.reference.pub;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asb.reference.listener.AsbListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

public class PublishToAsbQueue {
	
	 
	 Logger log = LoggerFactory.getLogger(AsbListener.class);
	
	 static final Gson GSON = new Gson();
	 
	 //private static final String CONNECTION ="Endpoint=sb://abanik-asb1.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=TEKfekGRDUqECxtJhJihw1lhBk1zob/FT1aAtnpUy/o=";
	 
	 private static final String CONNECTION ="Endpoint=sb://asb-fspt-dev1-dmz.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=X3BdVMu8GVDkVxogPt4/HAAjmAPM0fKShfEoru8pIP8=";
	 
	 //private static final String QUEUE_NAME = "abanik-asb1-queue1";
	 private static final String QUEUE_NAME = "queue1-dev1-dmz-fspt";
	
	 public static void main(String[] args) {
		 PublishToAsbQueue publishToAsbQueue = new PublishToAsbQueue();
		 publishToAsbQueue.publishMessages();
		 //System.exit(0);
	 }
	 
	 public void publishMessages(){
		 try{
			 
			 QueueClient sendClient = new QueueClient(new ConnectionStringBuilder(CONNECTION, QUEUE_NAME), ReceiveMode.PEEKLOCK);
		     this.sendMessagesAsync(sendClient).thenRunAsync(() -> sendClient.closeAsync());
			 
		 }catch(Exception ex){
			 ex.printStackTrace();
		 }
	 }
	 
	 private CompletableFuture<Void> sendMessagesAsync(QueueClient sendClient) {
	        List<HashMap<String, String>> data =
	                GSON.fromJson(
	                        "[" +
	                                "{'name' = 'Einstein', 'firstName' = 'Albert'}," +
	                                "{'name' = 'Heisenberg', 'firstName' = 'Werner'}," +
	                                "{'name' = 'Curie', 'firstName' = 'Marie'}," +
	                                "{'name' = 'Hawking', 'firstName' = 'Steven'}," +
	                                "{'name' = 'Newton', 'firstName' = 'Isaac'}," +
	                                "{'name' = 'Bohr', 'firstName' = 'Niels'}," +
	                                "{'name' = 'Faraday', 'firstName' = 'Michael'}," +
	                                "{'name' = 'Galilei', 'firstName' = 'Galileo'}," +
	                                "{'name' = 'Kepler', 'firstName' = 'Johannes'}," +
	                                "{'name' = 'Kopernikus', 'firstName' = 'Nikolaus'}" +
	                                "]",
	                        new TypeToken<List<HashMap<String, String>>>() {}.getType());

	        List<CompletableFuture<?>> tasks = new ArrayList<>();
	        for (int i = 0; i < data.size(); i++) {
	            final String messageId = Integer.toString(i);
	            Message message = new Message(GSON.toJson(data.get(i), Map.class).getBytes(UTF_8));
	            message.setContentType("application/json");
	            message.setLabel("Scientist");
	            message.setMessageId(messageId);
	            message.setTimeToLive(Duration.ofMinutes(2));
	            log.info("Message sending: Id = "+message.getMessageId());
	            tasks.add(
	                    sendClient.sendAsync(message).thenRunAsync(() -> {
	                        log.info("Message acknowledged: Id = "+message.getMessageId());
	                    }));
	        }
	        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture<?>[tasks.size()]));
	    }

	 

}
