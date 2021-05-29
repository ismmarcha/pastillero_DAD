/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author lol39
 */
@Embeddable
public class PastillaDosisPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_pastilla")
    private int idPastilla;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_dosis")
    private int idDosis;

    public PastillaDosisPK() {
    }

    public PastillaDosisPK(int idPastilla, int idDosis) {
        this.idPastilla = idPastilla;
        this.idDosis = idDosis;
    }

    public int getIdPastilla() {
        return idPastilla;
    }

    public void setIdPastilla(int idPastilla) {
        this.idPastilla = idPastilla;
    }

    public int getIdDosis() {
        return idDosis;
    }

    public void setIdDosis(int idDosis) {
        this.idDosis = idDosis;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idPastilla;
        hash += (int) idDosis;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PastillaDosisPK)) {
            return false;
        }
        PastillaDosisPK other = (PastillaDosisPK) object;
        if (this.idPastilla != other.idPastilla) {
            return false;
        }
        if (this.idDosis != other.idDosis) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.us.pillboxproweb.entities.PastillaDosisPK[ idPastilla=" + idPastilla + ", idDosis=" + idDosis + " ]";
    }
    
}
