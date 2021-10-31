package com.vega_opsc_student17605849.greyscalefitness.models;

import java.io.Serializable;

public class ExperienceModel implements Serializable {
    private String username;
    private Integer currentLevel;
    private Integer experience;

    public ExperienceModel() {
    }

    public ExperienceModel(String username, Integer currentLevel, Integer experience) {
        this.username = username;
        this.currentLevel = currentLevel;
        this.experience = experience;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }
}
