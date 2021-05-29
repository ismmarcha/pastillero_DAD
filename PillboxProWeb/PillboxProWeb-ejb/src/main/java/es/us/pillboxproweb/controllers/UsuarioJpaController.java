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
import es.us.pillboxproweb.entities.Pastillero;
import es.us.pillboxproweb.entities.Usuario;
import es.us.pillboxproweb.entities.Dosis;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author lol39
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getDosisCollection() == null) {
            usuario.setDosisCollection(new ArrayList<Dosis>());
        }
        if (usuario.getUsuarioCollection() == null) {
            usuario.setUsuarioCollection(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pastillero idPastillero = usuario.getIdPastillero();
            if (idPastillero != null) {
                idPastillero = em.getReference(idPastillero.getClass(), idPastillero.getIdPastillero());
                usuario.setIdPastillero(idPastillero);
            }
            Usuario idCuidador = usuario.getIdCuidador();
            if (idCuidador != null) {
                idCuidador = em.getReference(idCuidador.getClass(), idCuidador.getNif());
                usuario.setIdCuidador(idCuidador);
            }
            Collection<Dosis> attachedDosisCollection = new ArrayList<Dosis>();
            for (Dosis dosisCollectionDosisToAttach : usuario.getDosisCollection()) {
                dosisCollectionDosisToAttach = em.getReference(dosisCollectionDosisToAttach.getClass(), dosisCollectionDosisToAttach.getIdDosis());
                attachedDosisCollection.add(dosisCollectionDosisToAttach);
            }
            usuario.setDosisCollection(attachedDosisCollection);
            Collection<Usuario> attachedUsuarioCollection = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionUsuarioToAttach : usuario.getUsuarioCollection()) {
                usuarioCollectionUsuarioToAttach = em.getReference(usuarioCollectionUsuarioToAttach.getClass(), usuarioCollectionUsuarioToAttach.getNif());
                attachedUsuarioCollection.add(usuarioCollectionUsuarioToAttach);
            }
            usuario.setUsuarioCollection(attachedUsuarioCollection);
            em.persist(usuario);
            if (idPastillero != null) {
                idPastillero.getUsuarioCollection().add(usuario);
                idPastillero = em.merge(idPastillero);
            }
            if (idCuidador != null) {
                idCuidador.getUsuarioCollection().add(usuario);
                idCuidador = em.merge(idCuidador);
            }
            for (Dosis dosisCollectionDosis : usuario.getDosisCollection()) {
                Usuario oldNifOfDosisCollectionDosis = dosisCollectionDosis.getNif();
                dosisCollectionDosis.setNif(usuario);
                dosisCollectionDosis = em.merge(dosisCollectionDosis);
                if (oldNifOfDosisCollectionDosis != null) {
                    oldNifOfDosisCollectionDosis.getDosisCollection().remove(dosisCollectionDosis);
                    oldNifOfDosisCollectionDosis = em.merge(oldNifOfDosisCollectionDosis);
                }
            }
            for (Usuario usuarioCollectionUsuario : usuario.getUsuarioCollection()) {
                Usuario oldIdCuidadorOfUsuarioCollectionUsuario = usuarioCollectionUsuario.getIdCuidador();
                usuarioCollectionUsuario.setIdCuidador(usuario);
                usuarioCollectionUsuario = em.merge(usuarioCollectionUsuario);
                if (oldIdCuidadorOfUsuarioCollectionUsuario != null) {
                    oldIdCuidadorOfUsuarioCollectionUsuario.getUsuarioCollection().remove(usuarioCollectionUsuario);
                    oldIdCuidadorOfUsuarioCollectionUsuario = em.merge(oldIdCuidadorOfUsuarioCollectionUsuario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getNif()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getNif());
            Pastillero idPastilleroOld = persistentUsuario.getIdPastillero();
            Pastillero idPastilleroNew = usuario.getIdPastillero();
            Usuario idCuidadorOld = persistentUsuario.getIdCuidador();
            Usuario idCuidadorNew = usuario.getIdCuidador();
            Collection<Dosis> dosisCollectionOld = persistentUsuario.getDosisCollection();
            Collection<Dosis> dosisCollectionNew = usuario.getDosisCollection();
            Collection<Usuario> usuarioCollectionOld = persistentUsuario.getUsuarioCollection();
            Collection<Usuario> usuarioCollectionNew = usuario.getUsuarioCollection();
            if (idPastilleroNew != null) {
                idPastilleroNew = em.getReference(idPastilleroNew.getClass(), idPastilleroNew.getIdPastillero());
                usuario.setIdPastillero(idPastilleroNew);
            }
            if (idCuidadorNew != null) {
                idCuidadorNew = em.getReference(idCuidadorNew.getClass(), idCuidadorNew.getNif());
                usuario.setIdCuidador(idCuidadorNew);
            }
            Collection<Dosis> attachedDosisCollectionNew = new ArrayList<Dosis>();
            for (Dosis dosisCollectionNewDosisToAttach : dosisCollectionNew) {
                dosisCollectionNewDosisToAttach = em.getReference(dosisCollectionNewDosisToAttach.getClass(), dosisCollectionNewDosisToAttach.getIdDosis());
                attachedDosisCollectionNew.add(dosisCollectionNewDosisToAttach);
            }
            dosisCollectionNew = attachedDosisCollectionNew;
            usuario.setDosisCollection(dosisCollectionNew);
            Collection<Usuario> attachedUsuarioCollectionNew = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionNewUsuarioToAttach : usuarioCollectionNew) {
                usuarioCollectionNewUsuarioToAttach = em.getReference(usuarioCollectionNewUsuarioToAttach.getClass(), usuarioCollectionNewUsuarioToAttach.getNif());
                attachedUsuarioCollectionNew.add(usuarioCollectionNewUsuarioToAttach);
            }
            usuarioCollectionNew = attachedUsuarioCollectionNew;
            usuario.setUsuarioCollection(usuarioCollectionNew);
            usuario = em.merge(usuario);
            if (idPastilleroOld != null && !idPastilleroOld.equals(idPastilleroNew)) {
                idPastilleroOld.getUsuarioCollection().remove(usuario);
                idPastilleroOld = em.merge(idPastilleroOld);
            }
            if (idPastilleroNew != null && !idPastilleroNew.equals(idPastilleroOld)) {
                idPastilleroNew.getUsuarioCollection().add(usuario);
                idPastilleroNew = em.merge(idPastilleroNew);
            }
            if (idCuidadorOld != null && !idCuidadorOld.equals(idCuidadorNew)) {
                idCuidadorOld.getUsuarioCollection().remove(usuario);
                idCuidadorOld = em.merge(idCuidadorOld);
            }
            if (idCuidadorNew != null && !idCuidadorNew.equals(idCuidadorOld)) {
                idCuidadorNew.getUsuarioCollection().add(usuario);
                idCuidadorNew = em.merge(idCuidadorNew);
            }
            for (Dosis dosisCollectionOldDosis : dosisCollectionOld) {
                if (!dosisCollectionNew.contains(dosisCollectionOldDosis)) {
                    dosisCollectionOldDosis.setNif(null);
                    dosisCollectionOldDosis = em.merge(dosisCollectionOldDosis);
                }
            }
            for (Dosis dosisCollectionNewDosis : dosisCollectionNew) {
                if (!dosisCollectionOld.contains(dosisCollectionNewDosis)) {
                    Usuario oldNifOfDosisCollectionNewDosis = dosisCollectionNewDosis.getNif();
                    dosisCollectionNewDosis.setNif(usuario);
                    dosisCollectionNewDosis = em.merge(dosisCollectionNewDosis);
                    if (oldNifOfDosisCollectionNewDosis != null && !oldNifOfDosisCollectionNewDosis.equals(usuario)) {
                        oldNifOfDosisCollectionNewDosis.getDosisCollection().remove(dosisCollectionNewDosis);
                        oldNifOfDosisCollectionNewDosis = em.merge(oldNifOfDosisCollectionNewDosis);
                    }
                }
            }
            for (Usuario usuarioCollectionOldUsuario : usuarioCollectionOld) {
                if (!usuarioCollectionNew.contains(usuarioCollectionOldUsuario)) {
                    usuarioCollectionOldUsuario.setIdCuidador(null);
                    usuarioCollectionOldUsuario = em.merge(usuarioCollectionOldUsuario);
                }
            }
            for (Usuario usuarioCollectionNewUsuario : usuarioCollectionNew) {
                if (!usuarioCollectionOld.contains(usuarioCollectionNewUsuario)) {
                    Usuario oldIdCuidadorOfUsuarioCollectionNewUsuario = usuarioCollectionNewUsuario.getIdCuidador();
                    usuarioCollectionNewUsuario.setIdCuidador(usuario);
                    usuarioCollectionNewUsuario = em.merge(usuarioCollectionNewUsuario);
                    if (oldIdCuidadorOfUsuarioCollectionNewUsuario != null && !oldIdCuidadorOfUsuarioCollectionNewUsuario.equals(usuario)) {
                        oldIdCuidadorOfUsuarioCollectionNewUsuario.getUsuarioCollection().remove(usuarioCollectionNewUsuario);
                        oldIdCuidadorOfUsuarioCollectionNewUsuario = em.merge(oldIdCuidadorOfUsuarioCollectionNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuario.getNif();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getNif();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            Pastillero idPastillero = usuario.getIdPastillero();
            if (idPastillero != null) {
                idPastillero.getUsuarioCollection().remove(usuario);
                idPastillero = em.merge(idPastillero);
            }
            Usuario idCuidador = usuario.getIdCuidador();
            if (idCuidador != null) {
                idCuidador.getUsuarioCollection().remove(usuario);
                idCuidador = em.merge(idCuidador);
            }
            Collection<Dosis> dosisCollection = usuario.getDosisCollection();
            for (Dosis dosisCollectionDosis : dosisCollection) {
                dosisCollectionDosis.setNif(null);
                dosisCollectionDosis = em.merge(dosisCollectionDosis);
            }
            Collection<Usuario> usuarioCollection = usuario.getUsuarioCollection();
            for (Usuario usuarioCollectionUsuario : usuarioCollection) {
                usuarioCollectionUsuario.setIdCuidador(null);
                usuarioCollectionUsuario = em.merge(usuarioCollectionUsuario);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
