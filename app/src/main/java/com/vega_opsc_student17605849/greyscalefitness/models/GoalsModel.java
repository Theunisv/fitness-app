package com.vega_opsc_student17605849.greyscalefitness.models;

import java.io.Serializable;

public class GoalsModel implements Serializable {
    private String userName;
    private Float targetWeight;
    private String fitnessGoal;
    private Integer trainingGoal;
    private Integer waterIntakeGoal;
    private Integer activeTimeGoal;
    private Integer calorieIntakeGoal;

    public GoalsModel() {
    }

    public GoalsModel(String userName, Float targetWeight, String fitnessGoal, Integer trainingGoal, Integer waterIntakeGoal, Integer activeTimeGoal, Integer calorieIntakeGoal) {
        this.userName = userName;
        this.targetWeight = targetWeight;
        this.fitnessGoal = fitnessGoal;
        this.trainingGoal = trainingGoal;
        this.waterIntakeGoal = waterIntakeGoal;
        this.activeTimeGoal = activeTimeGoal;
        this.calorieIntakeGoal = calorieIntakeGoal;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(Float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public Integer getTrainingGoal() {
        return trainingGoal;
    }

    public void setTrainingGoal(Integer trainingGoal) {
        this.trainingGoal = trainingGoal;
    }

    public Integer getWaterIntakeGoal() {
        return waterIntakeGoal;
    }

    public void setWaterIntakeGoal(Integer waterIntakeGoal) {
        this.waterIntakeGoal = waterIntakeGoal;
    }

    public Integer getActiveTimeGoal() {
        return activeTimeGoal;
    }

    public void setActiveTimeGoal(Integer activeTimeGoal) {
        this.activeTimeGoal = activeTimeGoal;
    }

    public Integer getCalorieIntakeGoal() {
        return calorieIntakeGoal;
    }

    public void setCalorieIntakeGoal(Integer calorieIntakeGoal) {
        this.calorieIntakeGoal = calorieIntakeGoal;
    }
}
