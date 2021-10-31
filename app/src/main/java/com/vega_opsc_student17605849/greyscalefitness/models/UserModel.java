package com.vega_opsc_student17605849.greyscalefitness.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserModel implements Serializable {
    private String firstName;
    private String lastName;
    private String dob;
    private String displayName;
    private String imageUrl;
    private String emailAddress;
    private Double height;
    private Double weight;
    private String fitnessLevel;
    private String exerciseFrequency;
    private List<String> fitnessGoals;
    private String dailyActivity;
    private String userName;
    private Double targetWeight;
    private Integer targetCalories;
    private String gender;
    private String heightMetricPref;
    private String weightMeticPref;

    public String getHeightMetricPref() {
        return heightMetricPref;
    }

    public void setHeightMetricPref(String heightMetricPref) {
        this.heightMetricPref = heightMetricPref;
    }

    public String getWeightMeticPref() {
        return weightMeticPref;
    }

    public void setWeightMeticPref(String weightMeticPref) {
        this.weightMeticPref = weightMeticPref;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public UserModel(){}

    public UserModel(String firstName, String lastName, String dob, String emailAddress, Double height, Double weight, String fitnessLevel, String exerciseFrequency, List<String> fitnessGoals, String dailyActivity, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.emailAddress = emailAddress;
        this.height = height;
        this.weight = weight;
        this.fitnessLevel = fitnessLevel;
        this.exerciseFrequency = exerciseFrequency;
        this.fitnessGoals = fitnessGoals;
        this.dailyActivity = dailyActivity;
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Integer getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(Integer targetCalories) {
        this.targetCalories = targetCalories;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void generateUsername(){
        String[] emailBroken = this.emailAddress.split("@");
        displayName = emailBroken[0];
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(String fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public String getExerciseFrequency() {
        return exerciseFrequency;
    }

    public void setExerciseFrequency(String exerciseFrequency) {
        this.exerciseFrequency = exerciseFrequency;
    }

    public List<String> getFitnessGoals() {
        return fitnessGoals;
    }

    public void setFitnessGoals(List<String> fitnessGoals) {
        this.fitnessGoals = fitnessGoals;
    }

    public String getDailyActivity() {
        return dailyActivity;
    }

    public void setDailyActivity(String dailyActivity) {
        this.dailyActivity = dailyActivity;
    }
}
