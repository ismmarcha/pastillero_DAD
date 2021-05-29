/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author lol39
 */
@Entity
@Table(name = "registro_dosis")
@NamedQueries({
    @NamedQuery(name = "RegistroDosis.findAll", query = "SELECT r FROM RegistroDosis r"),
    @NamedQuery(name = "RegistroDosis.findByIdRegistroDosis", query = "SELECT r FROM RegistroDosis r WHERE r.idRegistroDosis = :idRegistroDosis"),
    @NamedQuery(name = "RegistroDosis.findByFechaDosis", query = "SELECT r FROM RegistroDosis r WHERE r.fechaDosis = :fechaDosis"),
    @NamedQuery(name = "RegistroDosis.findByTomada", query = "SELECT r FROM RegistroDosis r WHERE r.tomada = :tomada")})
public class RegistroDosis implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_registro_dosis")
    private Integer idRegistroDosis;
    @Column(name = "fecha_dosis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDosis;
    @Basic(optional = false)
    @NotNull
    @Column(name = "tomada")
    private boolean tomada;
    @JoinColumn(name = "id_dosis", referencedColumnName = "id_dosis")
    @ManyToOne
    private Dosis idDosis;

    public RegistroDosis() {
    }

    public RegistroDosis(Integer idRegistroDosis) {
        this.idRegistroDosis = idRegistroDosis;
    }

    public RegistroDosis(Integer idRegistroDosis, boolean tomada) {
        this.idRegistroDosis = idRegistroDosis;
        this.tomada = tomada;
    }

    public Integer getIdRegistroDosis() {
        return idRegistroDosis;
    }

    public void setIdRegistroDosis(Integer idRegistroDosis) {
        this.idRegistroDosis = idRegistroDosis;
    }

    public Date getFechaDosis() {
        return fechaDosis;
    }

    public void setFechaDosis(Date fechaDosis) {
        this.fechaDosis = fechaDosis;
    }

    public boolean getTomada() {
        return tomada;
    }

    public void setTomada(boolean tomada) {
        this.tomada = tomada;
    }

    public Dosis getIdDosis() {
        return idDosis;
    }

    public void setIdDosis(Dosis idDosis) {
        this.idDosis = idDosis;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRegistroDosis != null ? idRegistroDosis.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegistroDosis)) {
            return false;
        }
        RegistroDosis other = (RegistroDosis) object;
        if ((this.idRegistroDosis == null && other.idRegistroDosis != null) || (this.idRegistroDosis != null && !this.idRegistroDosis.equals(other.idRegistroDosis))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.us.pillboxproweb.entities.RegistroDosis[ idRegistroDosis=" + idRegistroDosis + " ]";
    }
    
}
