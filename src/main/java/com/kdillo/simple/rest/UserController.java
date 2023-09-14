package com.kdillo.simple.rest;

import com.kdillo.simple.SimpleApp;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UserController {

    //temp tester finals
    private static final String TEMPLATE = "Hello %s";
    private final AtomicLong COUNTER = new AtomicLong();

    @GetMapping("/user")
    public UserRecord getUser(@RequestParam(value="uid")UUID uid) {

        //grab the thing, then put things in the record to return...
        UserDBImpl userDB = new UserDBImpl(SimpleApp.pgConProvider);
        Optional<User> optionalUser = userDB.getById(uid);


        String email = "testemail@mail.com";
        String lastName = "Someone";
        return new UserRecord(uid, TEMPLATE, optionalUser.isPresent() ? optionalUser.get().getLastName() : lastName, email);
    }

}
