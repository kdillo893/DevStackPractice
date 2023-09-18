package com.kdillo.simple.rest;

import java.util.UUID;

public record UserRecord(UUID uid, String first_name, String last_name, String email) {

}
