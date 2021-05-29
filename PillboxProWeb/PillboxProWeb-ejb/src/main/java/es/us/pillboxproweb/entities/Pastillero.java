/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author lol39
 */
@Entity
@Table(name = "pastillero")
@NamedQueries({
    @NamedQuery(name = "Pastillero.findAll", query = "SELECT p FROM Pastillero p"),
    @NamedQuery(name = "Pastillero.findByIdPastillero", query = "SELECT p FROM Pastillero p WHERE p.idPastillero = :idPastillero"),
    @NamedQuery(name = "Pastillero.findByAlias", query = "SELECT p FROM Pastillero p WHERE p.alias = :alias")})
public class Pastillero implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "id_pastillero")
    private String idPastillero;
    @Size(max = 100)
    @Column(name = "alias")
    private String alias;
    @OneToMany(mappedBy = "idPastillero")
    private Collection<Usuario> usuarioCollection;

    public Pastillero() {
    }

    public Pastillero(String idPastillero) {
        this.idPastillero = idPastillero;
    }

    public String getIdPastillero() {
        return idPastillero;
    }

    public void setIdPastillero(String idPastillero) {
        this.idPastillero = idPastillero;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Collection<Usuario> getUsuarioCollection() {
        return usuarioCollection;
    }

    public void setUsuarioCollection(Collection<Usuario> usuarioCollection) {
        this.usuarioCollection = usuarioCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPastillero != null ? idPastillero.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pastillero)) {
            return false;
        }
        Pastillero other = (Pastillero) object;
        if ((this.idPastillero == null && other.idPastillero != null) || (this.idPastillero != null && !this.idPastillero.equals(other.idPastillero))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.us.pillboxproweb.entities.Pastillero[ idPastillero=" + idPastillero + " ]";
    }
    
}
