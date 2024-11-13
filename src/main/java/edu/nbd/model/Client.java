package edu.nbd.model;

import edu.nbd.exceptions.ClientException;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Client {

    @BsonId
    private String personalID;

    @BsonProperty("firstName")
    private String firstName;

    @BsonProperty("lastName")
    private String lastName;

    @BsonProperty("clientType")
    private ClientType clientType;

    @BsonProperty("archived")
    private boolean archived;

    @BsonProperty("currentRentsNumber")
    private int currentRentsNumber;

    public Client() {
    }

    @BsonCreator
    public Client(@BsonProperty("personalID") String personalID,
                  @BsonProperty("firstName") String firstName,
                  @BsonProperty("lastName") String lastName,
                  @BsonProperty("clientType") ClientType clientType) {
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
    public int getMaxVehicles() {
        return clientType.getMaxVehicles();
    }

    @BsonIgnore
    public String getClientInfo() {
        String className = "Client";
        return className + firstName + lastName + personalID + clientType.getTypeInfo();
    }

    @BsonIgnore
    public String getInfo() {
        return getClientInfo();
    }

    @BsonIgnore
    public String getTypeInfo() {
        return clientType.getClass().getSimpleName();
    }

    @BsonIgnore
    public String getId() {
        return getPersonalID();
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }
}