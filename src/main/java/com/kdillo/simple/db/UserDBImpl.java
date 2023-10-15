package com.kdillo.simple.db;

import com.kdillo.simple.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    private List<User> getUsersFromResultSet(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();

        while (resultSet.next()) {

            //getting all the columns and setting user object.
            User aUser = new User(UUID.fromString(resultSet.getString(1)),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getDate(5),
                    resultSet.getDate(6),
                    resultSet.getString(7),
                    resultSet.getString(8));

            users.add(aUser);
        }

        return users;
    }

    public List<User> getAll(User user, int offset, int limit) {
        return getAll(user, offset, limit, null, null);
    }

    public List<User> getAll(User user, Date before, Date after) {
        return getAll(user, 0, RETRIEVE_LIMIT, before, after);
    }

    public List<User> getAll(User user, int offset, int limit, Date before, Date after) {
        List<User> resultList = new ArrayList<>();

        try {
            Connection conn = this.connectionProvider.getConnection();

            int parmCount = 0;

            //prepare the "select" query
            LOGGER.info("Preparing getAllUsers with parameters from User: {}", user.toString());
            String theQuery = "SELECT uid, first_name, last_name, email, created, updated, pass_hash, pass_salt FROM users";

            Map<Integer, Object> queryWhereMap = new HashMap<>(); 
            if (user.email != null) {
                if (parmCount == 0)
                    theQuery += " WHERE ";
                else 
                    theQuery += " AND ";
                theQuery += " email = ?";

                parmCount++;
                queryWhereMap.put(parmCount, user.email);
            }

            if (user.last_name != null) {
                if (parmCount == 0)
                    theQuery += " WHERE ";
                else 
                    theQuery += " AND ";
                theQuery += " last_name = ?";

                parmCount++;
                queryWhereMap.put(parmCount, user.last_name);
            }

            if (user.first_name != null) {
                if (parmCount == 0)
                    theQuery += " WHERE ";
                else 
                    theQuery += " AND ";
                theQuery += " first_name = ?";

                parmCount++;
                queryWhereMap.put(parmCount, user.first_name);
            }

            if (before != null) {
                if (parmCount == 0)
                    theQuery += " WHERE ";
                else 
                    theQuery += " AND ";
                theQuery += " created < ?";

                parmCount++;
                queryWhereMap.put(parmCount, before);
            }
            if (after != null) {
                if (parmCount == 0)
                    theQuery += " WHERE ";
                else 
                    theQuery += " AND ";
                theQuery += " created > ?";

                parmCount++;
                queryWhereMap.put(parmCount, after);
            }
            theQuery+= " LIMIT " + limit + " OFFSET " + offset;

            //query prep'd, prepareStatement to fill segments
            PreparedStatement pStatement = conn.prepareStatement(theQuery);
            for (Map.Entry<Integer,Object> whereEntry : queryWhereMap.entrySet()) {
                if (whereEntry.getValue() instanceof String) {
                    pStatement.setString(whereEntry.getKey(), (String) whereEntry.getValue());
                } else if (whereEntry.getValue() instanceof Date) {
                    pStatement.setDate(whereEntry.getKey(), (Date) whereEntry.getValue());
                } else {
                    pStatement.setObject(whereEntry.getKey(), whereEntry.getValue());
                }
            }

            LOGGER.info("prepared getAll: {}", pStatement.toString());

            ResultSet resultSet = pStatement.executeQuery();

            resultList = getUsersFromResultSet(resultSet);
        } catch (SQLException sqlException) {
            LOGGER.debug("SQL Exception, bad query getting all Users");
            sqlException.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultList;
    }

    /**
     * default query by user, no offset or retrieve limit specified
     */
    public List<User> getAll(User user) {
        return getAll(user, 0, RETRIEVE_LIMIT);
    }

    /**
     * getAll without any logic for filtering by users; no offset/limit
     */
    public List<User> getAll() {

        try {
            Connection conn = this.connectionProvider.getConnection();

            String sqlQuery = "SELECT uid, first_name, last_name, email, created, updated, pass_hash, pass_salt FROM users" +
                " LIMIT " + RETRIEVE_LIMIT + " ;";
            PreparedStatement pStatement = conn.prepareStatement(sqlQuery);

            ResultSet resultSet = pStatement.executeQuery();

            return getUsersFromResultSet(resultSet);
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

            if (resultSet.next()) {
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

        if (!obj.hasPassword() || obj.email == null)
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
            pStatement.setString(1, obj.first_name);
            pStatement.setString(2, obj.last_name);
            pStatement.setString(3, obj.email);
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
        if (obj.uid == null)
            return false;

        //if no updatable attributes to set, return false
        if (!(obj.last_name != null
                    || obj.first_name != null
                    || obj.email != null
                    || obj.hasPassword())
           )
            return false;

        Optional<User> userInDb = this.getById(obj.uid);
        if (userInDb.isEmpty())
            return false;

        User updateUserObj = userInDb.get();

        obj.uid = updateUserObj.uid;
        if (obj.first_name == null) obj.first_name = updateUserObj.first_name;
        if (obj.last_name == null) obj.last_name = updateUserObj.last_name;
        if (obj.email == null) obj.email = updateUserObj.email;
        if (obj.hasPassword()) {
            obj.calculatePassHashWithNewSalt();
        }

        try {
            Connection conn = this.connectionProvider.getConnection();
            conn.setAutoCommit(false);
            String statementString = "UPDATE " + USERS_TABLE_NAME +
                " SET" +
                " (first_name, last_name, email, updated)" +
                " = (?, ?, ?, now())" +
                " WHERE uid = ?";
            PreparedStatement pStatement = conn.prepareStatement(statementString);

            pStatement.setString(1, obj.first_name);
            pStatement.setString(2, obj.last_name);
            pStatement.setString(3, obj.email);
            pStatement.setObject(4, obj.uid);

            int effectedLines = pStatement.executeUpdate();

            //are we also updating the password?
            if (obj.hasPassword()) {
                statementString = "UPDATE " + USERS_TABLE_NAME +
                    " SET (pass_hash, pass_salt, updated) = (?, ?, now()) WHERE uid = ?";
                pStatement = conn.prepareStatement(statementString);
                pStatement.setString(1, obj.getPassHash());
                pStatement.setString(2, obj.getPassSalt());
                pStatement.setObject(3, obj.uid);
            }

            //autocommit is enabled, can turn it off if I want.
            conn.commit();
            conn.setAutoCommit(true);

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
        return deleteById(obj.uid);
    }

    public boolean deleteById(UUID uuid) {

        if (uuid == null)
            return false;

        if (uuid == ADMIN_USER_ID)
            return false;

        try {
            Connection conn = this.connectionProvider.getConnection();
            conn.setAutoCommit(true);
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM users WHERE uid = ?");
            preparedStatement.setObject(1, uuid);

            boolean execResult = preparedStatement.execute();
            int updatedCount = preparedStatement.getUpdateCount();

            return updatedCount >= 0;

        } catch (SQLException sqlException) {
            LOGGER.debug("SQL Exception, meaning bad query.");
            sqlException.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
