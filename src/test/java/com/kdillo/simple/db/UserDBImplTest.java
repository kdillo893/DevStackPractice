package com.kdillo.simple.db;

import com.kdillo.simple.entities.User;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import org.junit.*;
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
        Properties properties = new Properties();
        PostgresqlConnectionProvider connectionProvider = new PostgresqlConnectionProvider(properties);
        userDbImpl = new UserDBImpl(connectionProvider);

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
    public void testGetPasswordSalt() {
        System.out.println("getPasswordSalt");
        User user = new User();

        //no UUID, can't query for the user. Optional should be empty.
        Optional<User> expected = Optional.empty();
        Optional<User> result = userDbImpl.getPasswordSalt(user);
        assertEquals(expected, result);

        //user.uid = UUID.randomUUID();
    }

    @Test
    public void testDeleteById() {
         
        //need valid IDs to delete.
        boolean result = userDbImpl.deleteById(null);
        assertEquals(false, result);

    }


    @Test
    public void testUpdate() throws Exception {

        User user = null;
        //need valid IDs to update.
        boolean expected = false;
        boolean result = userDbImpl.update(user);
        assertEquals(expected, result);

        //missing editable columns
        user = new User();

        result = userDbImpl.update(user);
        assertEquals(expected, result);
    }

    @Test
    public void testAdd() throws Exception {

        User user = null;
        //need valid user to add.
        boolean expected = false;
        boolean result = userDbImpl.update(user);
        assertEquals(expected, result);

        //missing needed info for creating a user
        user = new User();

        result = userDbImpl.update(user);
        assertEquals(expected, result);
    }

}
