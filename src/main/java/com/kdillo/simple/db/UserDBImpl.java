package com.kdillo.simple.db;

import com.kdillo.simple.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unused")
public class UserDBImpl {
    private static final Logger LOGGER = LogManager.getLogger(UserDBImpl.class);

    private static final int RETRIEVE_LIMIT = 100;

    private static final String USERS_TABLE_NAME = "users";


    //TODO: figure out if there's a way to protect deleting things from db side.
    private static final UUID ADMIN_USER_ID = UUID.fromString("f7062c7a-d3ec-485c-898d-cca6ade0512a");

    private final PostgresqlConnectionProvider connectionProvider;

    public UserDBImpl(PostgresqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<User> getAll(User obj) {

        try {
            Connection conn = this.connectionProvider.getConnection();

            //TODO: insert where logic depending on the parameters
            String sqlQuery = "SELECT uid, first_name, last_name, email, created, updated, pass_hash, pass_salt FROM users" +
                    " " +
                    " LIMIT " + RETRIEVE_LIMIT + " ;";
            PreparedStatement pStatement = conn.prepareStatement(sqlQuery);

            ResultSet rs = pStatement.executeQuery();

            List<User> theList = new ArrayList<>();
            while (rs.next()) {

                //getting all the columns and setting user object.
                User aUser = new User(UUID.fromString(rs.getString(1)),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getDate(5),
                        rs.getDate(6),
                        rs.getString(7),
                        rs.getString(8));

                theList.add(aUser);
            }

            return theList;
        } catch (SQLException sqlException) {
            LOGGER.debug("SQL Exception, bad query getting all Users");
            sqlException.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ArrayList<>();
    }

    public Optional<User> getById(UUID uuid) {

        try {
            Connection conn = this.connectionProvider.getConnection();
            conn.setAutoCommit(true);
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT uid, first_name, last_name, email, created, updated, pass_hash, pass_salt FROM users WHERE uid = ? LIMIT 1;");
            preparedStatement.setObject(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                //getting all the columns and setting user object.
                return Optional.of(new User(UUID.fromString(resultSet.getString(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5),
                        resultSet.getDate(6),
                        resultSet.getString(7),
                        resultSet.getString(8)));
            }
        } catch (SQLException sqlException) {
            LOGGER.debug("SQL Exception, meaning bad query.");
            sqlException.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<User> getPasswordSalt(User user) {

        return Optional.empty();
    }

    public UUID add(User obj) {

        if (!obj.hasPassword() || obj.getEmail() == null)
            return null;

        obj.calculatePassHashWithNewSalt();

        try {
            Connection conn = this.connectionProvider.getConnection();
            String columnsCommaSeparated = String.format("(%s, %s, %s, %s, %s, %s, %s, %s)",
                    "uid", "first_name", "last_name", "email", "created", "updated", "pass_hash", "pass_salt");
            String statementString = "INSERT INTO " + USERS_TABLE_NAME +
                    columnsCommaSeparated +
                    " VALUES (default, ?, ?, ?, now(), now(), ?, ?)" +
                    " RETURNING uid";
            PreparedStatement pStatement = conn.prepareStatement(statementString);

            //setting attributes of prepared statement 1=fn, 2=ln, 3=email, 4=uid
            pStatement.setString(1, obj.getFirstName());
            pStatement.setString(2, obj.getLastName());
            pStatement.setString(3, obj.getEmail());
            pStatement.setString(4, obj.getPassHash());
            pStatement.setString(5, obj.getPassSalt());

            ResultSet resultSet = pStatement.executeQuery();

            //check if we're returning a row, this should contain the UUID.
            while (resultSet.next()) {
                UUID userId = (UUID) resultSet.getObject(1);

                if (userId != null)
                    return userId;
            }
        } catch (SQLException sqlException) {
            LOGGER.debug("SQLException update on user");
            sqlException.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public boolean update(User obj) throws Exception {

        //guard clauses:
        //if no UID return false.
        if (obj.getUid() == null)
            return false;

        //TODO: add update to password logic (needs some stuff to generate hash)
        //if no updatable attributes to set, return false
        if ( !(obj.getLastName() != null
                || obj.getFirstName() != null
                || obj.getEmail() != null)
        )
            return false;

        //retrieve the user info, update if exists
        Optional<User> userInDb = this.getById(obj.getUid());
        if (userInDb.isEmpty())
            return false;

        User updateUserObj = userInDb.get();
        if (obj.getFirstName() != null) updateUserObj.setFirstName(obj.getFirstName());
        if (obj.getLastName() != null) updateUserObj.setLastName(obj.getLastName());
        if (obj.getEmail() != null) updateUserObj.setEmail(obj.getEmail());


        try {
            Connection conn = this.connectionProvider.getConnection();
            String statementString = "UPDATE " + USERS_TABLE_NAME +
                    " SET" +
                    " (first_name, last_name, email, updated)" +
                    " = (?, ?, ?, now() )" +
                    " WHERE uid = ?";
            PreparedStatement pStatement = conn.prepareStatement(statementString);

            //setting attributes of prepared statement 1=fn, 2=ln, 3=email, 4=uid
            pStatement.setString(1, obj.getFirstName());
            pStatement.setString(2, obj.getLastName());
            pStatement.setString(3, obj.getEmail());
            pStatement.setObject(4, obj.getUid());

            int effectedLines = pStatement.executeUpdate();

            //autocommit is enabled, can turn it off if I want.
//            conn.commit();

            return effectedLines >= 0;
        } catch (SQLException sqlException) {
            LOGGER.debug("SQLException update on user");
            sqlException.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean delete(User obj) throws Exception {
        return deleteById(obj.getUid());
    }

    public boolean deleteById(UUID uuid) throws Exception {

        if (uuid == null)
            return false;

        if (uuid == ADMIN_USER_ID)
            return false;

        try {
            Connection conn = this.connectionProvider.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM users WHERE uid = ?");
            preparedStatement.setObject(1, uuid);

            return preparedStatement.execute();
        } catch (SQLException sqlException) {
            LOGGER.debug("SQL Exception, meaning bad query.");
            sqlException.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
