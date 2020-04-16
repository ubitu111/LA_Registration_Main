package ru.kireev.mir.registrarlizaalert.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "volunteers")
public class Volunteer {

    @PrimaryKey(autoGenerate = true)
    private int uniqueId;
    private int index;
    private String name;
    private String surname;
    private String callSign;
    private String phoneNumber;
    private String isSent;
    private String carMark;
    private String carModel;
    private String carRegistrationNumber;
    private String carColor;
    private String isAddedToFox;

    public Volunteer(int uniqueId, int index, String name, String surname, String callSign, String phoneNumber, String isSent,
                         String carMark, String carModel, String carRegistrationNumber, String carColor, String isAddedToFox) {
        this.uniqueId = uniqueId;
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.callSign = callSign;
        this.phoneNumber = phoneNumber;
        this.isSent = isSent;
        this.carMark = carMark;
        this.carModel = carModel;
        this.carRegistrationNumber = carRegistrationNumber;
        this.carColor = carColor;
        this.isAddedToFox = isAddedToFox;
    }

    @Ignore
    public Volunteer(int index, String name, String surname, String callSign, String phoneNumber, String isSent,
                     String carMark, String carModel, String carRegistrationNumber, String carColor, String isAddedToFox) {
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.callSign = callSign;
        this.phoneNumber = phoneNumber;
        this.isSent = isSent;
        this.carMark = carMark;
        this.carModel = carModel;
        this.carRegistrationNumber = carRegistrationNumber;
        this.carColor = carColor;
        this.isAddedToFox = isAddedToFox;
    }

    public String isSent() {
        return isSent;
    }

    public void setSent(String sent) {
        isSent = sent;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCarMark() {
        return carMark;
    }

    public void setCarMark(String carMark) {
        this.carMark = carMark;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarRegistrationNumber() {
        return carRegistrationNumber;
    }

    public void setCarRegistrationNumber(String carRegistrationNumber) {
        this.carRegistrationNumber = carRegistrationNumber;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getIsAddedToFox() {
        return isAddedToFox;
    }

    public void setIsAddedToFox(String isAddedToFox) {
        this.isAddedToFox = isAddedToFox;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", name, surname, callSign);
    }
}
