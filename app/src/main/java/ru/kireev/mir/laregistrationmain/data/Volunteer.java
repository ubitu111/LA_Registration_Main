package ru.kireev.mir.laregistrationmain.data;

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

    public Volunteer(int uniqueId, int index, String name, String surname, String callSign, String phoneNumber, String isSent) {
        this.uniqueId = uniqueId;
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.callSign = callSign;
        this.phoneNumber = phoneNumber;
        this.isSent = isSent;
    }

    @Ignore
    public Volunteer(int index, String name, String surname, String callSign, String phoneNumber, String isSent) {
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.callSign = callSign;
        this.phoneNumber = phoneNumber;
        this.isSent = isSent;
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
}
