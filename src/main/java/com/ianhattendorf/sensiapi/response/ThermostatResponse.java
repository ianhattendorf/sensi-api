package com.ianhattendorf.sensiapi.response;

public final class ThermostatResponse {
    private final String deviceName;
    private final Integer contractorId;
    private final String address1;
    private final String city;
    private final String state;
    private final String iCD;
    private final String timeZone;
    private final String zipCode;
    private final String country;

    public ThermostatResponse(String deviceName, Integer contractorId, String address1, String city, String state,
                              String iCD, String timeZone, String zipCode, String country) {
        this.deviceName = deviceName;
        this.contractorId = contractorId;
        this.address1 = address1;
        this.city = city;
        this.state = state;
        this.iCD = iCD;
        this.timeZone = timeZone;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Integer getContractorId() {
        return contractorId;
    }

    public String getAddress1() {
        return address1;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getiCD() {
        return iCD;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }
}
