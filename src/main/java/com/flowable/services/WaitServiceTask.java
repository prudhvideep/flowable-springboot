package com.flowable.services;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class WaitServiceTask implements JavaDelegate {
   public void execute(DelegateExecution execution) {
      String activityId = execution.getCurrentActivityId();
      String waitTimeStr = (String)execution.getVariable(activityId + "$waitTime");
      this.waitTimeExecution(waitTimeStr);
      System.out.println("<---- Setting paymentStatus - Received ---->");
      execution.setVariable("paymentStatus", "Received");
   }

   public void waitTimeExecution(String waitTimeStr) {
      int waitTime = 0;
      if (waitTimeStr != null && !waitTimeStr.isEmpty()) {
         try {
            waitTime = Integer.parseInt(waitTimeStr);
         } catch (NumberFormatException var5) {
            System.out.println("Invalid waitTime format. Using default value of 0 seconds.");
         }
      }

      System.out.println("Waiting for " + waitTime + " seconds");

      try {
         Thread.sleep((long)waitTime * 1000L);
      } catch (InterruptedException var4) {
         Thread.currentThread().interrupt();
         System.out.println("Thread was interrupted during sleep.");
      }

      System.out.println("Wait is over.");
   }
}
    