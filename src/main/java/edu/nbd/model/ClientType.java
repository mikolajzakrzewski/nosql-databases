package edu.nbd.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "client_type")
@Table(name = "client_types", uniqueConstraints = @UniqueConstraint(columnNames = {"client_type"}))
public abstract class ClientType {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private long id;

    @Column
    @NotNull
    private int maxVehicles;

    @Column
    @NotNull
    private int discount;

    public ClientType(int maxVehicles, int discount) {
        this.maxVehicles = maxVehicles;
        this.discount = discount;
    }

    public ClientType() {

    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public double applyDiscount(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Given price cannot be lower than zero.");
        }

        if (price <= discount) {
            return 0;
        }

        return price - discount;
    }

    public String getTypeInfo() {
        return String.valueOf(maxVehicles) + discount;
    }
}