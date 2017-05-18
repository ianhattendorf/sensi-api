package com.ianhattendorf.sensi.sensiapi.response.data;

/**
 * Thermostat information.
 */
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

    /**
     * Get the device name.
     * @return The thermostat's device name.
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Get the ID of the contractor that installed the thermostat.
     * @return The contractor ID, or 0 if none exists.
     */
    public Integer getContractorId() {
        return contractorId;
    }

    /**
     * Get the address.
     * @return The address
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Get the city.
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * Get the state.
     * @return The state.
     */
    public String getState() {
        return state;
    }

    /**
     * Get the identifier (iCD). The iCD consists of 8 pairs of hexadecimal digits separated by dashes,
     * e.g. 12-34-56-78-90-ab-cd-ef.
     * @return The identifier.
     */
    public String getiCD() {
        return iCD;
    }

    /**
     * Get the timezone as return by the Sensi API, e.g. "US Mountain Standard Time".
     * @return The thermostat's timezone
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Get the zip code.
     * @return The zip code.
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * The 2-digit country identifier.
     * @return The country identifier.
     */
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
