package com.javaiii.groupproject.AutoPartsStore.Models.address;

import java.util.Arrays;
import java.util.Objects;

/**
 * Address class represents an address containing a street, city, state, and
 * zip code.
 *
 * All fields are required to be NON NULL.
 *
 * If street or city fields are too long to save to the database, the values will
 * be truncated to fit.
 *
 * State must be a 2 letter character array.
 *
 * Zip codes are stored as a fully qualified zip code with +4 suffix in the form:
 * 99999-9999.
 *
 * In general, all zip codes should include the +4 suffix, even if it is unknown,
 * however if a 5 digit zip code is provided, this class will automatically append
 * the -0000 indicating the +4 suffix is unknown at instantiation or when set.
 */
public class Address {
    private String street;
    private String city;
    private char state[];
    private char zipCode[];

    /**
     * Default constructor. Because none of these values are allowed to be null
     * when saving to the database, all arguments must be provided and cannot
     * be null at instantiation.
     * @param street The street address as a String, example "123 Main Street".
     * @param city The city of the address as a string, example "Atlanta".
     * @param state The state abbreviation for the address, example "GA". State
     *              abbreviations consist of only 2 characters.
     * @param zipCode The zip code of the address, example "99999-9999".
     *                Zip codes are represented as 9 digits delimited with a
     *                hyphen before the "+4 suffix" portion of a zip code. This
     *                class allows construction and setting of 5 digit zip codes
     *                but will pad a 5 digit zip code with an unknown suffix of
     *                "-0000".
     */
    public Address(String street, String city, String state, String zipCode) {
        setStreet(street);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
    }

    /**
     * Deep copy constructor.
     * @param obj The object we wish to copy.
     */
    public Address(Address obj) {
        this.street = obj.street;
        this.city = obj.city;
        this.state = obj.state;
        this.zipCode = obj.zipCode;
    }

    /**Getter for street*/
    public String getStreet() {
        return street;
    }

    /**Setter for street*/
    public void setStreet(String street) {
        this.street = Objects.requireNonNull(street);
    }

    /**Getter for city*/
    public String getCity() {
        return city;
    }

    /**Setter for city*/
    public void setCity(String city) {
        this.city = Objects.requireNonNull(city);
    }

    /**Getter for state*/
    public char[] getState() {
        return state;
    }

    /**
     * Setter for state member variable.
     * @param state a char array of 2 characters representing a state abbreviation.
     * @throws IllegalArgumentException If state is not exclusively 2 characters.
     */
    public void setState(String state) throws IllegalArgumentException {
        if (state.length() != 2) {
            throw new IllegalArgumentException("States can only be 2 characters");
        }
        this.state = state.toCharArray();
    }

    /**Getter for zipCode*/
    public char[] getZipCode() {
        return zipCode;
    }

    /**
     * Setter for zipCode member variable.
     * @param zipCode a char array of 5 or 10 characters representing a zip code.
     * @throws IllegalArgumentException If zipCode is not 5 or 10 characters.
     */
    public void setZipCode(String zipCode) throws IllegalArgumentException {
        if (zipCode.length() != 10 && zipCode.length() != 5) {
            throw new IllegalArgumentException("Zip code must be only 5 or 10 characters.");
        }

        // add -0000 suffix for 5 digit zip codes.
        if (zipCode.length() == 5) {
            this.zipCode = new char[10];
            String unknownSuffix = "-0000";
            System.arraycopy(zipCode.toCharArray(), 0, this.zipCode, 0, 5);
            System.arraycopy(unknownSuffix.toCharArray(), 0, this.zipCode, 5, 5);
        }
        else {
            this.zipCode = zipCode.toCharArray();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getStreet(), address.getStreet()) &&
                   Objects.equals(getCity(), address.getCity()) &&
                   Arrays.equals(getState(), address.getState()) &&
                   Arrays.equals(getZipCode(), address.getZipCode());
    }
}