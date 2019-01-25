package com.asb.reference.listener;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asb.reference.model.StoreScanModel;
import com.google.gson.Gson;
import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.MessageHandlerOptions;
import com.microsoft.azure.servicebus.QueueClient;

@Service
public class AsbListener {

	Logger log = LoggerFactory.getLogger(AsbListener.class);
	static final Gson GSON = new Gson();
	
	ExecutorService executorService;

	@Autowired
	QueueClient queueClient;
	
	//Part of JEE 6 Spec. This method is executed after context initialization.
	@PostConstruct
	public void initIt() throws Exception {
		log.info("Init method ::: get Queue Name : " + queueClient.getQueueName());
		this.executorService = Executors.newSingleThreadExecutor();
        this.registerReceiver(queueClient, executorService);
	}
	
	@PreDestroy
	public void cleanUp() throws Exception {
		// Shutdown the receiver and executor service
		queueClient.close();
        executorService.shutdown();
	}
	

	//This method acts as onMessage listener for ASB Queue
	private void registerReceiver(QueueClient queueClient, ExecutorService executorService) throws Exception {

		// register the RegisterMessageHandler callback with executor service
		queueClient.registerMessageHandler(new IMessageHandler() {
			// callback invoked when the message handler loop has obtained a
			// message
			public CompletableFuture<Void> onMessageAsync(IMessage message) {
				// receives message is passed to callback
				if (message.getLabel() != null && message.getContentType() != null
						&& message.getLabel().contentEquals("StoreScan")
						&& message.getContentType().contentEquals("application/json")) {

					byte[] body = message.getBody();
					StoreScanModel storeScanModel = GSON.fromJson(new String(body, UTF_8), StoreScanModel.class);
					log.info(" *****************************************************************************");
					log.info("Message ID: " + message.getMessageId());
					log.info("Message SequenceNumber: " + message.getSequenceNumber());
					log.info("Queued Timestamp: " + message.getEnqueuedTimeUtc());
					log.info("Expired Timestamp: " + message.getExpiresAtUtc());
					log.info("Content-type: " + message.getContentType());
					if (null != storeScanModel) {
						log.info("Gtin: " + storeScanModel.getGtin() + "\tLotNo#: " + storeScanModel.getLotNo() + "\tStoreId: "+storeScanModel.getStoreId());
					} else {
						log.info("storeScanModel object is null");
					}
					log.info(" *****************************************************************************");
				}
				return CompletableFuture.completedFuture(null);
			}

			// callback invoked when the message handler has an exception to
			// report
			public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
				log.warn(exceptionPhase + "-" + throwable.getMessage());
			}
		},
				// 1 concurrent call, messages are auto-completed, auto-renew
				// duration
				new MessageHandlerOptions(1, true, Duration.ofMinutes(1)), executorService);

	}
}
