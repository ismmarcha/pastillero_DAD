package es.us.lsi.dad.Usuario;

import io.vertx.core.json.JsonObject; 
import io.vertx.sqlclient.Row;

public class UsuarioImpl {

	protected String nif;
	protected String id_pastillero;
	protected String firstname;
	protected String lastname;
	protected String contraseña;
	protected String email;
	protected String rol;
	protected String id_cuidador;
	protected String reg_date;

	public UsuarioImpl(String nif,String id_pastillero, String firstname, String lastname, String contraseña, String email, String rol,
			String id_cuidador, String reg_date) {
		super();
		this.nif = nif;
		this.id_pastillero = id_pastillero;
		this.firstname = firstname;
		this.lastname = lastname;
		this.contraseña = contraseña;
		this.email = email;
		this.rol = rol;
		this.id_cuidador = id_cuidador;
		this.reg_date = reg_date;
	}

	public UsuarioImpl(Row v) {
		super();
		this.nif = v.getString("nif");
		this.id_pastillero = v.getString("id_pastillero");
		this.firstname = v.getString("firstname");
		this.lastname = v.getString("lastname");
		this.contraseña = v.getString("contraseña");
		this.email = v.getString("email");
		this.rol = v.getString("rol");
		this.id_cuidador = v.getString("id_cuidador");
		this.reg_date = String.valueOf(v.getLocalDateTime("reg_date"));
	}

	public UsuarioImpl(String body) {
		super();
		JsonObject v = new JsonObject(body);
		if (v.containsKey("id_pastilla")) {
			this.nif = v.getString("nif");
		}
		this.id_pastillero=v.getString("id_pastillero");
		this.firstname = v.getString("firstname");
		this.lastname = v.getString("lastname");
		this.contraseña = v.getString("contraseña");
		this.email = v.getString("email");
		this.rol = v.getString("rol");
		this.id_cuidador = v.getString("id_cuidador");
		this.reg_date = v.getString("reg_date");
	}

	
	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getId_pastillero() {
		return id_pastillero;
	}

	public void setId_pastillero(String id_pastillero) {
		this.id_pastillero = id_pastillero;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getId_cuidador() {
		return id_cuidador;
	}

	public void setId_cuidador(String id_cuidador) {
		this.id_cuidador = id_cuidador;
	}

	public String getReg_date() {
		return reg_date;
	}

	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}

	
	public JsonObject getJson() {
		JsonObject json = new JsonObject();

		json.put("nif", this.getNif());
		json.put("id_pastillero", this.getId_pastillero());
		json.put("firstname", this.getFirstname());
		json.put("lastname", this.getLastname());
		json.put("contraseña", this.getContraseña());
		json.put("email", this.getEmail());
		json.put("rol", this.getRol());
		json.put("id_cuidador", this.getId_cuidador());
		json.put("reg_date", this.getReg_date());

		return json;
	}
}
