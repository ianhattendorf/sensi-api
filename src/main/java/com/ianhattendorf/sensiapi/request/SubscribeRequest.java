package com.ianhattendorf.sensiapi.request;

import java.util.ArrayList;
import java.util.List;

public final class SubscribeRequest {
    private final String h;
    private final String m;
    private final List<String> a = new ArrayList<>();
    private final int i;

    public SubscribeRequest(String h, String m, String a, int i) {
        this.h = h;
        this.m = m;
        this.a.add(a);
        this.i = i;
    }

    public SubscribeRequest(String a, int i) {
        this("thermostat-v1", "Subscribe", a, i);
    }

    public String getH() {
        return h;
    }

    public String getM() {
        return m;
    }

    public List<String> getA() {
        return a;
    }

    public int getI() {
        return i;
    }
}
