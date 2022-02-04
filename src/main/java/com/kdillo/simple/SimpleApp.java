package com.kdillo.simple;

import com.kdillo.simple.db.MySQLAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SimpleApp {

    private static Properties properties = null;
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws Exception {

        //load app properties from config file.
        try (InputStream configFileStream = SimpleApp.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (configFileStream == null) {
                System.out.println("No props file found");
                return;
            }
            Properties props = new Properties();
            props.load(configFileStream);

            properties = props;
        } catch (IOException ex) {
            //unable to load from properties file..
            System.out.println("Never loaded props file, " + ex.getMessage());
            LOGGER.error(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (properties == null || properties.isEmpty()) {
            //failed to load properties, exit app.
            return;
        }


        MySQLAccess dbAccess = new MySQLAccess(properties);
        dbAccess.readDataBase();
    }
}
