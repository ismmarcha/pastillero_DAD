package es.us.lsi.dad;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttQoS;

public class MqttServerVerticle extends AbstractVerticle {

	private MqttServer mqttServer = null;

	@Override
	public void start(Promise<Void> startFuture) {
		mqttServer = MqttServer.create(vertx);
		mqttServer.endpointHandler(endpoint -> {
			System.out.println("Cliente conectado");
			endpoint.disconnectHandler(h -> {
				System.out.println("Cliente desconectado");
			});
			endpoint.subscribeHandler(subscribe -> {
				List<MqttQoS> grantedQosLevels = new ArrayList<>();
				for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
					System.out.println("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
					grantedQosLevels.add(s.qualityOfService());
				}
				// ack the subscriptions request
				endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
			});

			endpoint.unsubscribeHandler(unsubscribe -> {

				for (String t : unsubscribe.topics()) {
					System.out.println("Unsubscription for " + t);
				}
				// ack the subscriptions request
				endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
			});

			endpoint.publishHandler(message -> {

				System.out.println("Just received message [" + message.payload().toString(Charset.defaultCharset())
						+ "] with QoS [" + message.qosLevel() + "]");
				if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
					endpoint.publishAcknowledge(message.messageId());
				} else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
					endpoint.publishReceived(message.messageId());
				}

			}).publishReleaseHandler(messageId -> {
				endpoint.publishComplete(messageId);
			});

			// accept connection from the remote client
			endpoint.accept(false);
		}).listen(4500, v -> {
			if (v.succeeded()) {
				System.out.println("Servidor iniciado en el puerto " + v.result().actualPort());
			} else {
				System.out.println("ERROR INICIANDO SERVIDOR MQTT. CAUSA: " + v.cause());
			}
		});
	}

	@Override
	public void stop(Promise<Void> startFuture) {
		mqttServer.close();
	}

}
