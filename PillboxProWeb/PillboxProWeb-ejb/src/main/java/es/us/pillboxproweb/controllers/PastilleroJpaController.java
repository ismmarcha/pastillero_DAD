/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.pillboxproweb.controllers;

import es.us.pillboxproweb.controllers.exceptions.NonexistentEntityException;
import es.us.pillboxproweb.controllers.exceptions.PreexistingEntityException;
import es.us.pillboxproweb.entities.Pastillero;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import es.us.pillboxproweb.entities.Usuario;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author lol39
 */
public class PastilleroJpaController implements Serializable {

    public PastilleroJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pastillero pastillero) throws PreexistingEntityException, Exception {
        if (pastillero.getUsuarioCollection() == null) {
            pastillero.setUsuarioCollection(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Usuario> attachedUsuarioCollection = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionUsuarioToAttach : pastillero.getUsuarioCollection()) {
                usuarioCollectionUsuarioToAttach = em.getReference(usuarioCollectionUsuarioToAttach.getClass(), usuarioCollectionUsuarioToAttach.getNif());
                attachedUsuarioCollection.add(usuarioCollectionUsuarioToAttach);
            }
            pastillero.setUsuarioCollection(attachedUsuarioCollection);
            em.persist(pastillero);
            for (Usuario usuarioCollectionUsuario : pastillero.getUsuarioCollection()) {
                Pastillero oldIdPastilleroOfUsuarioCollectionUsuario = usuarioCollectionUsuario.getIdPastillero();
                usuarioCollectionUsuario.setIdPastillero(pastillero);
                usuarioCollectionUsuario = em.merge(usuarioCollectionUsuario);
                if (oldIdPastilleroOfUsuarioCollectionUsuario != null) {
                    oldIdPastilleroOfUsuarioCollectionUsuario.getUsuarioCollection().remove(usuarioCollectionUsuario);
                    oldIdPastilleroOfUsuarioCollectionUsuario = em.merge(oldIdPastilleroOfUsuarioCollectionUsuario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPastillero(pastillero.getIdPastillero()) != null) {
                throw new PreexistingEntityException("Pastillero " + pastillero + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pastillero pastillero) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pastillero persistentPastillero = em.find(Pastillero.class, pastillero.getIdPastillero());
            Collection<Usuario> usuarioCollectionOld = persistentPastillero.getUsuarioCollection();
            Collection<Usuario> usuarioCollectionNew = pastillero.getUsuarioCollection();
            Collection<Usuario> attachedUsuarioCollectionNew = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionNewUsuarioToAttach : usuarioCollectionNew) {
                usuarioCollectionNewUsuarioToAttach = em.getReference(usuarioCollectionNewUsuarioToAttach.getClass(), usuarioCollectionNewUsuarioToAttach.getNif());
                attachedUsuarioCollectionNew.add(usuarioCollectionNewUsuarioToAttach);
            }
            usuarioCollectionNew = attachedUsuarioCollectionNew;
            pastillero.setUsuarioCollection(usuarioCollectionNew);
            pastillero = em.merge(pastillero);
            for (Usuario usuarioCollectionOldUsuario : usuarioCollectionOld) {
                if (!usuarioCollectionNew.contains(usuarioCollectionOldUsuario)) {
                    usuarioCollectionOldUsuario.setIdPastillero(null);
                    usuarioCollectionOldUsuario = em.merge(usuarioCollectionOldUsuario);
                }
            }
            for (Usuario usuarioCollectionNewUsuario : usuarioCollectionNew) {
                if (!usuarioCollectionOld.contains(usuarioCollectionNewUsuario)) {
                    Pastillero oldIdPastilleroOfUsuarioCollectionNewUsuario = usuarioCollectionNewUsuario.getIdPastillero();
                    usuarioCollectionNewUsuario.setIdPastillero(pastillero);
                    usuarioCollectionNewUsuario = em.merge(usuarioCollectionNewUsuario);
                    if (oldIdPastilleroOfUsuarioCollectionNewUsuario != null && !oldIdPastilleroOfUsuarioCollectionNewUsuario.equals(pastillero)) {
                        oldIdPastilleroOfUsuarioCollectionNewUsuario.getUsuarioCollection().remove(usuarioCollectionNewUsuario);
                        oldIdPastilleroOfUsuarioCollectionNewUsuario = em.merge(oldIdPastilleroOfUsuarioCollectionNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = pastillero.getIdPastillero();
                if (findPastillero(id) == null) {
                    throw new NonexistentEntityException("The pastillero with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pastillero pastillero;
            try {
                pastillero = em.getReference(Pastillero.class, id);
                pastillero.getIdPastillero();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pastillero with id " + id + " no longer exists.", enfe);
            }
            Collection<Usuario> usuarioCollection = pastillero.getUsuarioCollection();
            for (Usuario usuarioCollectionUsuario : usuarioCollection) {
                usuarioCollectionUsuario.setIdPastillero(null);
                usuarioCollectionUsuario = em.merge(usuarioCollectionUsuario);
            }
            em.remove(pastillero);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pastillero> findPastilleroEntities() {
        return findPastilleroEntities(true, -1, -1);
    }

    public List<Pastillero> findPastilleroEntities(int maxResults, int firstResult) {
        return findPastilleroEntities(false, maxResults, firstResult);
    }

    private List<Pastillero> findPastilleroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pastillero.class));
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

    public Pastillero findPastillero(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pastillero.class, id);
        } finally {
            em.close();
        }
    }

    public int getPastilleroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pastillero> rt = cq.from(Pastillero.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
