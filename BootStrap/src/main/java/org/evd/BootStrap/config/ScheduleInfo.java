package org.evd.BootStrap.config;

import java.util.ArrayList;
import java.util.List;

public class ScheduleInfo {
    private String name;
    private int num;
    private List<ServiceInfo> services = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<ServiceInfo> getServices() {
        return services;
    }

    public void setServices(List<ServiceInfo> services) {
        this.services = services;
    }
}