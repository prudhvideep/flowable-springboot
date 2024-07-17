package com.flowable.classes;

import org.flowable.task.api.Task;
import org.flowable.engine.history.HistoricActivityInstance;

public class TaskData {
  private String taskId;
  private String taskKey;
  private String taskName;
  private String taskType;
  private String taskStatus;
  private String assignee;
  private String proceessInstanceId;

  public TaskData(HistoricActivityInstance historicActivityInstance) {
    this.taskId = historicActivityInstance.getId();
    this.taskKey = historicActivityInstance.getActivityId();
    this.taskName = historicActivityInstance.getActivityName();
    this.taskType = historicActivityInstance.getActivityType();
    this.taskStatus = "Completed";
    this.assignee = historicActivityInstance.getAssignee();
    this.proceessInstanceId = historicActivityInstance.getProcessInstanceId();
  }

  public TaskData(Task task, String pid){
    this.taskId = task.getId();
    this.taskKey = task.getTaskDefinitionKey();
    this.taskName = task.getName();
    //this.taskType = task.getScopeType();
    this.taskStatus = "Pending";
    this.assignee = task.getAssignee();
    this.proceessInstanceId = pid;
  }

  public String getTaskKey() {
    return taskKey;
  }

  public void setTaskKey(String taskKey) {
    this.taskKey = taskKey;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public String getTaskType() {
    return taskType;
  }

  public void setTaskType(String taskType) {
    this.taskType = taskType;
  }

  public String getTaskStatus() {
    return taskStatus;
  }

  public void setTaskStatus(String taskStatus) {
    this.taskStatus = taskStatus;
  }

  public String getProceessInstanceId() {
    return proceessInstanceId;
  }

  public void setProceessInstanceId(String proceessInstanceId) {
    this.proceessInstanceId = proceessInstanceId;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }
}
