package com.example.easywateringsystem.models;

import java.util.List;

// NOTE: Class name "System" was changed to "Systems"
// so that it doesn't conflict with the java.lang.System
// library when instantiating Systems objects.
public class Systems {
    private static int systemId;
    private String address;
    private List<Zone> zone;


    public Systems(String address) {
        this.address = address;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Zone> getZone() {
        return zone;
    }

    public void setZone(List<Zone> zone) {
        this.zone = zone;
    }


}
