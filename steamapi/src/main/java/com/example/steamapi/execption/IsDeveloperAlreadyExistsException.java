package com.example.steamapi.execption;

public class IsDeveloperAlreadyExistsException extends RuntimeException {
    public IsDeveloperAlreadyExistsException(String name) {
        super("Developer with name=" + name + " already exists");
    }
}
