/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.controllers;

import es.us.pillboxproweb.controllers.exceptions.IllegalOrphanException;
import es.us.pillboxproweb.controllers.exceptions.NonexistentEntityException;
import es.us.pillboxproweb.entities.Pastilla;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import es.us.pillboxproweb.entities.PastillaDosis;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author lol39
 */
public class PastillaJpaController implements Serializable {

    public PastillaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pastilla pastilla) {
        if (pastilla.getPastillaDosisCollection() == null) {
            pastilla.setPastillaDosisCollection(new ArrayList<PastillaDosis>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<PastillaDosis> attachedPastillaDosisCollection = new ArrayList<PastillaDosis>();
            for (PastillaDosis pastillaDosisCollectionPastillaDosisToAttach : pastilla.getPastillaDosisCollection()) {
                pastillaDosisCollectionPastillaDosisToAttach = em.getReference(pastillaDosisCollectionPastillaDosisToAttach.getClass(), pastillaDosisCollectionPastillaDosisToAttach.getPastillaDosisPK());
                attachedPastillaDosisCollection.add(pastillaDosisCollectionPastillaDosisToAttach);
            }
            pastilla.setPastillaDosisCollection(attachedPastillaDosisCollection);
            em.persist(pastilla);
            for (PastillaDosis pastillaDosisCollectionPastillaDosis : pastilla.getPastillaDosisCollection()) {
                Pastilla oldPastillaOfPastillaDosisCollectionPastillaDosis = pastillaDosisCollectionPastillaDosis.getPastilla();
                pastillaDosisCollectionPastillaDosis.setPastilla(pastilla);
                pastillaDosisCollectionPastillaDosis = em.merge(pastillaDosisCollectionPastillaDosis);
                if (oldPastillaOfPastillaDosisCollectionPastillaDosis != null) {
                    oldPastillaOfPastillaDosisCollectionPastillaDosis.getPastillaDosisCollection().remove(pastillaDosisCollectionPastillaDosis);
                    oldPastillaOfPastillaDosisCollectionPastillaDosis = em.merge(oldPastillaOfPastillaDosisCollectionPastillaDosis);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pastilla pastilla) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pastilla persistentPastilla = em.find(Pastilla.class, pastilla.getIdPastilla());
            Collection<PastillaDosis> pastillaDosisCollectionOld = persistentPastilla.getPastillaDosisCollection();
            Collection<PastillaDosis> pastillaDosisCollectionNew = pastilla.getPastillaDosisCollection();
            List<String> illegalOrphanMessages = null;
            for (PastillaDosis pastillaDosisCollectionOldPastillaDosis : pastillaDosisCollectionOld) {
                if (!pastillaDosisCollectionNew.contains(pastillaDosisCollectionOldPastillaDosis)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PastillaDosis " + pastillaDosisCollectionOldPastillaDosis + " since its pastilla field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<PastillaDosis> attachedPastillaDosisCollectionNew = new ArrayList<PastillaDosis>();
            for (PastillaDosis pastillaDosisCollectionNewPastillaDosisToAttach : pastillaDosisCollectionNew) {
                pastillaDosisCollectionNewPastillaDosisToAttach = em.getReference(pastillaDosisCollectionNewPastillaDosisToAttach.getClass(), pastillaDosisCollectionNewPastillaDosisToAttach.getPastillaDosisPK());
                attachedPastillaDosisCollectionNew.add(pastillaDosisCollectionNewPastillaDosisToAttach);
            }
            pastillaDosisCollectionNew = attachedPastillaDosisCollectionNew;
            pastilla.setPastillaDosisCollection(pastillaDosisCollectionNew);
            pastilla = em.merge(pastilla);
            for (PastillaDosis pastillaDosisCollectionNewPastillaDosis : pastillaDosisCollectionNew) {
                if (!pastillaDosisCollectionOld.contains(pastillaDosisCollectionNewPastillaDosis)) {
                    Pastilla oldPastillaOfPastillaDosisCollectionNewPastillaDosis = pastillaDosisCollectionNewPastillaDosis.getPastilla();
                    pastillaDosisCollectionNewPastillaDosis.setPastilla(pastilla);
                    pastillaDosisCollectionNewPastillaDosis = em.merge(pastillaDosisCollectionNewPastillaDosis);
                    if (oldPastillaOfPastillaDosisCollectionNewPastillaDosis != null && !oldPastillaOfPastillaDosisCollectionNewPastillaDosis.equals(pastilla)) {
                        oldPastillaOfPastillaDosisCollectionNewPastillaDosis.getPastillaDosisCollection().remove(pastillaDosisCollectionNewPastillaDosis);
                        oldPastillaOfPastillaDosisCollectionNewPastillaDosis = em.merge(oldPastillaOfPastillaDosisCollectionNewPastillaDosis);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pastilla.getIdPastilla();
                if (findPastilla(id) == null) {
                    throw new NonexistentEntityException("The pastilla with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pastilla pastilla;
            try {
                pastilla = em.getReference(Pastilla.class, id);
                pastilla.getIdPastilla();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pastilla with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<PastillaDosis> pastillaDosisCollectionOrphanCheck = pastilla.getPastillaDosisCollection();
            for (PastillaDosis pastillaDosisCollectionOrphanCheckPastillaDosis : pastillaDosisCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pastilla (" + pastilla + ") cannot be destroyed since the PastillaDosis " + pastillaDosisCollectionOrphanCheckPastillaDosis + " in its pastillaDosisCollection field has a non-nullable pastilla field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(pastilla);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pastilla> findPastillaEntities() {
        return findPastillaEntities(true, -1, -1);
    }

    public List<Pastilla> findPastillaEntities(int maxResults, int firstResult) {
        return findPastillaEntities(false, maxResults, firstResult);
    }

    private List<Pastilla> findPastillaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pastilla.class));
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

    public Pastilla findPastilla(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pastilla.class, id);
        } finally {
            em.close();
        }
    }

    public int getPastillaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pastilla> rt = cq.from(Pastilla.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
