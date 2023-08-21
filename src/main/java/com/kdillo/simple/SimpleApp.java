package com.kdillo.simple;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Properties;

import java.sql.*;
import java.util.UUID;

public class SimpleApp {

    private static final Logger LOGGER = LogManager.getLogger();

    static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    //initialize a random number generator when the app launches
    static SecureRandom rnd = new SecureRandom();

    public static void main(String[] args) throws Exception {

        try {
            DummyRun();
        } catch (SQLException exs) {
            exs.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return;

        //load app properties from config file.
//        try (InputStream configFileStream = SimpleApp.class.getClassLoader().getResourceAsStream("config.properties")) {
//            if (configFileStream == null) {
//                System.out.println("No props file found");
//                return;
//            }
//            Properties props = new Properties();
//            props.load(configFileStream);
//
//            properties = props;
//        } catch (IOException ex) {
//            //unable to load from properties file..
//            System.out.println("Never loaded props file, " + ex.getMessage());
//            LOGGER.error(ex.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (properties == null || properties.isEmpty()) {
//            //failed to load properties, exit app.
//            return;
//        }
//
//        DBAccess dbAccess = new DBAccess(properties);
//        dbAccess.readDataBase();
//
//        //spin wait for requests:

    }

    public static void DummyRun() throws Exception, SQLException {
        //just a dummy run to see if I can make the database access work.

        //set up properties for the db driver connection:
        Properties props = new Properties();
        props.setProperty("user", "kdill");
        props.setProperty("password", "testing123;DB");

        String url = "jdbc:postgresql://127.0.0.1:5432/simple?currentSchema=webapp";

        //connector:
        Connection conn = DriverManager.getConnection(url, props);

        //getting a user from the users table, print all the info from the rows returned.
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM users WHERE email = 'kdillo893@gmail.com'");
        while (rs.next()) {

            //getting values for each of the columns: uid, first_name, last_name, email, email, created, updated, pass_hash, pass_salt.

            String uid = rs.getString("uid");
            String fname = rs.getString("first_name");
            String lname = rs.getString("last_name");
            String email = rs.getString("email");
            Date created = rs.getDate("created");
            Date updated = rs.getDate("updated");
            String passHash = rs.getString("pass_hash");
            String passSalt = rs.getString("pass_salt");

            System.out.printf("uid=%s, fname=%s, lname=%s, email=%s, created=%s, updated=%s, passHash=%s, passSalt=%s\n", uid, fname, lname, email, created, updated, passHash, passSalt);
        }

        rs.close();
        st.close();

//        conn.close();

//        conn = DriverManager.getConnection(url, props);
        //testing to update the passHash of my user row with an update statement.rAN
        //make a password hash out of random salt
        String newSalt = randomString(32);
        String passHash = digestMessageWithAlg("silly-Thing123;xD", "SHA512");

        UUID myUid = UUID.fromString("7d95e88a-f10f-4b20-8303-e26db72ddd74");
        PreparedStatement pst = conn.prepareStatement("update users set pass_hash = ?, pass_salt = ? where uid = ? ;;");



        pst.setString(1, passHash);
        pst.setString(2, newSalt);
        pst.setObject(3, myUid);


        int rowsUpdated = pst.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.printf("rowsUpdated=%d, passHash=%s, passSalt=%s, UUID=%s", rowsUpdated, passHash, newSalt, myUid);
        }

        //what does update return? not sure.
//        while (rs.next()) {
//            // just printing the first column, then
//            System.out.printf("uid=%s", rs.getString("uid"));
//            System.out.printf("passHash=%s", rs.getString("pass_hash"));
//            System.out.printf("pass_salt=%s", rs.getString("pass_salt"));
////            System.out.printf("uid=%s", rs.getString("uid"));
////            System.out.printf("uid=%s", rs.getString("uid"));
////            System.out.printf("uid=%s", rs.getString("uid"));
//            System.out.println();
//        }

        rs.close();
        st.close();

    }


    static String digestMessageWithAlg(String message, String alg) {

        try {
            MessageDigest md = MessageDigest.getInstance(alg);

            byte[] messageDigest = md.digest(message.getBytes());

            //convert byte into signum representation? seems like this is for proper char bytesize conversion.
            //converts the very large value into string simply
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashtextSb = new StringBuilder(no.toString(16));

            //if the hash is too small, fill with 0's
            while(hashtextSb.length() < 32) hashtextSb.insert(0, '0');

            return hashtextSb.toString();

        } catch (NoSuchAlgorithmException ex) {
            System.err.printf("Couldn't find algorithm: %s", alg);
            throw new RuntimeException(ex);
        } catch (Exception ex) {
           // unknown
        }


        return null;
    }

    static String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(ALPHANUMERIC.charAt(rnd.nextInt(ALPHANUMERIC.length())));
        return sb.toString();
    }
}
