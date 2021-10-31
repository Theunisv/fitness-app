package com.vega_opsc_student17605849.greyscalefitness.models;

import java.io.Serializable;

public class CustomGoalModel  implements Serializable {

    private String goalName;
    private String goalAmount;
    private String goalMeasurement;

    public CustomGoalModel() {
    }

    public CustomGoalModel(String goalName, String goalAmount, String goalMeasurement) {
        this.goalName = goalName;
        this.goalAmount = goalAmount;
        this.goalMeasurement = goalMeasurement;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public String getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(String goalAmount) {
        this.goalAmount = goalAmount;
    }

    public String getGoalMeasurement() {
        return goalMeasurement;
    }

    public void setGoalMeasurement(String goalMeasurement) {
        this.goalMeasurement = goalMeasurement;
    }
}
