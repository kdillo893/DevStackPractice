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
public class UserDAO{

    private static final int RETRIEVE_LIMIT = 100;

    private static final String USERS_TABLE_NAME = "users";
    private static final Set<String> COLUMNS = Set.of(
            "uid", "first_name", "last_name", "email",
            "created", "updated", "pass_hash", "pass_salt");

    User user;

    private static final Logger LOGGER = LogManager.getLogger(UserDAO.class);

    private final PostgresqlConnectionProvider connectionProvider;

    public UserDAO(User user, PostgresqlConnectionProvider connectionProvider) {
        this.user = user;
        this.connectionProvider = connectionProvider;
    }

    public User getUserData() {
        return user;
    }

    public void setUserData(User user) {
        this.user = user;
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
            PreparedStatement pstatement = conn.prepareStatement("SELECT uid, first_name, last_name, email, created, updated, pass_hash, pass_salt FROM users WHERE uid = ? LIMIT 1;");
            pstatement.setObject(1, uuid);

            ResultSet rs = pstatement.executeQuery();

            while (rs.next()) {
                //getting all the columns and setting user object.
                user = new User(UUID.fromString(rs.getString(1)),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getDate(5),
                        rs.getDate(6),
                        rs.getString(7),
                        rs.getString(8));
            }

            return Optional.ofNullable(user);
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

    public boolean add(User obj) throws Exception {
        return false;
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
        return false;
    }

    public boolean deleteById(UUID uuid) throws Exception {
        return false;
    }
}
