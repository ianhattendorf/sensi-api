package com.ianhattendorf.sensi.sensiapi.response.data;

public final class Thermostat {
    private final String deviceName;
    private final Integer contractorId;
    private final String address1;
    private final String city;
    private final String state;
    private final String iCD;
    private final String timeZone;
    private final String zipCode;
    private final String country;

    public Thermostat(String deviceName, Integer contractorId, String address1, String city, String state,
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Thermostat{");
        sb.append("deviceName='").append(deviceName).append('\'');
        sb.append(", contractorId=").append(contractorId);
        sb.append(", address1='").append(address1).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", iCD='").append(iCD).append('\'');
        sb.append(", timeZone='").append(timeZone).append('\'');
        sb.append(", zipCode='").append(zipCode).append('\'');
        sb.append(", country='").append(country).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
