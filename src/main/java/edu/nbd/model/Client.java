package edu.nbd.model;

import edu.nbd.exceptions.ClientException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @NotEmpty
    @Size(min = 11, max = 11)
    private String personalID;

    @Column
    @NotEmpty
    private String firstName;

    @Column
    @NotEmpty
    private String lastName;

    @ManyToOne
    @NotNull
    private ClientType clientType;

    @Column
    @NotNull
    private boolean archived = false;

    @Version
    private long version;

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
            throw new ClientException("Given client type object shouldn't be null.");
        }
    }

    public int getMaxVehicles() {
        return clientType.getMaxVehicles();
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getInfo() {
        return getClientInfo();
    }

    public ClientType getClientType() {
        return clientType;
    }

    public String getTypeInfo() {
        return clientType.getClass().getSimpleName();
    }

    public String getId() {
        return getPersonalID();
    }
}