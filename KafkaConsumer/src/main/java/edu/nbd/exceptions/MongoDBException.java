package edu.nbd.exceptions;

import com.mongodb.MongoException;

public class MongoDBException extends MongoException {
    public MongoDBException(String message) {
        super(message);
    }

    public MongoDBException(String message, Throwable cause) {
        super(message, cause);
    }
}
