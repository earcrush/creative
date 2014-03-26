/* Copyright ItsOn Inc. 2012 */
package com.earcrush.common.persistence.dao.impl;

import com.earcrush.common.persistence.model.Account;
import java.util.Date;
import junit.framework.Assert;

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import test.FunctionalTestBase;


/**
 * Functional test
 *
 * @author chudak
 * @since Mar7 , 2012
 *
 */
public class AccountDAOImplFunctionalTest extends FunctionalTestBase
{
    @Test
    public void testUnmarshall() {
        Account account = createAccount("me@here.com");
        accountDao.refresh(account);
    }
    
    @Test(expected=DataIntegrityViolationException.class)
    public void testUniqueEmail()
    {
        createAccount("me@here.com");
        createAccount("me@here.com");
    }
    
    @Test(expected=DataIntegrityViolationException.class)
    public void testUniqueEmailDifferentCase()
    {
        createAccount("me@here.com");
        createAccount("ME@HERE.COM");
    }
    
    @Test
    public void testFindByEmail()
    {
        Account account = createAccount("me@here.com");
        Account retrieved = accountDao.findByEmail("me@here.com");
        Assert.assertNotNull(retrieved);
        Assert.assertEquals(account.getAccountId(), retrieved.getAccountId());
    }
    
    @Test
    public void testFindByEmailDifferentCase()
    {
        Account account = createAccount("me@here.com");
        Account retrieved = accountDao.findByEmail("ME@HERE.COM");
        Assert.assertNotNull(retrieved);
        Assert.assertEquals(account.getAccountId(), retrieved.getAccountId());
    }    
    
    protected Account createAccount(String email) {
        Account account = new Account();
        account.setFirstName("Ear");
        account.setLastName("Crush");
        account.setEmail(email);
        account.setCreateDate(new Date());
        account.setPassword("Password");
        accountDao.persist(account);
        accountDao.flush();
        return account;
    }
            
    protected Account createAccount(Account account)
    {        
        accountDao.persist(account);
        accountDao.flush();
        Assert.assertNotNull(account.getAccountId());
        return account;
    }

}
