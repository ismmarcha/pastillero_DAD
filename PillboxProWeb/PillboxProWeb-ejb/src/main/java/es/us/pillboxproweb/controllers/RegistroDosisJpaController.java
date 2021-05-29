/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.controllers;

import es.us.pillboxproweb.controllers.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import es.us.pillboxproweb.entities.Dosis;
import es.us.pillboxproweb.entities.RegistroDosis;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author lol39
 */
public class RegistroDosisJpaController implements Serializable {

    public RegistroDosisJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RegistroDosis registroDosis) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dosis idDosis = registroDosis.getIdDosis();
            if (idDosis != null) {
                idDosis = em.getReference(idDosis.getClass(), idDosis.getIdDosis());
                registroDosis.setIdDosis(idDosis);
            }
            em.persist(registroDosis);
            if (idDosis != null) {
                idDosis.getRegistroDosisCollection().add(registroDosis);
                idDosis = em.merge(idDosis);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RegistroDosis registroDosis) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegistroDosis persistentRegistroDosis = em.find(RegistroDosis.class, registroDosis.getIdRegistroDosis());
            Dosis idDosisOld = persistentRegistroDosis.getIdDosis();
            Dosis idDosisNew = registroDosis.getIdDosis();
            if (idDosisNew != null) {
                idDosisNew = em.getReference(idDosisNew.getClass(), idDosisNew.getIdDosis());
                registroDosis.setIdDosis(idDosisNew);
            }
            registroDosis = em.merge(registroDosis);
            if (idDosisOld != null && !idDosisOld.equals(idDosisNew)) {
                idDosisOld.getRegistroDosisCollection().remove(registroDosis);
                idDosisOld = em.merge(idDosisOld);
            }
            if (idDosisNew != null && !idDosisNew.equals(idDosisOld)) {
                idDosisNew.getRegistroDosisCollection().add(registroDosis);
                idDosisNew = em.merge(idDosisNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = registroDosis.getIdRegistroDosis();
                if (findRegistroDosis(id) == null) {
                    throw new NonexistentEntityException("The registroDosis with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegistroDosis registroDosis;
            try {
                registroDosis = em.getReference(RegistroDosis.class, id);
                registroDosis.getIdRegistroDosis();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The registroDosis with id " + id + " no longer exists.", enfe);
            }
            Dosis idDosis = registroDosis.getIdDosis();
            if (idDosis != null) {
                idDosis.getRegistroDosisCollection().remove(registroDosis);
                idDosis = em.merge(idDosis);
            }
            em.remove(registroDosis);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RegistroDosis> findRegistroDosisEntities() {
        return findRegistroDosisEntities(true, -1, -1);
    }

    public List<RegistroDosis> findRegistroDosisEntities(int maxResults, int firstResult) {
        return findRegistroDosisEntities(false, maxResults, firstResult);
    }

    private List<RegistroDosis> findRegistroDosisEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RegistroDosis.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public RegistroDosis findRegistroDosis(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RegistroDosis.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegistroDosisCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RegistroDosis> rt = cq.from(RegistroDosis.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
