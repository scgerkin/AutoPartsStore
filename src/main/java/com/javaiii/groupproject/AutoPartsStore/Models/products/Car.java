package com.javaiii.groupproject.AutoPartsStore.Models.products;

import java.util.Objects;

/**
 * Car encapsulates information concerning what parts fit a car.
 *
 * The class provides static factory methods for instantiating Car objects.
 */
public class Car {
    private Integer carID;
    private String make;
    private String model;
    private int productionYear;

    /**
     * Private constructor can only be called by using the createNew or createExisting
     * factory methods.
     * @param carID The carID used to differentiate available car configurations.
     * @param make The makers of the car (Chevy, Ford, etc).
     * @param model The model of the car (Sonic, Fiesta, etc).
     * @param productionYear The production year for this car configuration.
     */
    private Car(Integer carID, String make, String model, Integer productionYear) {
        setCarID(carID);
        setMake(make);
        setModel(model);
        setProductionYear(productionYear);
    }

    /**
     * Factory method for creating a Car object that needs to be added to the
     * database.
     *
     * @param make The makers of the car (Chevy, Ford, etc).
     * @param model The model of the car (Sonic, Fiesta, etc).
     * @param productionYear The production year for this car configuration.
     * @return a new Car object to be saved to the database.
     */
    public static Car createNew(String make, String model, Integer productionYear) {
        return new Car(-1, make, model, productionYear);
    }

    /**
     * Factory method for creating a Car object from existing data (typically
     * from the database).
     *
     * @param carID The carID used to differentiate available car configurations.
     * @param make The makers of the car (Chevy, Ford, etc).
     * @param model The model of the car (Sonic, Fiesta, etc).
     * @param productionYear The production year for this car configuration.
     * @return an existing Car object from established data.
     */
    public static Car createExisting(Integer carID, String make, String model,
                                     Integer productionYear) {
        return new Car(carID, make, model, productionYear);
    }

    /**Getter for carID*/
    public Integer getCarID() {
        return carID;
    }

    /**Setter for productionYear, requires non-null*/
    public void setCarID(Integer carID) {
        this.carID = Objects.requireNonNull(carID);
    }

    /**Getter for make*/
    public String getMake() {
        return make;
    }

    /**Setter for productionYear, requires non-null*/
    public void setMake(String make) {
        this.make = Objects.requireNonNull(make);
    }

    /**Getter for model*/
    public String getModel() {
        return model;
    }

    /**Setter for productionYear, requires non-null*/
    public void setModel(String model) {
        this.model = Objects.requireNonNull(model);
    }

    /**Getter for productionYear*/
    public Integer getProductionYear() {
        return productionYear;
    }

    /**Setter for productionYear, requires non-null*/
    public void setProductionYear(Integer productionYear) {
        this.productionYear = Objects.requireNonNull(productionYear);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return getProductionYear().equals(car.getProductionYear()) &&
                   Objects.equals(getCarID(), car.getCarID()) &&
                   Objects.equals(getMake(), car.getMake()) &&
                   Objects.equals(getModel(), car.getModel());
    }
}
