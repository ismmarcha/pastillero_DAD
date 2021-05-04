package es.us.lsi.dad.Dosis;

import io.vertx.core.json.JsonObject;  
import io.vertx.sqlclient.Row;

public class RegistroDosisImpl {
	private int id_registro_dosis;
	private int id_dosis;
	private boolean tomada;
	
	
	public RegistroDosisImpl(int id_registro_dosis, int id_dosis, boolean tomada) {
		super();
		this.id_registro_dosis = id_registro_dosis;
		this.id_dosis = id_dosis;
		this.tomada = tomada;
	}
	
	public RegistroDosisImpl(Row v) {
		super();
		this.id_registro_dosis = v.getInteger("id_registro_dosis");
		this.id_dosis = v.getInteger("id_dosis");
		this.tomada = v.getBoolean("tomada");
		
	}
	
	public RegistroDosisImpl(String body) {
		super();
		JsonObject json = new JsonObject(body);
		if (json.containsKey("id_registro_dosis")) {
			this.id_registro_dosis = json.getInteger("id_registro_dosis");
		}
		this.id_dosis = json.getInteger("id_dosis");
		this.tomada = json.getBoolean("tomada");
	}

	public int getId_registro_dosis() {
		return id_registro_dosis;
	}

	public void setId_registro_dosis(int id_registro_dosis) {
		this.id_registro_dosis = id_registro_dosis;
	}

	public int getId_dosis() {
		return id_dosis;
	}

	public void setId_dosis(int id_dosis) {
		this.id_dosis = id_dosis;
	}

	public boolean isTomada() {
		return tomada;
	}

	public void setTomada(boolean tomada) {
		this.tomada = tomada;
	}
	
	
	public JsonObject getJson() {
		JsonObject json = new JsonObject();

		json.put("id_registro_dosis", this.getId_registro_dosis());
		json.put("id_dosis", this.getId_dosis());
		json.put("tomada", this.isTomada());

		return json;
	}

}
