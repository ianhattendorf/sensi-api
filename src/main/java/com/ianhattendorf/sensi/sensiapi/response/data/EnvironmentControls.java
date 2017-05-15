package com.ianhattendorf.sensi.sensiapi.response.data;

public final class EnvironmentControls {
    private Temperature coolSetpoint;
    private Temperature heatSetpoint;
    private String scheduleMode;
    private String holdMode;
    private String fanMode;

    public EnvironmentControls() {}

    public EnvironmentControls(Temperature coolSetpoint, Temperature heatSetpoint, String scheduleMode,
                               String holdMode, String fanMode) {
        this.coolSetpoint = coolSetpoint;
        this.heatSetpoint = heatSetpoint;
        this.scheduleMode = scheduleMode;
        this.holdMode = holdMode;
        this.fanMode = fanMode;
    }

    public Temperature getCoolSetpoint() {
        return coolSetpoint;
    }

    public void setCoolSetpoint(Temperature coolSetpoint) {
        this.coolSetpoint = coolSetpoint;
    }

    public Temperature getHeatSetpoint() {
        return heatSetpoint;
    }

    public void setHeatSetpoint(Temperature heatSetpoint) {
        this.heatSetpoint = heatSetpoint;
    }

    public String getScheduleMode() {
        return scheduleMode;
    }

    public void setScheduleMode(String scheduleMode) {
        this.scheduleMode = scheduleMode;
    }

    public String getHoldMode() {
        return holdMode;
    }

    public void setHoldMode(String holdMode) {
        this.holdMode = holdMode;
    }

    public String getFanMode() {
        return fanMode;
    }

    public void setFanMode(String fanMode) {
        this.fanMode = fanMode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EnvironmentControls{");
        sb.append("coolSetpoint=").append(coolSetpoint);
        sb.append(", heatSetpoint=").append(heatSetpoint);
        sb.append(", scheduleMode='").append(scheduleMode).append('\'');
        sb.append(", holdMode='").append(holdMode).append('\'');
        sb.append(", fanMode='").append(fanMode).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static EnvironmentControls merge(EnvironmentControls o1, EnvironmentControls o2) {
        if (o1 == null) {
            return o2;
        }
        if (o2 == null) {
            return o1;
        }
        if (o2.getCoolSetpoint() != null) {
            o1.setCoolSetpoint(o2.getCoolSetpoint());
        }
        if (o2.getHeatSetpoint() != null) {
            o1.setHeatSetpoint(o2.getHeatSetpoint());
        }
        if (o2.getScheduleMode() != null) {
            o1.setScheduleMode(o2.getScheduleMode());
        }
        if (o2.getScheduleMode() != null) {
            o1.setScheduleMode(o2.getScheduleMode());
        }
        if (o2.getFanMode() != null) {
            o1.setFanMode(o2.getFanMode());
        }
        return o1;
    }
}
