package edu.nbd.model;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Default")
public class Default extends ClientType {

    public Default() {
        super(1, 0);
    }
}