/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.controllers;

import es.us.pillboxproweb.controllers.exceptions.IllegalOrphanException;
import es.us.pillboxproweb.controllers.exceptions.NonexistentEntityException;
import es.us.pillboxproweb.entities.Dosis;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import es.us.pillboxproweb.entities.Usuario;
import es.us.pillboxproweb.entities.RegistroDosis;
import java.util.ArrayList;
import java.util.Collection;
import es.us.pillboxproweb.entities.PastillaDosis;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author lol39
 */
public class DosisJpaController implements Serializable {

    public DosisJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Dosis dosis) {
        if (dosis.getRegistroDosisCollection() == null) {
            dosis.setRegistroDosisCollection(new ArrayList<RegistroDosis>());
        }
        if (dosis.getPastillaDosisCollection() == null) {
            dosis.setPastillaDosisCollection(new ArrayList<PastillaDosis>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario nif = dosis.getNif();
            if (nif != null) {
                nif = em.getReference(nif.getClass(), nif.getNif());
                dosis.setNif(nif);
            }
            Collection<RegistroDosis> attachedRegistroDosisCollection = new ArrayList<RegistroDosis>();
            for (RegistroDosis registroDosisCollectionRegistroDosisToAttach : dosis.getRegistroDosisCollection()) {
                registroDosisCollectionRegistroDosisToAttach = em.getReference(registroDosisCollectionRegistroDosisToAttach.getClass(), registroDosisCollectionRegistroDosisToAttach.getIdRegistroDosis());
                attachedRegistroDosisCollection.add(registroDosisCollectionRegistroDosisToAttach);
            }
            dosis.setRegistroDosisCollection(attachedRegistroDosisCollection);
            Collection<PastillaDosis> attachedPastillaDosisCollection = new ArrayList<PastillaDosis>();
            for (PastillaDosis pastillaDosisCollectionPastillaDosisToAttach : dosis.getPastillaDosisCollection()) {
                pastillaDosisCollectionPastillaDosisToAttach = em.getReference(pastillaDosisCollectionPastillaDosisToAttach.getClass(), pastillaDosisCollectionPastillaDosisToAttach.getPastillaDosisPK());
                attachedPastillaDosisCollection.add(pastillaDosisCollectionPastillaDosisToAttach);
            }
            dosis.setPastillaDosisCollection(attachedPastillaDosisCollection);
            em.persist(dosis);
            if (nif != null) {
                nif.getDosisCollection().add(dosis);
                nif = em.merge(nif);
            }
            for (RegistroDosis registroDosisCollectionRegistroDosis : dosis.getRegistroDosisCollection()) {
                Dosis oldIdDosisOfRegistroDosisCollectionRegistroDosis = registroDosisCollectionRegistroDosis.getIdDosis();
                registroDosisCollectionRegistroDosis.setIdDosis(dosis);
                registroDosisCollectionRegistroDosis = em.merge(registroDosisCollectionRegistroDosis);
                if (oldIdDosisOfRegistroDosisCollectionRegistroDosis != null) {
                    oldIdDosisOfRegistroDosisCollectionRegistroDosis.getRegistroDosisCollection().remove(registroDosisCollectionRegistroDosis);
                    oldIdDosisOfRegistroDosisCollectionRegistroDosis = em.merge(oldIdDosisOfRegistroDosisCollectionRegistroDosis);
                }
            }
            for (PastillaDosis pastillaDosisCollectionPastillaDosis : dosis.getPastillaDosisCollection()) {
                Dosis oldDosisOfPastillaDosisCollectionPastillaDosis = pastillaDosisCollectionPastillaDosis.getDosis();
                pastillaDosisCollectionPastillaDosis.setDosis(dosis);
                pastillaDosisCollectionPastillaDosis = em.merge(pastillaDosisCollectionPastillaDosis);
                if (oldDosisOfPastillaDosisCollectionPastillaDosis != null) {
                    oldDosisOfPastillaDosisCollectionPastillaDosis.getPastillaDosisCollection().remove(pastillaDosisCollectionPastillaDosis);
                    oldDosisOfPastillaDosisCollectionPastillaDosis = em.merge(oldDosisOfPastillaDosisCollectionPastillaDosis);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Dosis dosis) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dosis persistentDosis = em.find(Dosis.class, dosis.getIdDosis());
            Usuario nifOld = persistentDosis.getNif();
            Usuario nifNew = dosis.getNif();
            Collection<RegistroDosis> registroDosisCollectionOld = persistentDosis.getRegistroDosisCollection();
            Collection<RegistroDosis> registroDosisCollectionNew = dosis.getRegistroDosisCollection();
            Collection<PastillaDosis> pastillaDosisCollectionOld = persistentDosis.getPastillaDosisCollection();
            Collection<PastillaDosis> pastillaDosisCollectionNew = dosis.getPastillaDosisCollection();
            List<String> illegalOrphanMessages = null;
            for (PastillaDosis pastillaDosisCollectionOldPastillaDosis : pastillaDosisCollectionOld) {
                if (!pastillaDosisCollectionNew.contains(pastillaDosisCollectionOldPastillaDosis)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PastillaDosis " + pastillaDosisCollectionOldPastillaDosis + " since its dosis field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (nifNew != null) {
                nifNew = em.getReference(nifNew.getClass(), nifNew.getNif());
                dosis.setNif(nifNew);
            }
            Collection<RegistroDosis> attachedRegistroDosisCollectionNew = new ArrayList<RegistroDosis>();
            for (RegistroDosis registroDosisCollectionNewRegistroDosisToAttach : registroDosisCollectionNew) {
                registroDosisCollectionNewRegistroDosisToAttach = em.getReference(registroDosisCollectionNewRegistroDosisToAttach.getClass(), registroDosisCollectionNewRegistroDosisToAttach.getIdRegistroDosis());
                attachedRegistroDosisCollectionNew.add(registroDosisCollectionNewRegistroDosisToAttach);
            }
            registroDosisCollectionNew = attachedRegistroDosisCollectionNew;
            dosis.setRegistroDosisCollection(registroDosisCollectionNew);
            Collection<PastillaDosis> attachedPastillaDosisCollectionNew = new ArrayList<PastillaDosis>();
            for (PastillaDosis pastillaDosisCollectionNewPastillaDosisToAttach : pastillaDosisCollectionNew) {
                pastillaDosisCollectionNewPastillaDosisToAttach = em.getReference(pastillaDosisCollectionNewPastillaDosisToAttach.getClass(), pastillaDosisCollectionNewPastillaDosisToAttach.getPastillaDosisPK());
                attachedPastillaDosisCollectionNew.add(pastillaDosisCollectionNewPastillaDosisToAttach);
            }
            pastillaDosisCollectionNew = attachedPastillaDosisCollectionNew;
            dosis.setPastillaDosisCollection(pastillaDosisCollectionNew);
            dosis = em.merge(dosis);
            if (nifOld != null && !nifOld.equals(nifNew)) {
                nifOld.getDosisCollection().remove(dosis);
                nifOld = em.merge(nifOld);
            }
            if (nifNew != null && !nifNew.equals(nifOld)) {
                nifNew.getDosisCollection().add(dosis);
                nifNew = em.merge(nifNew);
            }
            for (RegistroDosis registroDosisCollectionOldRegistroDosis : registroDosisCollectionOld) {
                if (!registroDosisCollectionNew.contains(registroDosisCollectionOldRegistroDosis)) {
                    registroDosisCollectionOldRegistroDosis.setIdDosis(null);
                    registroDosisCollectionOldRegistroDosis = em.merge(registroDosisCollectionOldRegistroDosis);
                }
            }
            for (RegistroDosis registroDosisCollectionNewRegistroDosis : registroDosisCollectionNew) {
                if (!registroDosisCollectionOld.contains(registroDosisCollectionNewRegistroDosis)) {
                    Dosis oldIdDosisOfRegistroDosisCollectionNewRegistroDosis = registroDosisCollectionNewRegistroDosis.getIdDosis();
                    registroDosisCollectionNewRegistroDosis.setIdDosis(dosis);
                    registroDosisCollectionNewRegistroDosis = em.merge(registroDosisCollectionNewRegistroDosis);
                    if (oldIdDosisOfRegistroDosisCollectionNewRegistroDosis != null && !oldIdDosisOfRegistroDosisCollectionNewRegistroDosis.equals(dosis)) {
                        oldIdDosisOfRegistroDosisCollectionNewRegistroDosis.getRegistroDosisCollection().remove(registroDosisCollectionNewRegistroDosis);
                        oldIdDosisOfRegistroDosisCollectionNewRegistroDosis = em.merge(oldIdDosisOfRegistroDosisCollectionNewRegistroDosis);
                    }
                }
            }
            for (PastillaDosis pastillaDosisCollectionNewPastillaDosis : pastillaDosisCollectionNew) {
                if (!pastillaDosisCollectionOld.contains(pastillaDosisCollectionNewPastillaDosis)) {
                    Dosis oldDosisOfPastillaDosisCollectionNewPastillaDosis = pastillaDosisCollectionNewPastillaDosis.getDosis();
                    pastillaDosisCollectionNewPastillaDosis.setDosis(dosis);
                    pastillaDosisCollectionNewPastillaDosis = em.merge(pastillaDosisCollectionNewPastillaDosis);
                    if (oldDosisOfPastillaDosisCollectionNewPastillaDosis != null && !oldDosisOfPastillaDosisCollectionNewPastillaDosis.equals(dosis)) {
                        oldDosisOfPastillaDosisCollectionNewPastillaDosis.getPastillaDosisCollection().remove(pastillaDosisCollectionNewPastillaDosis);
                        oldDosisOfPastillaDosisCollectionNewPastillaDosis = em.merge(oldDosisOfPastillaDosisCollectionNewPastillaDosis);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dosis.getIdDosis();
                if (findDosis(id) == null) {
                    throw new NonexistentEntityException("The dosis with id " + id + " no longer exists.");
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
            Dosis dosis;
            try {
                dosis = em.getReference(Dosis.class, id);
                dosis.getIdDosis();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dosis with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<PastillaDosis> pastillaDosisCollectionOrphanCheck = dosis.getPastillaDosisCollection();
            for (PastillaDosis pastillaDosisCollectionOrphanCheckPastillaDosis : pastillaDosisCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Dosis (" + dosis + ") cannot be destroyed since the PastillaDosis " + pastillaDosisCollectionOrphanCheckPastillaDosis + " in its pastillaDosisCollection field has a non-nullable dosis field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario nif = dosis.getNif();
            if (nif != null) {
                nif.getDosisCollection().remove(dosis);
                nif = em.merge(nif);
            }
            Collection<RegistroDosis> registroDosisCollection = dosis.getRegistroDosisCollection();
            for (RegistroDosis registroDosisCollectionRegistroDosis : registroDosisCollection) {
                registroDosisCollectionRegistroDosis.setIdDosis(null);
                registroDosisCollectionRegistroDosis = em.merge(registroDosisCollectionRegistroDosis);
            }
            em.remove(dosis);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Dosis> findDosisEntities() {
        return findDosisEntities(true, -1, -1);
    }

    public List<Dosis> findDosisEntities(int maxResults, int firstResult) {
        return findDosisEntities(false, maxResults, firstResult);
    }

    private List<Dosis> findDosisEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Dosis.class));
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

    public Dosis findDosis(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Dosis.class, id);
        } finally {
            em.close();
        }
    }

    public int getDosisCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Dosis> rt = cq.from(Dosis.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
