package com.asb.reference.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.asb.reference.model.StoreScanModel;
import com.google.gson.Gson;
import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

public class StoreScanServiceImpl implements StoreScanService {

	@Autowired
	private Environment env;

	Logger log = LoggerFactory.getLogger(StoreScanServiceImpl.class);

	static final Gson GSON = new Gson();

	@Override
	public boolean queueStoreScan(StoreScanModel storeScanModel) {
		try {
			this.publishMessages(storeScanModel);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}

	private void publishMessages(StoreScanModel storeScanModel) {
		try {

			String AsbEndpoint = env.getProperty("ASB_ENDPOINT");
			String queueName = env.getProperty("ASB_QUEUENAME");

			QueueClient sendClient = new QueueClient(new ConnectionStringBuilder(AsbEndpoint, queueName),
					ReceiveMode.PEEKLOCK);
			this.sendMessagesAsync(sendClient, storeScanModel).thenRunAsync(() -> sendClient.closeAsync());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private CompletableFuture<Void> sendMessagesAsync(QueueClient sendClient, StoreScanModel storeScanModel) {

		List<CompletableFuture<?>> tasks = new ArrayList<>();

		Message message = new Message(GSON.toJson(storeScanModel, StoreScanModel.class).getBytes(UTF_8));
		message.setContentType("application/json");
		message.setLabel("StoreScan");
		message.setMessageId(String.valueOf(storeScanModel.hashCode()));
		message.setTimeToLive(Duration.ofMinutes(2));
		log.info("Message sending: Id = " + message.getMessageId());
		tasks.add(sendClient.sendAsync(message).thenRunAsync(() -> {
			log.info("Message acknowledged: Id = " + message.getMessageId());
		}));

		return CompletableFuture.allOf(tasks.toArray(new CompletableFuture<?>[tasks.size()]));
	}

}
