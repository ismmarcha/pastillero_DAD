package es.us.lsi.dad;

import io.vertx.core.Vertx;

public class Main {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		//Inicia el vértice encargado de recibir las peticiones HTTP (HTTP Server)
        vertx.deployVerticle(new HttpServerVerticle());
<<<<<<< Updated upstream:Pillbox_Pro/src/main/java/es/us/lsi/dad/Main.java
        vertx.deployVerticle(new MqttServerVerticle());
=======
        vertx.deployVerticle(new MQTTServerVerticle());
>>>>>>> Stashed changes:Usuario/src/main/java/es/us/lsi/dad/Main.java
	}
	
}
