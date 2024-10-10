package edu.nbd.model;

import edu.nbd.exceptions.ClientException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Client {

    @Id
    private String personalID;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @ManyToOne(fetch = jakarta.persistence.FetchType.EAGER, cascade = {jakarta.persistence.CascadeType.PERSIST, jakarta.persistence.CascadeType.MERGE})
    private ClientType clientType;

    private boolean isArchived = false;

    public Client(String firstName, String lastName, String personalID, ClientType clientType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalID = personalID;
        this.clientType = clientType;
    }

    public Client() {

    }

    public String getClientInfo() {
        String className = "Client";
        return className + firstName + lastName + personalID + clientType.getTypeInfo();
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

    public String getPersonalID() {
        return personalID;
    }

    public void setClientType(ClientType clientType) {
        if (clientType != null) {
            this.clientType = clientType;
        } else {
            throw new ClientException("Given client type pointer shouldn't be null.");
        }
    }

    public int getMaxVehicles() {
        return clientType.getMaxVehicles();
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }

    public String getInfo() {
        return getClientInfo();
    }

    public String getId() {
        return getPersonalID();
    }
}