package com.kdillo.simple.db;

/*
 * Reference from https://www.vogella.com/tutorials/MySQLJava/article.html, modifying to fit with my db.
 *
 * This is for a simple database connection using MYSQL
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Date;
import java.util.Properties;

public class MySQLAccess {
    Logger LOGGER = LogManager.getLogger(this.getClass());

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public void readDataBase(Properties props) throws Exception {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            //localhost/schema?(credentials)
            String dbnameProp = props.getProperty("db.name");
            String dbname = dbnameProp == null ? "" : "/" + dbnameProp;
            String host = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            //construct url for mysql db connection using the property info...
            String url = "jdbc:mysql://"+host+dbname+"?user="+user+"&password="+password;

            //Set up the connection with the DB
            connect = DriverManager.getConnection(url, user, password);

            // SAMPLE STATEMENT FOR SELECTING; PLACE IN RESULT SET
            statement = connect.createStatement();
            resultSet = statement
                    .executeQuery("select * from scratchapp.user");
            writeResultSet(resultSet);

            //get date long value for
            Date defaultDate = new Date();

            //SAMPLE UPDATING TABLE WITH A VALUE
            PreparedStatement preparedStatement = connect
                    .prepareStatement("insert into  scratchapp.user values (?, ?, ?, ?)");
            preparedStatement.setString(1, "TestUsername");
            preparedStatement.setString(2, "TestEmail");
            preparedStatement.setString(3, "TestPassword123");
            preparedStatement.setDate(4, new java.sql.Date(defaultDate.getTime()));
            preparedStatement.executeUpdate();

            preparedStatement = connect
                    .prepareStatement("SELECT username, email, password, create_time from scratchapp.user");
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);

            // SAMPLE DELETE OF PREVIOUSLY INSERTED USER
            preparedStatement = connect
                    .prepareStatement("delete from scratchapp.user where username= ? ; ");
            preparedStatement.setString(1, "TestUsername");
            preparedStatement.executeUpdate();

            // SELECT ALL STATEMENT
            resultSet = statement
                    .executeQuery("select * from scratchapp.user");
            writeMetaData(resultSet);

        } catch (Exception e) {
            System.out.println("Failed to resolve all database access requests: " + e.getMessage());
            throw e;
        } finally {
            //be sure to clean variables used while calling to db.
            close();
        }

    }

    /**
     * Method for showing what the columns of the table being edited are.
     *  Temporary to show it's working on expected table
     * @param resultSet - passed SQL result set, used to display this info.
     * @throws SQLException if the results set doesn't contain what's expected.
     */
    private void writeMetaData(ResultSet resultSet) throws SQLException {
        //  Now get some metadata from the database
        // Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
        }
    }

    /**
     * Method for printing information returned from DB query to console.
     *  Temporary to understand how it's working.
     * @param resultSet a SQL returned result set containing user data from query
     * @throws SQLException if any of the columns or data aren't correct for that table.
     */
    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            String user = resultSet.getString("username");
            String email = resultSet.getString("email");
            Date date = resultSet.getDate("create_time");
            System.out.println("User: " + user);
            System.out.println("CreatedDate: " + date);
            System.out.println("Email: " + email);
        }
    }

    // Closing results to maintain that nothing bleeds over, others cleared for simplicity..
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}