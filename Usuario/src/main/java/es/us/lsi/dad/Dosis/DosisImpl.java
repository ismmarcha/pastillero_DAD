package es.us.lsi.dad.Dosis;

import io.vertx.core.json.JsonObject; 
import io.vertx.sqlclient.Row;

public class DosisImpl {

	private int id_dosis;
	private String nif;
	private String hora_inicio;
	private int dia_semana;
	private String observacion;

	public DosisImpl(int id_dosis, String nif, String hora_inicio, int dia_semana, String observacion) {
		super();
		this.id_dosis = id_dosis;
		this.nif = nif;
		this.hora_inicio = hora_inicio;
		this.dia_semana = dia_semana;
		this.observacion = observacion;
	}

	public DosisImpl(Row v) {
		super();
		this.id_dosis = v.getInteger("id_dosis");
		this.nif = v.getString("nif");
		this.hora_inicio = v.getString("hora_inicio");
		;
		this.dia_semana = v.getInteger("dia_semana");
		this.observacion = v.getString("observacion");
	}

	public DosisImpl(String body) {
		super();
		JsonObject json = new JsonObject(body);
		if (json.containsKey("id_dosis")) {
			this.id_dosis = json.getInteger("id_dosis");
		}
		this.nif = json.getString("nif");
		this.hora_inicio = json.getString("hora_inicio");
		this.dia_semana = json.getInteger("dia_semana");
		this.observacion = json.getString("observacion");

	}

	public int getId_dosis() {
		return id_dosis;
	}

	public void setId_dosis(int id_dosis) {
		this.id_dosis = id_dosis;
	}

	public String getnif() {
		return nif;
	}

	public void setnif(String nif) {
		this.nif = nif;
	}

	public String getHora_inicio() {
		return hora_inicio;
	}

	public void setHora_inicio(String hora_inicio) {
		this.hora_inicio = hora_inicio;
	}

	public int getDia_semana() {
		return dia_semana;
	}

	public void setDia_semana(int dia_semana) {
		this.dia_semana = dia_semana;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public JsonObject getJson() {
		JsonObject json = new JsonObject();

		json.put("id_dosis", this.getId_dosis());
		json.put("nif", this.getnif());
		json.put("hora_inicio", this.getHora_inicio());
		json.put("dia_semana", this.getDia_semana());
		json.put("observacion", this.getObservacion());

		return json;
	}

}
