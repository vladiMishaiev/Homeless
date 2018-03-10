package com.homeless.Logic;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by vladi on 2/25/2018.
 */

@IgnoreExtraProperties
public class Review {

    private String id;
    private String userIdl;
    private String country;
    private String neighborhood;
    private int numOfRooms;
    private int floor;
    private double size;
    private double price;

    //apartment props
    private boolean AC;
    private boolean bars;
    private boolean parking;
    private boolean elevators;
    private boolean accessFoDisabled;
    private boolean safetyRoom;
    private boolean terrace;
    private boolean sunTerrace;
    private boolean storage;
    private boolean renovated;
    private boolean shared;
    private boolean petsAllowed;
    private boolean isLongTermLease;
    private boolean isUnit;
    private boolean furnished;
    private boolean boiler;

    private String review;
    //stars 0.5 resolution
    private double score;

    public Review(String id, String userIdl) {
        this.id = id;
        this.userIdl = userIdl;
        initPrps();
    }
    public Review() {
        initPrps();
    }


    private void initPrps(){
        AC = false;
        bars = false;
        parking = false;
        elevators = false;
        accessFoDisabled = false;
        safetyRoom = false;
        terrace = false;
        sunTerrace = false;
        storage = false;
        renovated = false;
        shared = false;
        petsAllowed = false;
        isLongTermLease = false;
        isUnit = false;
        furnished = false;
        boiler = false;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserIdl() {
        return userIdl;
    }

    public void setUserIdl(String userIdl) {
        this.userIdl = userIdl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public int getNumOfRooms() {
        return numOfRooms;
    }

    public void setNumOfRooms(int numOfRooms) {
        this.numOfRooms = numOfRooms;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAC() {
        return AC;
    }

    public void setAC(boolean AC) {
        this.AC = AC;
    }

    public boolean isBars() {
        return bars;
    }

    public void setBars(boolean bars) {
        this.bars = bars;
    }

    public boolean isParking() {
        return parking;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }

    public boolean isElevators() {
        return elevators;
    }

    public void setElevators(boolean elevators) {
        this.elevators = elevators;
    }

    public boolean isAccessFoDisabled() {
        return accessFoDisabled;
    }

    public void setAccessFoDisabled(boolean accessFoDisabled) {
        this.accessFoDisabled = accessFoDisabled;
    }

    public boolean isSafetyRoom() {
        return safetyRoom;
    }

    public void setSafetyRoom(boolean safetyRoom) {
        this.safetyRoom = safetyRoom;
    }

    public boolean isTerrace() {
        return terrace;
    }

    public void setTerrace(boolean terrace) {
        this.terrace = terrace;
    }

    public boolean isSunTerrace() {
        return sunTerrace;
    }

    public void setSunTerrace(boolean sunTerrace) {
        this.sunTerrace = sunTerrace;
    }

    public boolean isStorage() {
        return storage;
    }

    public void setStorage(boolean storage) {
        this.storage = storage;
    }

    public boolean isRenovated() {
        return renovated;
    }

    public void setRenovated(boolean renovated) {
        this.renovated = renovated;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public boolean isLongTermLease() {
        return isLongTermLease;
    }

    public void setLongTermLease(boolean longTermLease) {
        isLongTermLease = longTermLease;
    }

    public boolean isUnit() {
        return isUnit;
    }

    public void setUnit(boolean unit) {
        isUnit = unit;
    }

    public boolean isFurnished() {
        return furnished;
    }

    public void setFurnished(boolean furnished) {
        this.furnished = furnished;
    }

    public boolean isBoiler() {
        return boiler;
    }

    public void setBoiler(boolean boiler) {
        this.boiler = boiler;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
