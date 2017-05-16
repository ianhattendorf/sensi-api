package com.ianhattendorf.sensi.sensiapi.response.data;

public final class Weather {
    private String condition;
    private Integer conditionId;
    private Temperature currentTemp;
    private Temperature highTemp;
    private Temperature lowTemp;
    private Location location;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getConditionId() {
        return conditionId;
    }

    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
    }

    public Temperature getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(Temperature currentTemp) {
        this.currentTemp = currentTemp;
    }

    public Temperature getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(Temperature highTemp) {
        this.highTemp = highTemp;
    }

    public Temperature getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(Temperature lowTemp) {
        this.lowTemp = lowTemp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Weather{");
        sb.append("condition='").append(condition).append('\'');
        sb.append(", conditionId=").append(conditionId);
        sb.append(", currentTemp=").append(currentTemp);
        sb.append(", highTemp=").append(highTemp);
        sb.append(", lowTemp=").append(lowTemp);
        sb.append(", location=").append(location);
        sb.append('}');
        return sb.toString();
    }
}
