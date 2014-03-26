/* Copyright (C) 2011 ItsOn, Inc. */

package com.earcrush.common.persistence.dao;

import com.earcrush.common.persistence.model.Account;

/**
 * AccountDAO
 *
 * @since Jan 27, 2012
 * @author chudak
 */
public interface AccountDAO extends AbstractDAO<Long, Account>
{
    public Account findByEmail(String email);
}
