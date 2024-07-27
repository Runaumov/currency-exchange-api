package org.example.currencyexchangeapi.exceptions;

public class ModelAlreadyExistsException extends RuntimeException {

    public ModelAlreadyExistsException(String message) {
        super(message);
    }
}
