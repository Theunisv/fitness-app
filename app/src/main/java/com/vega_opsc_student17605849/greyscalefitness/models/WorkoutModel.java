package com.vega_opsc_student17605849.greyscalefitness.models;

import java.io.Serializable;

public class WorkoutModel implements Serializable {

    private String activityName;
    private Integer caloriesBurned;
    private Integer activityDuration;
    private String activityDate;
    private String activityEstimatedEffort;
    private String userName;

    public WorkoutModel() {
    }

    public WorkoutModel(String activityName, Integer caloriesBurned, Integer activityDuration, String activityDate, String activityEstimatedEffort, String userName) {
        this.activityName = activityName;
        this.caloriesBurned = caloriesBurned;
        this.activityDuration = activityDuration;
        this.activityDate = activityDate;
        this.activityEstimatedEffort = activityEstimatedEffort;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Integer getActivityDuration() {
        return activityDuration;
    }

    public void setActivityDuration(Integer activityDuration) {
        this.activityDuration = activityDuration;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityEstimatedEffort() {
        return activityEstimatedEffort;
    }

    public void setActivityEstimatedEffort(String activityEstimatedEffort) {
        this.activityEstimatedEffort = activityEstimatedEffort;
    }

    public void init(){
        this.activityName = "Test1";
        this.caloriesBurned = 200;
        this.activityDuration = 50;
        this.activityDate = "28.02.2020";
        this.activityEstimatedEffort = "Low";
        this.userName = "Blank";
    }
}
