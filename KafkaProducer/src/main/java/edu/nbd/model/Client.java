package edu.nbd.model;

import edu.nbd.exceptions.ClientException;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Client {

    @BsonId
    @JsonbProperty("personalID")
    private String personalID;

    @BsonProperty("firstName")
    @JsonbProperty("firstName")
    private String firstName;

    @BsonProperty("lastName")
    @JsonbProperty("lastName")
    private String lastName;

    @BsonProperty("clientType")
    @JsonbProperty("clientType")
    private ClientType clientType;

    @BsonProperty("archived")
    @JsonbProperty("archived")
    private boolean archived;

    @BsonProperty("currentRentsNumber")
    @JsonbProperty("currentRentsNumber")
    private int currentRentsNumber;

    public Client() {
    }

    @BsonCreator
    @JsonbCreator
    public Client(@BsonProperty("personalID") @JsonbProperty("personalID") String personalID,
                  @BsonProperty("firstName") @JsonbProperty("firstName") String firstName,
                  @BsonProperty("lastName") @JsonbProperty("lastName") String lastName,
                  @BsonProperty("clientType") @JsonbProperty("clientType") ClientType clientType) {
        this.personalID = personalID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.clientType = clientType;
        this.archived = false;
        this.currentRentsNumber = 0;
    }

    public String getPersonalID() {
        return personalID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName != null && !firstName.isEmpty()) {
            this.firstName = firstName;
        } else {
            throw new ClientException("Given first name shouldn't be empty.");
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName != null && !lastName.isEmpty()) {
            this.lastName = lastName;
        } else {
            throw new ClientException("Given last name shouldn't be empty.");
        }
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        if (clientType != null) {
            this.clientType = clientType;
        } else {
            throw new ClientException("Given client type object shouldn't be null.");
        }
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public int getCurrentRentsNumber() {
        return currentRentsNumber;
    }

    @BsonIgnore
    @JsonbTransient
    public int getMaxVehicles() {
        return clientType.getMaxVehicles();
    }

    @BsonIgnore
    @JsonbTransient
    public String getClientInfo() {
        String className = "Client";
        return className + firstName + lastName + personalID + clientType.getTypeInfo();
    }

    @BsonIgnore
    @JsonbTransient
    public String getInfo() {
        return getClientInfo();
    }

    @BsonIgnore
    @JsonbTransient
    public String getTypeInfo() {
        return clientType.getClass().getSimpleName();
    }

    @BsonIgnore
    @JsonbTransient
    public String getId() {
        return getPersonalID();
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }
}