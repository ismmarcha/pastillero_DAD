package es.us.lsi.dad.Pastilla;

import io.vertx.core.json.JsonObject; 
import io.vertx.sqlclient.Row;


public class PastillaImpl {
	private int id_pastilla;
	private String nombre;
	private String descripcion;
	private double peso;
	
	public PastillaImpl(int id_pastilla ,String nombre, String descripcion, double peso) {
		super();
		this.id_pastilla = id_pastilla;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.peso = peso;		
	}
	
	public PastillaImpl(Row v) {
		super();
		this.id_pastilla = v.getInteger("id_pastilla");
		this.nombre = v.getString("nombre");
		this.descripcion = v.getString("descripcion");
		;
		this.peso = v.getDouble("peso");
	}
	
	public PastillaImpl(String body) {
		super();
		JsonObject json = new JsonObject(body);
		if (json.containsKey("id_pastilla")) {
			this.id_pastilla = json.getInteger("id_pastilla");
		}
		this.nombre = json.getString("nombre");
		this.descripcion = json.getString("descripcion");
		this.peso = json.getDouble("peso");
	}

	public int getId_pastilla() {
		return id_pastilla;
	}

	public void setId_pastilla(int id_pastilla) {
		this.id_pastilla = id_pastilla;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getPeso() {
		return peso;
	}

	public void setPeso(double peso) {
		this.peso = peso;
	}
	
	public JsonObject getJson() {
		JsonObject json = new JsonObject();

		json.put("id_pastilla", this.getId_pastilla());
		json.put("nombre", this.getNombre());
		json.put("descripcion", this.getDescripcion());
		json.put("peso", this.getPeso());

		return json;
	}
	

}
