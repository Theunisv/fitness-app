package com.vega_opsc_student17605849.greyscalefitness.models;

import java.io.Serializable;

public class WaterIntakeModel implements Serializable {

    String userName;
    String currentDate;
    int glassesDrank;

    public WaterIntakeModel() {
    }

    public WaterIntakeModel(String userName, String currentDate, int glassesDrank) {
        this.userName = userName;
        this.currentDate = currentDate;
        this.glassesDrank = glassesDrank;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public int getGlassesDrank() {
        return glassesDrank;
    }

    public void setGlassesDrank(int glassesDrank) {
        this.glassesDrank = glassesDrank;
    }
}
