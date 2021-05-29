/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author lol39
 */
@Entity
@Table(name = "pastilla_dosis")
@NamedQueries({
    @NamedQuery(name = "PastillaDosis.findAll", query = "SELECT p FROM PastillaDosis p"),
    @NamedQuery(name = "PastillaDosis.findByIdPastilla", query = "SELECT p FROM PastillaDosis p WHERE p.pastillaDosisPK.idPastilla = :idPastilla"),
    @NamedQuery(name = "PastillaDosis.findByIdDosis", query = "SELECT p FROM PastillaDosis p WHERE p.pastillaDosisPK.idDosis = :idDosis"),
    @NamedQuery(name = "PastillaDosis.findByCantidad", query = "SELECT p FROM PastillaDosis p WHERE p.cantidad = :cantidad")})
public class PastillaDosis implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PastillaDosisPK pastillaDosisPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cantidad")
    private double cantidad;
    @JoinColumn(name = "id_dosis", referencedColumnName = "id_dosis", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Dosis dosis;
    @JoinColumn(name = "id_pastilla", referencedColumnName = "id_pastilla", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Pastilla pastilla;

    public PastillaDosis() {
    }

    public PastillaDosis(PastillaDosisPK pastillaDosisPK) {
        this.pastillaDosisPK = pastillaDosisPK;
    }

    public PastillaDosis(PastillaDosisPK pastillaDosisPK, double cantidad) {
        this.pastillaDosisPK = pastillaDosisPK;
        this.cantidad = cantidad;
    }

    public PastillaDosis(int idPastilla, int idDosis) {
        this.pastillaDosisPK = new PastillaDosisPK(idPastilla, idDosis);
    }

    public PastillaDosisPK getPastillaDosisPK() {
        return pastillaDosisPK;
    }

    public void setPastillaDosisPK(PastillaDosisPK pastillaDosisPK) {
        this.pastillaDosisPK = pastillaDosisPK;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public Dosis getDosis() {
        return dosis;
    }

    public void setDosis(Dosis dosis) {
        this.dosis = dosis;
    }

    public Pastilla getPastilla() {
        return pastilla;
    }

    public void setPastilla(Pastilla pastilla) {
        this.pastilla = pastilla;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pastillaDosisPK != null ? pastillaDosisPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PastillaDosis)) {
            return false;
        }
        PastillaDosis other = (PastillaDosis) object;
        if ((this.pastillaDosisPK == null && other.pastillaDosisPK != null) || (this.pastillaDosisPK != null && !this.pastillaDosisPK.equals(other.pastillaDosisPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.us.pillboxproweb.entities.PastillaDosis[ pastillaDosisPK=" + pastillaDosisPK + " ]";
    }
    
}
