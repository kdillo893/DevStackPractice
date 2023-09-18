package com.kdillo.simple.web;

import com.kdillo.simple.SimpleApp;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.User;
import com.kdillo.simple.web.UserRecord;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserController {

    //temp tester finals
    private static final String TEMPLATE = "Hello %s";
    private final AtomicLong COUNTER = new AtomicLong();

    public UserRecord getUser(UUID uid) {

        //grab the thing, then put things in the record to return...
        UserDBImpl userDB = new UserDBImpl(SimpleApp.pgConProvider);
        Optional<User> optionalUser = userDB.getById(uid);


        String email = "testemail@mail.com";
        String lastName = "Someone";
        return new UserRecord(uid, TEMPLATE, optionalUser.isPresent() ? optionalUser.get().getLastName() : lastName, email);
    }

}
