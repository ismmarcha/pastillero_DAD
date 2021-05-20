package es.us.lsi.dad;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttQoS;

public class MqttClientVerticle extends AbstractVerticle {

	private MqttClient mqttClient = null;

	@Override
	public void start(Promise<Void> startFuture) {
		MqttClientOptions mqttOptions = new MqttClientOptions();
		mqttOptions.setUsername("admin1");
		mqttOptions.setPassword("123456");
		mqttClient = MqttClient.create(vertx, mqttOptions);
		mqttClient.connect(1883, "192.168.1.10", res -> {
			// mqttClient.disconnect();
			System.out.print("Conectado correctamente al servidor MQTT en el puerto 1883");
			mqttClient.publishHandler(s -> {
				System.out.println("There are new message in topic: " + s.topicName());
				System.out.println("Content(as string) of the message: " + s.payload().toString());
				System.out.println("QoS: " + s.qosLevel());
			}).subscribe("placa/#", 1);
			mqttClient.publish("placa/a8df25211e38f106b2602c3cb5da01c66616160a/move", Buffer.buffer("1"),
					MqttQoS.AT_LEAST_ONCE, false, false);
		});

	}

	@Override
	public void stop(Promise<Void> startFuture) {
		mqttClient.disconnect();
	}

}
