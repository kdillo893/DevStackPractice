package com.kdillo.simple.db;

import com.kdillo.simple.entities.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kdill
 */
public class UserDBImplTest {

    private static UserDBImpl userDbImpl;

    public UserDBImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        //TODO: set up a connection provider that provides a connection to a test db
    }

    @AfterClass
    public static void tearDownClass() {
        userDbImpl = null;
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getPasswordSalt method, of class UserDBImpl.
     */
    @Test
    @Ignore
    public void testGetPasswordSalt() {
        System.out.println("getPasswordSalt");

        User user = new User();
        user.uid = UUID.randomUUID();

        Optional<User> expResult = null;
        Optional<User> result = userDbImpl.getPasswordSalt(user);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
