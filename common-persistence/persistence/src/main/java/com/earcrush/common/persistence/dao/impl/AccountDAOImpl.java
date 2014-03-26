/* Copyright (C) 2012 ItsOn, Inc. */

package com.earcrush.common.persistence.dao.impl;

import com.earcrush.common.persistence.dao.AccountDAO;
import com.earcrush.common.persistence.dao.BaseAbstractDAO;
import com.earcrush.common.persistence.model.Account;
import javax.persistence.Query;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AccountDAOImpl
 *
 * @since Jan 27, 2012
 * @author chudak
 */
@Repository("accountDao")
public class AccountDAOImpl extends BaseAbstractDAO<Long, Account> implements AccountDAO {

    /* (non-Javadoc)
     * @see com.itsoninc.das.common.persistence.dao.AccountDAO#findByEmail(java.lang.String)
     */
    public Account findByEmail(String email)
    {
        Validate.notNull(email, "Email is null");
        
        Query q = entityManager.createQuery("SELECT a from Account a where lower(email) = :email");
        q.setParameter("email", email.toLowerCase());
        return findSingleEntity(q);
    }
}
