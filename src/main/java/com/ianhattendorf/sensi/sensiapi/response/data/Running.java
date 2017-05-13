package com.ianhattendorf.sensi.sensiapi.response.data;

public final class Running {
    private final String mode;

    public Running(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Running{");
        sb.append("mode='").append(mode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
