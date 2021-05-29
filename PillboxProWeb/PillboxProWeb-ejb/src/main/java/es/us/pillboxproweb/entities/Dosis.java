/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "dosis")
@NamedQueries({
    @NamedQuery(name = "Dosis.findAll", query = "SELECT d FROM Dosis d"),
    @NamedQuery(name = "Dosis.findByIdDosis", query = "SELECT d FROM Dosis d WHERE d.idDosis = :idDosis"),
    @NamedQuery(name = "Dosis.findByHoraInicio", query = "SELECT d FROM Dosis d WHERE d.horaInicio = :horaInicio"),
    @NamedQuery(name = "Dosis.findByDiaSemana", query = "SELECT d FROM Dosis d WHERE d.diaSemana = :diaSemana"),
    @NamedQuery(name = "Dosis.findByObservacion", query = "SELECT d FROM Dosis d WHERE d.observacion = :observacion")})
public class Dosis implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_dosis")
    private Integer idDosis;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "hora_inicio")
    private String horaInicio;
    @Column(name = "dia_semana")
    private Integer diaSemana;
    @Size(max = 200)
    @Column(name = "observacion")
    private String observacion;
    @OneToMany(mappedBy = "idDosis")
    private Collection<RegistroDosis> registroDosisCollection;
    @JoinColumn(name = "nif", referencedColumnName = "nif")
    @ManyToOne
    private Usuario nif;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dosis")
    private Collection<PastillaDosis> pastillaDosisCollection;

    public Dosis() {
    }

    public Dosis(Integer idDosis) {
        this.idDosis = idDosis;
    }

    public Dosis(Integer idDosis, String horaInicio) {
        this.idDosis = idDosis;
        this.horaInicio = horaInicio;
    }

    public Integer getIdDosis() {
        return idDosis;
    }

    public void setIdDosis(Integer idDosis) {
        this.idDosis = idDosis;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Collection<RegistroDosis> getRegistroDosisCollection() {
        return registroDosisCollection;
    }

    public void setRegistroDosisCollection(Collection<RegistroDosis> registroDosisCollection) {
        this.registroDosisCollection = registroDosisCollection;
    }

    public Usuario getNif() {
        return nif;
    }

    public void setNif(Usuario nif) {
        this.nif = nif;
    }

    public Collection<PastillaDosis> getPastillaDosisCollection() {
        return pastillaDosisCollection;
    }

    public void setPastillaDosisCollection(Collection<PastillaDosis> pastillaDosisCollection) {
        this.pastillaDosisCollection = pastillaDosisCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDosis != null ? idDosis.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dosis)) {
            return false;
        }
        Dosis other = (Dosis) object;
        if ((this.idDosis == null && other.idDosis != null) || (this.idDosis != null && !this.idDosis.equals(other.idDosis))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.us.pillboxproweb.entities.Dosis[ idDosis=" + idDosis + " ]";
    }
    
}
