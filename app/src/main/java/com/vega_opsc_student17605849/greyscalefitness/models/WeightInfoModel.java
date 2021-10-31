package com.vega_opsc_student17605849.greyscalefitness.models;

import java.io.Serializable;

public class WeightInfoModel implements Serializable {

     private String userName;
     private String dateCaptured;
     private float weight;
     private float fat;
     private float muscle;
     private float water;
     private float target;

    public WeightInfoModel() {
    }

    public WeightInfoModel(String userName, String dateCaptured, float weight, float fat, float muscle, float water, float target) {
        this.userName = userName;
        this.dateCaptured = dateCaptured;
        this.weight = weight;
        this.fat = fat;
        this.muscle = muscle;
        this.water = water;
        this.target = target;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDateCaptured() {
        return dateCaptured;
    }

    public void setDateCaptured(String dateCaptured) {
        this.dateCaptured = dateCaptured;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getMuscle() {
        return muscle;
    }

    public void setMuscle(float muscle) {
        this.muscle = muscle;
    }

    public float getWater() {
        return water;
    }

    public void setWater(float water) {
        this.water = water;
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public void init(String userName,String date){
        this.userName = userName;
        this.dateCaptured = date;
        this.weight = 0;
        this.fat = 0;
        this.muscle = 0;
        this.water = 0;
        this.target = 0;
    }
}
