/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author lol39
 */
@Entity
@Table(name = "usuario")
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByNif", query = "SELECT u FROM Usuario u WHERE u.nif = :nif"),
    @NamedQuery(name = "Usuario.findByFirstname", query = "SELECT u FROM Usuario u WHERE u.firstname = :firstname"),
    @NamedQuery(name = "Usuario.findByLastname", query = "SELECT u FROM Usuario u WHERE u.lastname = :lastname"),
    @NamedQuery(name = "Usuario.findByContrase\u00f1a", query = "SELECT u FROM Usuario u WHERE u.contrase\u00f1a = :contrase\u00f1a"),
    @NamedQuery(name = "Usuario.findByEmail", query = "SELECT u FROM Usuario u WHERE u.email = :email"),
    @NamedQuery(name = "Usuario.findByRol", query = "SELECT u FROM Usuario u WHERE u.rol = :rol"),
    @NamedQuery(name = "Usuario.findByRegDate", query = "SELECT u FROM Usuario u WHERE u.regDate = :regDate")})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 9)
    @Column(name = "nif")
    private String nif;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "firstname")
    private String firstname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "lastname")
    private String lastname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "contrase\u00f1a")
    private String contraseña;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "rol")
    private String rol;
    @Column(name = "reg_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;
    @OneToMany(mappedBy = "nif")
    private Collection<Dosis> dosisCollection;
    @JoinColumn(name = "id_pastillero", referencedColumnName = "id_pastillero")
    @ManyToOne
    private Pastillero idPastillero;
    @OneToMany(mappedBy = "idCuidador")
    private Collection<Usuario> usuarioCollection;
    @JoinColumn(name = "id_cuidador", referencedColumnName = "nif")
    @ManyToOne
    private Usuario idCuidador;

    public Usuario() {
    }

    public Usuario(String nif) {
        this.nif = nif;
    }

    public Usuario(String nif, String firstname, String lastname, String contraseña, String email, String rol) {
        this.nif = nif;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contraseña = contraseña;
        this.email = email;
        this.rol = rol;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
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

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Collection<Dosis> getDosisCollection() {
        return dosisCollection;
    }

    public void setDosisCollection(Collection<Dosis> dosisCollection) {
        this.dosisCollection = dosisCollection;
    }

    public Pastillero getIdPastillero() {
        return idPastillero;
    }

    public void setIdPastillero(Pastillero idPastillero) {
        this.idPastillero = idPastillero;
    }

    public Collection<Usuario> getUsuarioCollection() {
        return usuarioCollection;
    }

    public void setUsuarioCollection(Collection<Usuario> usuarioCollection) {
        this.usuarioCollection = usuarioCollection;
    }

    public Usuario getIdCuidador() {
        return idCuidador;
    }

    public void setIdCuidador(Usuario idCuidador) {
        this.idCuidador = idCuidador;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nif != null ? nif.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.nif == null && other.nif != null) || (this.nif != null && !this.nif.equals(other.nif))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.us.pillboxproweb.entities.Usuario[ nif=" + nif + " ]";
    }
    
}
