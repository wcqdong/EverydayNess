package org.evd.BootStrap.config;

import java.util.List;

public class NodeInfo {
    private String name;
    private String addr;
    private List<ScheduleInfo> schedule;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public List<ScheduleInfo> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<ScheduleInfo> schedule) {
        this.schedule = schedule;
    }
}
