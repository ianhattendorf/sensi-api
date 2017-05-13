package com.ianhattendorf.sensi.sensiapi.response.data;

public final class ScheduleTemps {
    private Temperature autoHeat;
    private Temperature autoCool;
    private Temperature heat;
    private Temperature cool;

    public ScheduleTemps() {}

    public ScheduleTemps(Temperature autoHeat, Temperature autoCool, Temperature heat, Temperature cool) {
        this.autoHeat = autoHeat;
        this.autoCool = autoCool;
        this.heat = heat;
        this.cool = cool;
    }

    public Temperature getAutoHeat() {
        return autoHeat;
    }

    public Temperature getAutoCool() {
        return autoCool;
    }

    public Temperature getHeat() {
        return heat;
    }

    public Temperature getCool() {
        return cool;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScheduleTemps{");
        sb.append("autoHeat=").append(autoHeat);
        sb.append(", autoCool=").append(autoCool);
        sb.append(", heat=").append(heat);
        sb.append(", cool=").append(cool);
        sb.append('}');
        return sb.toString();
    }
}
