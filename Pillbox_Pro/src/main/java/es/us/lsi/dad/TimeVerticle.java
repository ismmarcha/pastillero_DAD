package es.us.lsi.dad;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;

public class TimeVerticle extends AbstractVerticle {

	Map<String, Object> siguientesDosis;
	MqttClient mqttClient;
	Locale spanishLocale = new Locale("es", "ES");

	TimeVerticle(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}

	@Override
	public void start(Promise<Void> startFuture) {
		siguientesDosis = new HashMap<String, Object>();
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
		vertx.eventBus().request("getSiguienteDosisByPastillero", "getSiguienteDosisByPastillero", reply -> {
			if (reply.succeeded()) {
				calcularEnvioMensajeDosis();
				JsonObject jsonReply = (JsonObject) reply.result().body();
				siguientesDosis.putAll(jsonReply.getMap());
				System.out.println(siguientesDosis.toString());
			} else {
				System.out.println("ERROR: " + reply.cause());
			}
		});
	}

	public void calcularEnvioMensajeDosis() {
		LocalDateTime now = LocalDateTime.now();
		ArrayList<String> listToRemove = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : siguientesDosis.entrySet()) {
			String hashPlaca = entry.getKey();
			JsonObject jsonValue = new JsonObject(entry.getValue().toString());
			Map<String, Object> mapValue = jsonValue.getMap();
			int diaSemana = Integer.valueOf(mapValue.entrySet().iterator().next().getKey());
			String horaStr = String.valueOf(mapValue.entrySet().iterator().next().getValue());
			LocalTime horaTime = LocalTime.parse(horaStr);
			LocalDateTime horaDateTime = now.withHour(horaTime.getHour());
			horaDateTime = horaDateTime.withMinute(horaTime.getMinute());
			System.out.println("Dia de la semana: " + diaSemana + "->" + horaStr);
			LocalDateTime fechaNextDosis;
			if (((now.getDayOfWeek().getValue() - 1) == diaSemana && now.compareTo(horaDateTime) > 0)
					|| ((now.getDayOfWeek().getValue() - 1) != diaSemana)) {
				fechaNextDosis = now.with(TemporalAdjusters.next(DayOfWeek.of(diaSemana + 1)));
			} else {
				fechaNextDosis = now;
			}
			fechaNextDosis = fechaNextDosis.withHour(horaTime.getHour());
			fechaNextDosis = fechaNextDosis.withMinute(horaTime.getMinute());
			System.out.println("LocalDateTime Next day: " + fechaNextDosis);
			if (fechaNextDosis.compareTo(now) <= 0) {
				mqttClient.publish("placa/" + entry.getKey() + "/move", Buffer.buffer("1"), MqttQoS.AT_LEAST_ONCE,
						false, false);
				listToRemove.add(entry.getKey());
			}
			JsonObject datosNextDosis = new JsonObject();
			datosNextDosis.put("dia", DayOfWeek.of(diaSemana + 1));
			datosNextDosis.put("hora", horaStr);
			mqttClient.publish("placa/" + entry.getKey() + "/nextDosis", datosNextDosis.toBuffer(),
					MqttQoS.AT_LEAST_ONCE, false, false);
		}
		listToRemove.forEach(k -> {
			siguientesDosis.remove(k);
		});
	}
}
