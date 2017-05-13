package com.ianhattendorf.sensi.sensiapi.response.data;

public final class OperationalStatus {
    private Temperature temperature;
    private Integer humidity;
    private Integer batteryVoltage;
    private Running running;
    private Boolean lowPower;
    private String operatingMode;
    private ScheduleTemps scheduleTemps;
    private Integer powerStatus;

    public OperationalStatus() {}

    public OperationalStatus(Temperature temperature, Integer humidity, Integer batteryVoltage, Running running,
                             Boolean lowPower, String operatingMode, ScheduleTemps scheduleTemps, Integer powerStatus) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.batteryVoltage = batteryVoltage;
        this.running = running;
        this.lowPower = lowPower;
        this.operatingMode = operatingMode;
        this.scheduleTemps = scheduleTemps;
        this.powerStatus = powerStatus;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Integer batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public Running getRunning() {
        return running;
    }

    public void setRunning(Running running) {
        this.running = running;
    }

    public Boolean getLowPower() {
        return lowPower;
    }

    public void setLowPower(Boolean lowPower) {
        this.lowPower = lowPower;
    }

    public String getOperatingMode() {
        return operatingMode;
    }

    public void setOperatingMode(String operatingMode) {
        this.operatingMode = operatingMode;
    }

    public ScheduleTemps getScheduleTemps() {
        return scheduleTemps;
    }

    public void setScheduleTemps(ScheduleTemps scheduleTemps) {
        this.scheduleTemps = scheduleTemps;
    }

    public Integer getPowerStatus() {
        return powerStatus;
    }

    public void setPowerStatus(Integer powerStatus) {
        this.powerStatus = powerStatus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OperationalStatus{");
        sb.append("temperature=").append(temperature);
        sb.append(", humidity=").append(humidity);
        sb.append(", batteryVoltage=").append(batteryVoltage);
        sb.append(", running=").append(running);
        sb.append(", lowPower=").append(lowPower);
        sb.append(", operatingMode='").append(operatingMode).append('\'');
        sb.append(", scheduleTemps=").append(scheduleTemps);
        sb.append(", powerStatus=").append(powerStatus);
        sb.append('}');
        return sb.toString();
    }

    public static OperationalStatus merge(OperationalStatus o1, OperationalStatus o2) {
        if (o1 == null) {
            return o2;
        }
        if (o2 == null) {
            return o1;
        }
        if (o2.getTemperature() != null) {
            o1.setTemperature(o2.getTemperature());
        }
        if (o2.getHumidity() != null) {
            o1.setHumidity(o2.getHumidity());
        }
        if (o2.getBatteryVoltage() != null) {
            o1.setBatteryVoltage(o2.getBatteryVoltage());
        }
        if (o2.getRunning() != null) {
            o1.setRunning(o2.getRunning());
        }
        if (o2.getLowPower() != null) {
            o1.setLowPower(o2.getLowPower());
        }
        if (o2.getOperatingMode() != null) {
            o1.setOperatingMode(o2.getOperatingMode());
        }
        if (o2.getScheduleTemps() != null) {
            o1.setScheduleTemps(o2.getScheduleTemps());
        }
        if (o2.getPowerStatus() != null) {
            o1.setPowerStatus(o2.getPowerStatus());
        }
        return o1;
    }
}
