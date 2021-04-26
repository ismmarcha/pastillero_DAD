package es.us.lsi.dad;

import com.google.gson.Gson; 

import es.us.lsi.dad.Pastilla.BDPastilla;
import es.us.lsi.dad.Dosis.BDDosis;
import es.us.lsi.dad.Pastillero.BDPastillero;
import es.us.lsi.dad.Usuario.BDUsuario;
import es.us.lsi.dad.Usuario.UsuarioImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Query;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class BDVerticle extends AbstractVerticle {

	MySQLPool mySqlClient;
	Gson gson;

	@Override
	public void start(Promise<Void> startFuture) {
		gson = new Gson();
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("pastillero_dad").setUser("root").setPassword("ISmmarcha671");

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

		mySqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

		BDUsuario bdUsuario = new BDUsuario(vertx, mySqlClient);
		BDPastillero bdPastillero = new BDPastillero(vertx, mySqlClient);
		BDDosis bdDosis = new BDDosis(vertx, mySqlClient);
		BDPastilla bdPastilla = new BDPastilla(vertx, mySqlClient);
		
		bdUsuario.iniciarConsumersBDUsuario();
		bdPastillero.iniciarConsumersBDPastillero();
		bdDosis.iniciarConsumersBDDosis();
		bdPastilla.iniciarConsumersBDPastilla();
		
		startFuture.complete();

	}
	
}