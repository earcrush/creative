/* Copyright (C) 2009 ItsOn, Inc. */
package com.earcrush.common.persistence.dao;

import java.util.List;

/**
 * AbstractDAO is the interface for all JPA based
 * DAO services.
 *
 * @since Nov 19, 2009
 * @author justin
 */
public interface AbstractDAO<K, E> 
{
    /** persist the entity */
    E persist(E entity);

    /** remove the entity from the session */
    void remove(E entity);

    /** find entity by technical key */
    E findById(K id);
    
    /** get reference to entity by technical key */
    E getReference(K id);
    
    /** find all entities */
    List<E> getAll();

    /** merge the entity - user for updating detached objects */
    void merge(E entity);
    
    /** refresh the entity - reload from database */
    E refresh(E entity);
    
    /** flushes pending changes */
    void flush();
}
