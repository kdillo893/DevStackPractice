package com.kdillo.simple;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author kdill
 */
public class SimpleAppTest {

    public SimpleAppTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of randomString method, of class SimpleApp.
     */
    @Test
    public void testRandomString() {
        System.out.println("randomString");
        int len = 0;
        String expResult = "";
        String result = SimpleApp.randomString(len);
        assertEquals(expResult, result);

        len = 128;
        result = SimpleApp.randomString(len);
        assertEquals(128, result.length());

        System.out.println("theRandomString= " + result);

        //TODO assert the randomness of the string is valid; secure random should be ok
    }

}
