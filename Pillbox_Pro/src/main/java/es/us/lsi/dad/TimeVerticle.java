package es.us.lsi.dad;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;

public class TimeVerticle extends AbstractVerticle {

	Map<String, String> siguientesDosis;
	Map<String, LocalTime> siguienteDosisTime;
	MqttClient mqttClient;

	TimeVerticle(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}

	@Override
	public void start(Promise<Void> startFuture) {
		siguientesDosis = new HashMap<String, String>();
		siguienteDosisTime = new HashMap<String, LocalTime>();
		siguientesDosis.put("a8df25211e38f106b2602c3cb5da01c66616160a", "20:03");

		for (Map.Entry<String, String> entry : siguientesDosis.entrySet()) {
			siguienteDosisTime.put(entry.getKey(), LocalTime.parse(entry.getValue()));
		}
		timerSiguientesDosis();
	}

	@Override
	public void stop(Promise<Void> startFuture) {

	}

	public void timerSiguientesDosis() {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				obtenerSiguientesDosis();
			}
		}, Date.from(Instant.now()), Duration.ofSeconds(30).toMillis());
	}

	public void obtenerSiguientesDosis() {
		LocalTime now = LocalTime.now();
		int hour = now.getHour();
		int minute = now.getMinute();
		for (Map.Entry<String, LocalTime> entry : siguienteDosisTime.entrySet()) {
			System.out.println("BD: "+entry.getValue().toString());
			System.out.println("Local: "+now.toString());
			if (entry.getValue().compareTo(now) <= 0) {
				mqttClient.publish("placa/" + entry.getKey() + "/move",
						Buffer.buffer(entry.getValue().toString()),
						MqttQoS.AT_LEAST_ONCE, false, false);
			}
		}
	}
}
