package com.example.steamapi.execption;

public class IsGameAlreadyExistsException extends RuntimeException {
    public IsGameAlreadyExistsException(String name) {
        super("Game with name=" + name + " already exists");
    }
}
