package com.kdillo.simple.db;

import com.kdillo.simple.datamodel.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public class UserDAO{

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

        return null;
    }

    public Optional<User> getById(UUID uuid) {

        try {
            Connection conn = this.connectionProvider.getConnection();
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
        return false;
    }

    public boolean delete(User obj) throws Exception {
        return false;
    }

    public boolean deleteById(UUID uuid) throws Exception {
        return false;
    }
}
