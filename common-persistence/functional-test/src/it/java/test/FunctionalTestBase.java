/* Copyright ItsOn Inc. 2009 */
package test;

import com.earcrush.common.persistence.dao.AccountDAO;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


/**
 * Base for all persistence functional tests.
 *
 * @author Charles Hudak
 * @since Nov 25, 2009
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public abstract class FunctionalTestBase {
    
    @Autowired
    protected AccountDAO accountDao;
}
