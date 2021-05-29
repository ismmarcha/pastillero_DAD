/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.controllers;

import es.us.pillboxproweb.controllers.exceptions.NonexistentEntityException;
import es.us.pillboxproweb.controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import es.us.pillboxproweb.entities.Dosis;
import es.us.pillboxproweb.entities.Pastilla;
import es.us.pillboxproweb.entities.PastillaDosis;
import es.us.pillboxproweb.entities.PastillaDosisPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author lol39
 */
public class PastillaDosisJpaController implements Serializable {

    public PastillaDosisJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PastillaDosis pastillaDosis) throws PreexistingEntityException, Exception {
        if (pastillaDosis.getPastillaDosisPK() == null) {
            pastillaDosis.setPastillaDosisPK(new PastillaDosisPK());
        }
        pastillaDosis.getPastillaDosisPK().setIdDosis(pastillaDosis.getDosis().getIdDosis());
        pastillaDosis.getPastillaDosisPK().setIdPastilla(pastillaDosis.getPastilla().getIdPastilla());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dosis dosis = pastillaDosis.getDosis();
            if (dosis != null) {
                dosis = em.getReference(dosis.getClass(), dosis.getIdDosis());
                pastillaDosis.setDosis(dosis);
            }
            Pastilla pastilla = pastillaDosis.getPastilla();
            if (pastilla != null) {
                pastilla = em.getReference(pastilla.getClass(), pastilla.getIdPastilla());
                pastillaDosis.setPastilla(pastilla);
            }
            em.persist(pastillaDosis);
            if (dosis != null) {
                dosis.getPastillaDosisCollection().add(pastillaDosis);
                dosis = em.merge(dosis);
            }
            if (pastilla != null) {
                pastilla.getPastillaDosisCollection().add(pastillaDosis);
                pastilla = em.merge(pastilla);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPastillaDosis(pastillaDosis.getPastillaDosisPK()) != null) {
                throw new PreexistingEntityException("PastillaDosis " + pastillaDosis + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PastillaDosis pastillaDosis) throws NonexistentEntityException, Exception {
        pastillaDosis.getPastillaDosisPK().setIdDosis(pastillaDosis.getDosis().getIdDosis());
        pastillaDosis.getPastillaDosisPK().setIdPastilla(pastillaDosis.getPastilla().getIdPastilla());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PastillaDosis persistentPastillaDosis = em.find(PastillaDosis.class, pastillaDosis.getPastillaDosisPK());
            Dosis dosisOld = persistentPastillaDosis.getDosis();
            Dosis dosisNew = pastillaDosis.getDosis();
            Pastilla pastillaOld = persistentPastillaDosis.getPastilla();
            Pastilla pastillaNew = pastillaDosis.getPastilla();
            if (dosisNew != null) {
                dosisNew = em.getReference(dosisNew.getClass(), dosisNew.getIdDosis());
                pastillaDosis.setDosis(dosisNew);
            }
            if (pastillaNew != null) {
                pastillaNew = em.getReference(pastillaNew.getClass(), pastillaNew.getIdPastilla());
                pastillaDosis.setPastilla(pastillaNew);
            }
            pastillaDosis = em.merge(pastillaDosis);
            if (dosisOld != null && !dosisOld.equals(dosisNew)) {
                dosisOld.getPastillaDosisCollection().remove(pastillaDosis);
                dosisOld = em.merge(dosisOld);
            }
            if (dosisNew != null && !dosisNew.equals(dosisOld)) {
                dosisNew.getPastillaDosisCollection().add(pastillaDosis);
                dosisNew = em.merge(dosisNew);
            }
            if (pastillaOld != null && !pastillaOld.equals(pastillaNew)) {
                pastillaOld.getPastillaDosisCollection().remove(pastillaDosis);
                pastillaOld = em.merge(pastillaOld);
            }
            if (pastillaNew != null && !pastillaNew.equals(pastillaOld)) {
                pastillaNew.getPastillaDosisCollection().add(pastillaDosis);
                pastillaNew = em.merge(pastillaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PastillaDosisPK id = pastillaDosis.getPastillaDosisPK();
                if (findPastillaDosis(id) == null) {
                    throw new NonexistentEntityException("The pastillaDosis with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(PastillaDosisPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PastillaDosis pastillaDosis;
            try {
                pastillaDosis = em.getReference(PastillaDosis.class, id);
                pastillaDosis.getPastillaDosisPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pastillaDosis with id " + id + " no longer exists.", enfe);
            }
            Dosis dosis = pastillaDosis.getDosis();
            if (dosis != null) {
                dosis.getPastillaDosisCollection().remove(pastillaDosis);
                dosis = em.merge(dosis);
            }
            Pastilla pastilla = pastillaDosis.getPastilla();
            if (pastilla != null) {
                pastilla.getPastillaDosisCollection().remove(pastillaDosis);
                pastilla = em.merge(pastilla);
            }
            em.remove(pastillaDosis);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PastillaDosis> findPastillaDosisEntities() {
        return findPastillaDosisEntities(true, -1, -1);
    }

    public List<PastillaDosis> findPastillaDosisEntities(int maxResults, int firstResult) {
        return findPastillaDosisEntities(false, maxResults, firstResult);
    }

    private List<PastillaDosis> findPastillaDosisEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PastillaDosis.class));
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

    public PastillaDosis findPastillaDosis(PastillaDosisPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PastillaDosis.class, id);
        } finally {
            em.close();
        }
    }

    public int getPastillaDosisCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PastillaDosis> rt = cq.from(PastillaDosis.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
