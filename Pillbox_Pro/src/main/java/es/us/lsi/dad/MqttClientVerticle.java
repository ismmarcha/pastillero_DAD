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
		mqttClient.connect(1883, "192.168.1.10", s -> {
			//mqttClient.disconnect();
			System.out.print("Conectado correctamente al servidor MQTT en el puerto 1883");
			mqttClient.publish("temperature", Buffer.buffer("adios!"), MqttQoS.AT_LEAST_ONCE, false, false);
		});

	}

	@Override
	public void stop(Promise<Void> startFuture) {
		mqttClient.disconnect();
	}

}
