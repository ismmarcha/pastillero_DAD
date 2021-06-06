package es.us.lsi.dad;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.netty.handler.codec.mqtt.MqttQoS;

public class MqttClientVerticle extends AbstractVerticle {

	private MqttClient mqttClient = null;

	@Override
	public void start(Promise<Void> startFuture) {
		MqttClientOptions mqttOptions = new MqttClientOptions();
		mqttOptions.setUsername("admin1");
		mqttOptions.setPassword("123456");
		mqttClient = MqttClient.create(vertx, mqttOptions);
		mqttClient.connect(1883, "192.168.1.179", res -> {
			// mqttClient.disconnect();
			if (res.succeeded()) {
				System.out.print("Conectado correctamente al servidor MQTT en el puerto 1883");
				//DeploymentOptions options = new DeploymentOptions().setWorker(true);
				//vertx.deployVerticle(new TimeVerticle(mqttClient), options);
				mqttClient.publishHandler(s -> {
					System.out.println("There are new message in topic: " + s.topicName());
					System.out.println("Content(as string) of the message: " + s.payload().toString());
					System.out.println("QoS: " + s.qosLevel());
				}).subscribe("placa/#", 1);
				/*mqttClient.publish("placa/a8df25211e38f106b2602c3cb5da01c66616160a/move", Buffer.buffer("1"),
						MqttQoS.AT_LEAST_ONCE, false, false);*/
			} else {
				System.out.println("ERROR AL INICIAR EL CLIENTE MQTT: " + res.cause());
			}
		});

	}

	@Override
	public void stop(Promise<Void> startFuture) {
		mqttClient.disconnect();
	}

}
