package edu.nbd.model;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(key = "_type", value = "default")
public class Default extends ClientType {

    public Default() {
        super(1, 0);
    }
}