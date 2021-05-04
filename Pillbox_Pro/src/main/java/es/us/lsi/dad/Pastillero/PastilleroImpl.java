package es.us.lsi.dad.Pastillero;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

public class PastilleroImpl {

	private String id_pastillero;
	private String alias;
	
	
	public PastilleroImpl(String id_pastillero, String alias) {
		super();
		this.id_pastillero = id_pastillero;
		this.alias = alias;
	}
	
	public PastilleroImpl(Row v) {
		super();
		this.id_pastillero= v.getString("id_pastillero");
		this.alias = v.getString("alias");
	}
	
	public PastilleroImpl(String body) {
		super();
		JsonObject json = new JsonObject(body);
		if (json.containsKey("id_pastillero")) {
			this.id_pastillero = json.getString("id_pastillero");
		}
		this.alias = json.getString("alias");
	}
	

	public String getId_pastillero() {
		return id_pastillero;
	}

	public void setId_pastillero(String id_pastillero) {
		this.id_pastillero = id_pastillero;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public JsonObject getJson() {
		JsonObject json = new JsonObject();

		json.put("id_pastillero", this.getId_pastillero());
		json.put("alias", this.getAlias());
		return json;
	}

}
