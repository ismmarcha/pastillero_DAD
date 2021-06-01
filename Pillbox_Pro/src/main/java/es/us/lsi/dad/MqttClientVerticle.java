package es.us.lsi.dad;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.netty.handler.codec.mqtt.MqttQoS;

public class MqttClientVerticle extends AbstractVerticle {

	boolean dosisEditadas = false;
	boolean succeded = false;

	public MqttClient mqttClient = null;

	@Override
	public void start(Promise<Void> startFuture) {
		enviaActuDosis();
		/*
		 * MqttClientOptions mqttOptions = new MqttClientOptions();
		 * mqttOptions.setUsername("admin1"); mqttOptions.setPassword("123456");
		 * mqttClient = MqttClient.create(vertx, mqttOptions);
		 * /*mqttClient.connect(1883, "192.168.1.177", res -> { //
		 * mqttClient.disconnect(); if (res.succeeded()) {
		 * 
		 * System.out.print("Conectado correctamente al servidor MQTT en el puerto 1883"
		 * ); // DeploymentOptions options = new DeploymentOptions().setWorker(true); //
		 * vertx.deployVerticle(new TimeVerticle(mqttClient), options);
		 * mqttClient.publishHandler(s -> {
		 * System.out.println("There are new message in topic: " + s.topicName());
		 * System.out.println("Content(as string) of the message: " +
		 * s.payload().toString()); System.out.println("QoS: " + s.qosLevel());
		 * }).subscribe("placa/actuDosis", 1);
		 * 
		 * } else { System.out.println("ERROR AL INICIAR EL CLIENTE MQTT: " +
		 * res.cause()); } });
		 */
	}

	public void enviaActuDosis() {
		
		MessageConsumer<String> consumer = vertx.eventBus().consumer("enviaActuDosis");
		consumer.handler(message -> {

			String id_pastillero = message.body();	
			System.out.println("placa/" + id_pastillero + "/actuDosis");

			MqttClientOptions mqttOptions = new MqttClientOptions();

			mqttOptions.setUsername("admin1");
			mqttOptions.setPassword("123456");
			mqttClient = MqttClient.create(vertx, mqttOptions);

			System.out.println(vertx);

			mqttClient.exceptionHandler(e -> {
				System.out.println(e.getCause());
			});

			mqttClient.connect(1883, "192.168.1.177", res -> {
				if (res.succeeded()) {
					System.out.print("Conectado correctamente al servidor MQTT en el puerto 1883");

					mqttClient.publish("placa/" + id_pastillero + "/actuDosis", Buffer.buffer("1"),
							MqttQoS.AT_LEAST_ONCE, false, false);
					message.reply("CORRECTÍSIMO");
				} else {
					message.fail(500, "ERROR AL ENVIAR MQTT");
				}
			});
		});

	}

	@Override
	public void stop(Promise<Void> startFuture) {
		mqttClient.disconnect();
	}

}
