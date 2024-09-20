package com.kdillo.simple.db;

import com.kdillo.simple.entities.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDBImpl {
    private static final Logger LOGGER = LogManager.getLogger(UserDBImpl.class);

    private static final int RETRIEVE_LIMIT = 100;

    private static final String USERS_TABLE_NAME = "users";

	// TODO: figure out if there's a way to protect deleting things from db side.
	// Also should probably have this in an external place or just enforced in DB-land
    private static final UUID ADMIN_USER_ID = UUID.fromString("f7062c7a-d3ec-485c-898d-cca6ade0512a");

    private final PostgresqlConnectionProvider connectionProvider;

    public UserDBImpl(PostgresqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    private List<User> getUsersFromResultSet(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();

        while (resultSet.next()) {

            //getting all the columns and setting user userect.
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

			// TODO: make the optional where's more condensed... there's definitely a better way
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
                if (whereEntry.getValue() instanceof String string) {
                    pStatement.setString(whereEntry.getKey(), string);
                } else if (whereEntry.getValue() instanceof Date date) {
                    pStatement.setDate(whereEntry.getKey(), date);
                } else {
                    pStatement.setObject(whereEntry.getKey(), whereEntry.getValue());
                }
            }

            LOGGER.info("prepared getAll: {}", pStatement.toString());

            ResultSet resultSet = pStatement.executeQuery();

            resultList = getUsersFromResultSet(resultSet);
        } catch (SQLException sqlException) {
            LOGGER.debug("SQL Exception, bad query getting all Users");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return resultList;
    }

    /**
     * default query by user, no offset or retrieve limit specified
     * @param user
     * @return 
     */
    public List<User> getAll(User user) {
        return getAll(user, 0, RETRIEVE_LIMIT);
    }

    /**
	 * getAll without any logic for filtering by users; b/c no offset/limit, have hard cap.
	 * could this just do "getAll(new User())" ? yes but want to avoid logic for certain ops.
     * @return 
     */
    public List<User> getAll() {

        try {
            Connection conn = this.connectionProvider.getConnection();

			String sqlQuery = "SELECT uid, first_name, last_name, email, created, updated, pass_hash, pass_salt FROM users"
					+ " LIMIT " + RETRIEVE_LIMIT + " ;";
            PreparedStatement pStatement = conn.prepareStatement(sqlQuery);

            ResultSet resultSet = pStatement.executeQuery();

            return getUsersFromResultSet(resultSet);
        } catch (SQLException sqlException) {
            LOGGER.debug("SQL Exception, bad query getting all Users");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return new ArrayList<>();
    }

    public Optional<User> getById(UUID uuid) {

        try {
            Connection conn = this.connectionProvider.getConnection();
            conn.setAutoCommit(true);
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT uid, first_name, last_name, email, created, updated, pass_hash, pass_salt FROM users WHERE uid = ? LIMIT 1;");
            preparedStatement.setObject(1, uuid);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                //getting all the columns and setting user userect.
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
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

	//TODO: didn't implement. what the hell was this?
    public Optional<User> getPasswordSalt(User user) {

        return Optional.empty();
    }

	//TODO: make an "add and return"
    public UUID add(User user) {

        if (user == null || !user.hasPassword() || user.email == null)
            return null;

        user.calculatePassHashWithNewSalt();

        try {
			String columnsCommaSeparated = String.format("(%s, %s, %s, %s, %s, %s, %s, %s)",
				"uid", "first_name", "last_name", "email", "created", "updated", "pass_hash", "pass_salt");
			String statementString = "INSERT INTO " + USERS_TABLE_NAME + columnsCommaSeparated
					+ " VALUES (default, ?, ?, ?, now(), now(), ?, ?)" + " RETURNING uid";

            Connection conn = this.connectionProvider.getConnection();
            PreparedStatement pStatement = conn.prepareStatement(statementString);

            //setting attributes of prepared statement 1=fn, 2=ln, 3=email, 4=uid
            pStatement.setString(1, user.first_name);
            pStatement.setString(2, user.last_name);
            pStatement.setString(3, user.email);
            pStatement.setString(4, user.getPassHash());
            pStatement.setString(5, user.getPassSalt());

            ResultSet resultSet = pStatement.executeQuery();

            //check if we're returning a row, this should contain the UUID.
            while (resultSet.next()) {
                UUID userId = (UUID) resultSet.getObject(1);

                if (userId != null)
					LOGGER.info("Why God Why!?");
                    return userId;
            }
        } catch (SQLException sqlException) {
            LOGGER.debug("SQLException update on user");
            sqlException.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public boolean update(User user) throws Exception {

        //guard clauses:
        //no user, if no UID return false.
        if (user == null || user.uid == null)
            return false;

        //if no updatable attributes to set, return false
        if (!(user.last_name != null
                    || user.first_name != null
                    || user.email != null
                    || user.hasPassword())
           )
            return false;

        Optional<User> userInDb = this.getById(user.uid);
        if (userInDb.isEmpty())
            return false;

        User updateUserObj = userInDb.get();

        user.uid = updateUserObj.uid;
		if (user.first_name == null)
			user.first_name = updateUserObj.first_name;
		if (user.last_name == null)
			user.last_name = updateUserObj.last_name;
		if (user.email == null)
			user.email = updateUserObj.email;
        if (user.hasPassword()) {
            user.calculatePassHashWithNewSalt();
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

            pStatement.setString(1, user.first_name);
            pStatement.setString(2, user.last_name);
            pStatement.setString(3, user.email);
            pStatement.setObject(4, user.uid);

            int effectedLines = pStatement.executeUpdate();

            //are we also updating the password?
            if (user.hasPassword()) {
				statementString = "UPDATE " + USERS_TABLE_NAME
						+ " SET (pass_hash, pass_salt, updated) = (?, ?, now()) WHERE uid = ?";
                pStatement = conn.prepareStatement(statementString);
                pStatement.setString(1, user.getPassHash());
                pStatement.setString(2, user.getPassSalt());
                pStatement.setObject(3, user.uid);
            }

            //autocommit is enabled, can turn it off if I want.
            conn.commit();
            conn.setAutoCommit(true);

            return effectedLines >= 0;
        } catch (SQLException sqlException) {
            LOGGER.debug("SQLException update on user");
            sqlException.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean delete(User user) throws Exception {
        return deleteById(user.uid);
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

            return updatedCount > 0;

        } catch (SQLException sqlException) {
            LOGGER.debug("SQL Exception, meaning bad query.");
            sqlException.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
