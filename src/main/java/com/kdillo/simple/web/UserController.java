package com.kdillo.simple.web;

import com.kdillo.simple.SimpleApp;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.User;
import com.kdillo.simple.web.UserRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;


//@RestController
public class UserController {

    //temp tester finals
    private static final String TEMPLATE = "Hello %s";
    private final AtomicLong COUNTER = new AtomicLong();

//    @GetMapping("/users")
    public List<UserRecord> getUsers() {

        return null;
    }

//    @GetMapping("/user/{uid}")
    public UserRecord getUser(/*@PathVariable*/ String uid) {

        UUID theUid = UUID.fromString(uid);

        //grab the thing, then put things in the record to return...
        UserDBImpl userDB = new UserDBImpl(SimpleApp.pgConProvider);
        Optional<User> optionalUser = userDB.getById(theUid);


        String email = "testemail@mail.com";
        String lastName = "Someone";
        return new UserRecord(theUid, TEMPLATE, optionalUser.isPresent() ? optionalUser.get().getLastName() : lastName, email);
    }

//    @PostMapping("/user")
    public String createUser(/*@RequestBody*/ UserRecord userRecord) {
        //the user data would be passed...

        return "something";
    }

//    @PutMapping("/user/{uid}")
    public UserRecord updateUser(/*@RequestBody*/ UserRecord userRecord, /*@PathVariable*/ String uid) {

        return null;
    }

//    @DeleteMapping("/user/{uid}")
    public void deleteUser(/*@PathVariable*/ String uid) {

    }

}
