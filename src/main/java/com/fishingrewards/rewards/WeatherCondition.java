package com.fishingrewards.rewards;

public enum WeatherCondition {
    CLEAR(0),
    RAIN(1),
    THUNDER(2);

    private int id;
    WeatherCondition(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
