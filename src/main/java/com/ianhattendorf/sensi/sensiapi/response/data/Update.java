package com.ianhattendorf.sensi.sensiapi.response.data;

public final class Update {
    private EnvironmentControls environmentControls;
    private OperationalStatus operationalStatus;

    public Update() {}

    public EnvironmentControls getEnvironmentControls() {
        return environmentControls;
    }

    public void setEnvironmentControls(EnvironmentControls environmentControls) {
        this.environmentControls = environmentControls;
    }

    public OperationalStatus getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(OperationalStatus operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Update{");
        sb.append("environmentControls=").append(environmentControls);
        sb.append(", operationalStatus=").append(operationalStatus);
        sb.append('}');
        return sb.toString();
    }

    public static Update merge(Update o1, Update o2) {
        if (o1 == null) {
            return o2;
        }
        if (o2 == null) {
            return o1;
        }
        o1.setEnvironmentControls(EnvironmentControls.merge(o1.getEnvironmentControls(), o2.getEnvironmentControls()));
        o1.setOperationalStatus(OperationalStatus.merge(o1.getOperationalStatus(), o2.getOperationalStatus()));
        return o1;
    }
}
