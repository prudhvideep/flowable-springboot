package com.flowable.services;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;


public class BotCallServiceTask implements JavaDelegate {
  @Override
  public void execute(DelegateExecution execution) {
    String activityId = execution.getCurrentActivityId();
    String mobileNumber = (String) execution.getVariable(activityId + "$" + "mobileNumber");
    String waitTimeStr = (String) execution.getVariable(activityId + "$" + "waitTime");

    System.out.println("<<---- Bot Call ---->>");
    System.out.println("< ----- To Mobile Number : " + mobileNumber + " ----- >");
    
    waitTimeExecution(waitTimeStr);
    
    System.out.println("< ----- Setting callStatus - Answered ----- >");
    execution.setVariable(activityId + "$" + "callStatus", "Answered");
  }

  public void waitTimeExecution(String waitTimeStr){
    int waitTime = 0;
    if (waitTimeStr != null && !waitTimeStr.isEmpty()) {
      try {
        waitTime = Integer.parseInt(waitTimeStr);
      } catch (NumberFormatException e) {
        System.out.println("Invalid waitTime format. Using default value of 0 seconds.");
      }
    }

    System.out.println("Waiting for " + waitTime + " seconds");

    try {
      Thread.sleep(waitTime * 1000L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.println("Thread was interrupted during sleep.");
    }

    System.out.println("Wait is over.");
  }


}