package com.ianhattendorf.sensiapi.response.data;

public final class Temperature {
    public final Integer f;
    public final Integer c;

    public Temperature(Integer f, Integer c) {
        this.f = f;
        this.c = c;
    }

    public Integer getF() {
        return f;
    }

    public Integer getC() {
        return c;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Temperature{");
        sb.append("f=").append(f);
        sb.append(", c=").append(c);
        sb.append('}');
        return sb.toString();
    }
}
