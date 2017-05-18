package com.ianhattendorf.sensi.sensiapi.response.data;

/**
 * Weather information.
 */
public final class Weather {
    private String condition;
    private Integer conditionId;
    private Temperature currentTemp;
    private Temperature highTemp;
    private Temperature lowTemp;
    private Location location;

    /**
     * Get the current condition, e.g. "Partly Cloudy".
     * @return The current condition.
     */
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Get the current condition ID as returned by the Sensi API.
     * @return The current condition ID.
     */
    public Integer getConditionId() {
        return conditionId;
    }

    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
    }

    /**
     * Get the current temperature.
     * @return The current temperature.
     */
    public Temperature getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(Temperature currentTemp) {
        this.currentTemp = currentTemp;
    }

    /**
     * Get the high temperature.
     * @return The high temperature.
     */
    public Temperature getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(Temperature highTemp) {
        this.highTemp = highTemp;
    }

    /**
     * Get the low temperature.
     * @return The low temperature.
     */
    public Temperature getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(Temperature lowTemp) {
        this.lowTemp = lowTemp;
    }

    /**
     * Get the weather location.
     * @return The weather location.
     */
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
