package com.kdillo.simple.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author kdill
 */
public class UserTest {
    
    public UserTest() {
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
     * Test of hasPassword method, of class User.
     */
    @Test
    public void testHasPassword() {
        System.out.println("hasPassword");
        User instance = new User();
        boolean expResult = false;
        boolean result = instance.hasPassword();
        assertEquals(expResult, result);
        
        instance.setPassword("someFakePassword123");
        expResult = true;
        result = instance.hasPassword();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of generateNewSalt method, of class User.
     */
    @Test
    public void testGenerateNewSalt() {
        System.out.println("generateNewSalt");
        User user = new User();
        
        assertEquals(null, user.getPassSalt());
        
        user.generateNewSalt();
        
        //a new salt should be 32char.
        String theSalt = user.getPassSalt();
        assertEquals(32, theSalt.length());
        
        //salts shouldn't be similar, so see if generating a new one is different.
        user.generateNewSalt();
        String newSalt = user.getPassSalt();
        
        assertNotEquals(theSalt, newSalt);
    }

    /**
     * Test of calculatePassHashWithNewSalt method, of class User.
     */
    @Test
    public void testCalculatePassHashWithNewSalt() {
        System.out.println("calculatePassHashWithNewSalt");
        User testUser = new User();

        String testPassword = "testPassword123$#xD";
        testUser.setPassword(testPassword);
        testUser.calculatePassHashWithNewSalt();

        //first, the testUser should have a salt and hash.
        String theSalt = testUser.getPassSalt();
        String theHash = testUser.getPassHash();
        assertNotNull(theSalt);
        assertNotNull(theHash);

        //second, the hash should be something that isn't just a digest of the password
        try {
            MessageDigest sha512md = MessageDigest.getInstance("SHA-512"); 
            MessageDigest sha256md = MessageDigest.getInstance("SHA-256"); 
            MessageDigest sha1md = MessageDigest.getInstance("SHA-1"); 
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            byte[] testPwBytes = testPassword.getBytes();

            byte[] outBytes = sha512md.digest(testPwBytes);
            BigInteger hashAsBigNum = new BigInteger(1, outBytes);
            StringBuilder hashTextSb = new StringBuilder(hashAsBigNum.toString(16));
            //if the hash is too small, fill with 0's
            while (hashTextSb.length() < 32) hashTextSb.insert(0, '0');
            String hashOfPass = hashTextSb.toString();
            System.out.println("has sha512: " + hashOfPass);
            assertNotEquals(theHash, hashOfPass);

            outBytes = sha256md.digest(testPwBytes);
            hashAsBigNum = new BigInteger(1, outBytes);
            hashTextSb = new StringBuilder(hashAsBigNum.toString(16));
            //if the hash is too small, fill with 0's
            while (hashTextSb.length() < 32) hashTextSb.insert(0, '0');
            hashOfPass = hashTextSb.toString();
            System.out.println("has sha256: " + hashOfPass);
            assertNotEquals(theHash, hashOfPass);

            outBytes = sha1md.digest(testPwBytes);
            hashAsBigNum = new BigInteger(1, outBytes);
            hashTextSb = new StringBuilder(hashAsBigNum.toString(16));
            //if the hash is too small, fill with 0's
            while (hashTextSb.length() < 32) hashTextSb.insert(0, '0');
            hashOfPass = hashTextSb.toString();
            System.out.println("has sha1: " + hashOfPass);
            assertNotEquals(theHash, hashOfPass);

            outBytes = md5.digest(testPwBytes);
            hashAsBigNum = new BigInteger(1, outBytes);
            hashTextSb = new StringBuilder(hashAsBigNum.toString(16));
            //if the hash is too small, fill with 0's
            while (hashTextSb.length() < 32) hashTextSb.insert(0, '0');
            hashOfPass = hashTextSb.toString();
            System.out.println("has md5: " + hashOfPass);
            assertNotEquals(theHash, hashOfPass);

            System.out.printf("resultHash: %s\n", theHash);
            
        } catch (Exception ex) {
            fail("reached exception state trying to hash passwords");
        }

        //third, one hash shouldn't be the same with different salts.
        testUser.calculatePassHashWithNewSalt();

        //first, the testUser should have a new salt and differing hash
        String newSalt = testUser.getPassSalt();
        String newHash = testUser.getPassHash();
        assertNotEquals(theSalt, newSalt);
        assertNotEquals(theHash, newHash);

        //could measure the "difference" by converting the hex string into bits.
    }
    
}
