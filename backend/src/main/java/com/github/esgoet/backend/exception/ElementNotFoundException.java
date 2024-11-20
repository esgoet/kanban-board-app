package com.github.esgoet.backend.exception;

public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String element, String id) {
        super(element + " with ID " + id + " not found");
    }
}
