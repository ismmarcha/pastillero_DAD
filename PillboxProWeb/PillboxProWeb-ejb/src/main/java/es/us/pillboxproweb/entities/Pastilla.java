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
@Table(name = "pastilla")
@NamedQueries({
    @NamedQuery(name = "Pastilla.findAll", query = "SELECT p FROM Pastilla p"),
    @NamedQuery(name = "Pastilla.findByIdPastilla", query = "SELECT p FROM Pastilla p WHERE p.idPastilla = :idPastilla"),
    @NamedQuery(name = "Pastilla.findByNombre", query = "SELECT p FROM Pastilla p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Pastilla.findByDescripcion", query = "SELECT p FROM Pastilla p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "Pastilla.findByPeso", query = "SELECT p FROM Pastilla p WHERE p.peso = :peso")})
public class Pastilla implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_pastilla")
    private Integer idPastilla;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 300)
    @Column(name = "descripcion")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "peso")
    private Double peso;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pastilla")
    private Collection<PastillaDosis> pastillaDosisCollection;

    public Pastilla() {
    }

    public Pastilla(Integer idPastilla) {
        this.idPastilla = idPastilla;
    }

    public Pastilla(Integer idPastilla, String nombre) {
        this.idPastilla = idPastilla;
        this.nombre = nombre;
    }

    public Integer getIdPastilla() {
        return idPastilla;
    }

    public void setIdPastilla(Integer idPastilla) {
        this.idPastilla = idPastilla;
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

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
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
        hash += (idPastilla != null ? idPastilla.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pastilla)) {
            return false;
        }
        Pastilla other = (Pastilla) object;
        if ((this.idPastilla == null && other.idPastilla != null) || (this.idPastilla != null && !this.idPastilla.equals(other.idPastilla))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.us.pillboxproweb.entities.Pastilla[ idPastilla=" + idPastilla + " ]";
    }
    
}
