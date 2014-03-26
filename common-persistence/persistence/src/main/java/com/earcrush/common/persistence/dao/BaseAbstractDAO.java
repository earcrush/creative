/* Copyright (C) 2009 ItsOn, Inc. */
package com.earcrush.common.persistence.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * BaseAbstractDAO implements the abstract DAO interface
 * and presents the base class for concrete DAO implementation.
 *
 * @since Nov 19, 2009
 * @author justin
 */
public abstract class BaseAbstractDAO<K, E> implements AbstractDAO<K, E> 
{
    protected Class<E> entityClass;
    
    @PersistenceContext
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<E> getAll()
    {
        String query = "SELECT o from " + entityClass.getSimpleName() + " o";
        return entityManager.createQuery(query).getResultList();
    }

    @SuppressWarnings({ "unchecked" })
    public BaseAbstractDAO() 
    {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
    }
    
    public void setEntityManager(EntityManager manager)
    {
        this.entityManager = manager;
    }

    public E persist(E entity) 
    {
        entityManager.persist(entity);
        return entity;
    }

    public void remove(E entity) 
    {
        entityManager.remove(entity);
    }
    
    public E refresh(E entity) 
    {
        entityManager.refresh(entity);
        return entity;
    }

    public E findById(K id) 
    {
        return entityManager.find(entityClass, id);
    }
    
    public E getReference(K id) 
    {
        return entityManager.getReference(entityClass, id);
    }
    
    public void flush()
    {
        entityManager.flush();
    }

    public void merge(E entity)
    {
        entityManager.merge(entity);
    }
    
    @SuppressWarnings("unchecked")
    protected E findSingleEntity(Query query)
    {
        try
        {
            return (E) query.getSingleResult();
        }
        catch (NoResultException e)
        {
            return null;
        }
    }
    
    protected Object getSingleResult(Query query)
    {
        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    protected List<E> getResultList(Query query)
    {
        return query.getResultList();
    }
}
