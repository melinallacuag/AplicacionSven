package com.anggastudio.sample.WebApiSVEN.Models;

public class SettingTask {

    private String taskID;
    private String name;
    private Boolean isTask;

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getTask() {
        return isTask;
    }

    public void setTask(Boolean task) {
        isTask = task;
    }
}
