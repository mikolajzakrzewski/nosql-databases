package edu.nbd.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import edu.nbd.exceptions.ClientException;

@Entity(defaultKeyspace = "nbd")
@CqlName("clients")
public class Client {

    @PartitionKey
    @CqlName("personal_id")
    private String personalID;

    @CqlName("first_name")
    private String firstName;

    @CqlName("last_name")
    private String lastName;

    @CqlName("client_type")
    private ClientType clientType;

    @CqlName("archived")
    private boolean archived;

    public Client() {
    }

    public Client(String personalID, String firstName, String lastName, ClientType clientType) {
        this.personalID = personalID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.clientType = clientType;
        this.archived = false;
    }

    public String getPersonalID() {
        return personalID;
    }

    public void setPersonalID(String personalID) {
        if (personalID != null && !personalID.isEmpty()) {
            this.personalID = personalID;
        } else {
            throw new ClientException("Given personal ID shouldn't be empty.");
        }
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

    public int getMaxVehicles() {
        return clientType.getMaxVehicles();
    }

    public String getClientInfo() {
        String className = "Client";
        return className + firstName + lastName + personalID + clientType.getTypeInfo();
    }

    public String getInfo() {
        return getClientInfo();
    }

    public String getTypeInfo() {
        return clientType.getClass().getSimpleName();
    }

    public String getId() {
        return getPersonalID();
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }
}