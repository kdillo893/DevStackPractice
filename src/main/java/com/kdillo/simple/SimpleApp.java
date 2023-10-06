package com.kdillo.simple;

import com.kdillo.simple.db.PostgresqlConnectionProvider;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.Document;
import com.kdillo.simple.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

/**
 * Running simple things in main to test and verify
 * @author kdill
 */
public class SimpleApp {
    private static final Logger LOGGER = LogManager.getLogger(SimpleApp.class);

    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    //initialize a random number generator when the app launches
    public static final SecureRandom rnd = new SecureRandom();

    private static Properties props = null;
    public static PostgresqlConnectionProvider pgConProvider = null;

    public static void main(String[] args) throws InterruptedException {

        try {
            
            LOGGER.info("something logged");
            loadApplicationProperties();
            LOGGER.info("app settings loaded");

            //TODO: figure out how to use datastore for postgresql instead of abstracted thing.
//            PGSimpleDataSource ds = new PGSimpleDataSource();

            //abstracted connection provider, which spins up new DB connections;
            pgConProvider = new PostgresqlConnectionProvider(props);

//            SampleUserTest(pgConProvider);

            LOGGER.info("Starting to write and persist a document");
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("simple");
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            entityManager.getTransaction().begin();

            Document document = new Document("https://github.com/kdillo893/simplestFrontToBack/blob/main/README.md");

            entityManager.persist(document);
            entityManager.getTransaction().commit();
            
            
            LOGGER.info("Document written: {}", document);
            
            //main loop
//            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
//            server.createContext("/sample", new MyHandler());
//            server.setExecutor(null);
//            server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void SampleUserTest(PostgresqlConnectionProvider pgConProvider) {

        UserDBImpl userDbImpl = new UserDBImpl(pgConProvider);

        //get a user by ID
        Optional<User> optionalUser = userDbImpl.getById(UUID.fromString("7d95e88a-f10f-4b20-8303-e26db72ddd74"));
        System.out.println(optionalUser);


        //overwrite the first name of the user from above (if exists) to "Tyler" if "Kevin" and vice versa
        if (optionalUser.isPresent()) {
            User theUser = optionalUser.get();

            if (theUser.getFirstName().equals("Kevin")) {
                theUser.setFirstName("Tyler");
            } else if (theUser.getFirstName().equals("Tyler")) {
                theUser.setFirstName("Kevin");
            }

            try {
                boolean wasUpdated = userDbImpl.update(theUser);

                if (wasUpdated) {
                    System.out.println("The user was updated");
                } else {
                    System.out.println("the user was NOT updated.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        User testUser = new User();
        //find users matching a certain criteria on User object
        testUser.setLastName("Dillon");
        List<User> users = userDbImpl.getAll(testUser);
        System.out.println(users);

        //add and delete a user.
        User newUser = new User("Test", "Person", "beareamis@gmail.com", "testPassword123");
        UUID newUserId = userDbImpl.add(newUser);
        System.out.printf("user created: uuid=%s\n", newUserId.toString());


        try {
            boolean userWasDeleted = userDbImpl.deleteById(newUserId);
            System.out.printf("user %s deleted: uuid=%s\n",
                    userWasDeleted ? "was" : "was NOT",
                    newUserId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHANUMERIC.charAt(rnd.nextInt(ALPHANUMERIC.length())));
        }

        LOGGER.debug("random string produced: " + sb);

        return sb.toString();
    }

    private static void loadApplicationProperties() {

        try (InputStream propsStream = new FileInputStream("src/main/resources/config.properties")) {
            props = new Properties();

            props.load(propsStream);
        } catch (IOException ex) {
            LOGGER.debug("Unable to load app properties");
            ex.printStackTrace();
        }
    }
}
