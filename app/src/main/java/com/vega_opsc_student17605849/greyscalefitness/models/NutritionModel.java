package com.vega_opsc_student17605849.greyscalefitness.models;

import java.io.Serializable;

public class NutritionModel implements Serializable {
    private String strDate;
    private Integer breakfastCalories;
    private Integer lunchCalories;
    private Integer dinnerCalories;
    private Integer snackCalories;
    private String breakfastImagesURI;
    private String lunchImagesURI;
    private String dinnerImagesURI;
    private String snackImagesURI;
    private Integer calorieTarget;

    public NutritionModel() {
    }

    public NutritionModel(String strDate, Integer breakfastCalories, Integer lunchCalories, Integer dinnerCalories, Integer snackCalories, String breakfastImagesURI, String lunchImagesURI, String dinnerImagesURI, String snackImagesURI, Integer calorieTarget) {
        this.strDate = strDate;
        this.breakfastCalories = breakfastCalories;
        this.lunchCalories = lunchCalories;
        this.dinnerCalories = dinnerCalories;
        this.snackCalories = snackCalories;
        this.breakfastImagesURI = breakfastImagesURI;
        this.lunchImagesURI = lunchImagesURI;
        this.dinnerImagesURI = dinnerImagesURI;
        this.snackImagesURI = snackImagesURI;
        this.calorieTarget = calorieTarget;
    }
    public void init(String date){
        strDate = date;
        breakfastCalories = 0;
        lunchCalories = 0;
        this.dinnerCalories = 0;
        this.snackCalories = 0;
        this.breakfastImagesURI = "";
        this.lunchImagesURI = "";
        this.dinnerImagesURI = "";
        this.snackImagesURI = "";
        this.calorieTarget = 2000;
    }
    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public Integer getBreakfastCalories() {
        return breakfastCalories;
    }

    public void setBreakfastCalories(Integer breakfastCalories) {
        this.breakfastCalories = breakfastCalories;
    }

    public Integer getLunchCalories() {
        return lunchCalories;
    }

    public void setLunchCalories(Integer lunchCalories) {
        this.lunchCalories = lunchCalories;
    }

    public Integer getDinnerCalories() {
        return dinnerCalories;
    }

    public void setDinnerCalories(Integer dinnerCalories) {
        this.dinnerCalories = dinnerCalories;
    }

    public Integer getSnackCalories() {
        return snackCalories;
    }

    public void setSnackCalories(Integer snackCalories) {
        this.snackCalories = snackCalories;
    }

    public String getBreakfastImagesURI() {
        return breakfastImagesURI;
    }

    public void setBreakfastImagesURI(String breakfastImagesURI) {
        this.breakfastImagesURI = breakfastImagesURI;
    }

    public String getLunchImagesURI() {
        return lunchImagesURI;
    }

    public void setLunchImagesURI(String lunchImagesURI) {
        this.lunchImagesURI = lunchImagesURI;
    }

    public String getDinnerImagesURI() {
        return dinnerImagesURI;
    }

    public void setDinnerImagesURI(String dinnerImagesURI) {
        this.dinnerImagesURI = dinnerImagesURI;
    }

    public String getSnackImagesURI() {
        return snackImagesURI;
    }

    public void setSnackImagesURI(String snackImagesURI) {
        this.snackImagesURI = snackImagesURI;
    }

    public Integer getCalorieTarget() {
        return calorieTarget;
    }

    public void setCalorieTarget(Integer calorieTarget) {
        this.calorieTarget = calorieTarget;
    }
}
