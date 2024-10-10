package edu.nbd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "default_type")
public class Default extends ClientType {

    public Default() {
        super(1, 0);
    }
}