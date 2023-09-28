package com.kdillo.simple.web;

import com.kdillo.simple.SimpleApp;
import com.kdillo.simple.db.UserDBImpl;
import com.kdillo.simple.entities.User;
import com.kdillo.simple.web.UserRecord;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;


@Path("users/{uid}")
public class UserController {

    //temp tester finals
    private static final String TEMPLATE = "Hello %s";
    private final AtomicLong COUNTER = new AtomicLong();

//    @GET
//    @Produces("text/json")
//    public String getUsers() {
//
//        return null;
//    }

    @GET
    @Produces("text/json")
    public UserRecord getUser(@PathParam("uid") String uid) {

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

    @DELETE
    @Produces("text/json")
    public void deleteUser(@PathParam("uid") String uid) {
        System.out.print("hi delete");
    }

}
