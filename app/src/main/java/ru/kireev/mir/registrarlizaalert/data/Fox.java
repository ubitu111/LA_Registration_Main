package ru.kireev.mir.registrarlizaalert.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "foxes")
public class Fox {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String numberOfFox;
    private Volunteer elderOfFox;
    private List<Volunteer> membersOfFox;
    private String navigators;
    private String walkieTalkies;
    private String compasses;
    private String lamps;
    private String others;
    private String task;
    private String leavingTime;
    private String returnTime;

    public Fox(int id, String numberOfFox, Volunteer elderOfFox,
               List<Volunteer> membersOfFox, String navigators,
               String walkieTalkies, String compasses, String lamps,
               String others, String task, String leavingTime, String returnTime) {
        this.id = id;
        this.numberOfFox = numberOfFox;
        this.elderOfFox = elderOfFox;
        this.membersOfFox = membersOfFox;
        this.navigators = navigators;
        this.walkieTalkies = walkieTalkies;
        this.compasses = compasses;
        this.lamps = lamps;
        this.others = others;
        this.task = task;
        this.leavingTime = leavingTime;
        this.returnTime = returnTime;
    }

    @Ignore
    public Fox(String numberOfFox, Volunteer elderOfFox,
               List<Volunteer> membersOfFox, String navigators,
               String walkieTalkies, String compasses, String lamps,
               String others, String task, String leavingTime, String returnTime) {
        this.numberOfFox = numberOfFox;
        this.elderOfFox = elderOfFox;
        this.membersOfFox = membersOfFox;
        this.navigators = navigators;
        this.walkieTalkies = walkieTalkies;
        this.compasses = compasses;
        this.lamps = lamps;
        this.others = others;
        this.task = task;
        this.leavingTime = leavingTime;
        this.returnTime = returnTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumberOfFox() {
        return numberOfFox;
    }

    public void setNumberOfFox(String numberOfFox) {
        this.numberOfFox = numberOfFox;
    }

    public Volunteer getElderOfFox() {
        return elderOfFox;
    }

    public void setElderOfFox(Volunteer elderOfFox) {
        this.elderOfFox = elderOfFox;
    }

    public List<Volunteer> getMembersOfFox() {
        return membersOfFox;
    }

    public void setMembersOfFox(List<Volunteer> membersOfFox) {
        this.membersOfFox = membersOfFox;
    }

    public String getNavigators() {
        return navigators;
    }

    public void setNavigators(String navigators) {
        this.navigators = navigators;
    }

    public String getWalkieTalkies() {
        return walkieTalkies;
    }

    public void setWalkieTalkies(String walkieTalkies) {
        this.walkieTalkies = walkieTalkies;
    }

    public String getCompasses() {
        return compasses;
    }

    public void setCompasses(String compasses) {
        this.compasses = compasses;
    }

    public String getLamps() {
        return lamps;
    }

    public void setLamps(String lamps) {
        this.lamps = lamps;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getLeavingTime() {
        return leavingTime;
    }

    public void setLeavingTime(String leavingTime) {
        this.leavingTime = leavingTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }
}
