package fr.beed.pulscircula;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

//DailyReport class

public class DailyReport implements Serializable {
    String Date;
    int number_of_steps;
    Float Distance;
    int duration;
    int goals;
    int stand_counter;
    int number_of_correct_steps;
    String injury_report;

    //Constructors
    public DailyReport(String date, int number_of_setps, Float distance, int duration, int goals, int stand_counter, int number_of_correct_steps, String email) {
        this.setDate(date);
        this.setNumber_of_steps(number_of_setps);
        this.setDistance(distance);
        this.setDuration(duration);
        this.setGoals(goals);
        this.setStand_counter(stand_counter);
        this.setNumber_of_correct_steps(number_of_correct_steps);
    }

    public DailyReport(HashMap hashMap, String str) {
        this.setDate(str);
        this.setNumber_of_steps((int) hashMap.get("number_of_steps"));
        this.setDistance((Float) hashMap.get("distance"));
        this.setDuration((int) hashMap.get("duration"));
        this.setGoals((int) hashMap.get("goals"));
        this.setStand_counter((int) hashMap.get("stand_counter"));
        this.setNumber_of_correct_steps((int) hashMap.get("number_of_correct_steps"));
        this.setInjury_report((String) hashMap.get("injury_report"));
    }

    public DailyReport(String date) {
        this.setDate(date);
        this.setNumber_of_steps(0);
        this.setDistance(0f);
        this.setDuration(0);
        this.setGoals(0);
        this.setStand_counter(0);
        this.setNumber_of_correct_steps(0);
        this.setInjury_report("0");
    }

    public DailyReport() {
        this.setDate("");
        this.setNumber_of_steps(0);
        this.setDistance(0f);
        this.setDuration(0);
        this.setGoals(0);
        this.setStand_counter(0);
        this.setNumber_of_correct_steps(0);
        this.setInjury_report("0");
    }

    //Getters & setters

    public String getInjury_report() {
        return injury_report;
    }

    public void setInjury_report(String injury_report) { this.injury_report = injury_report; }
    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getNumber_of_steps() {
        return number_of_steps;
    }

    public void setNumber_of_steps(int number_of_setps) {
        this.number_of_steps = number_of_setps;
    }

    public Float getDistance() {
        return Distance;
    }

    public void setDistance(Float distance) {
        Distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getStand_counter() {
        return stand_counter;
    }

    public void setStand_counter(int stand_counter) {
        this.stand_counter = stand_counter;
    }

    public int getNumber_of_correct_steps() {
        return number_of_correct_steps;
    }

    public void setNumber_of_correct_steps(int number_of_correct_steps) {
        this.number_of_correct_steps = number_of_correct_steps;
    }

}
