package com.flowable.services;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class SMSServiceTask implements JavaDelegate {
  @Override
  public void execute(DelegateExecution execution) {
    String mobile = (String) execution.getVariable("mobileNumber");
    if (mobile == null || mobile.equals("default_number")) {
      System.out.println("No valid mobile number provided. SMS cannot be sent.");
      // You might want to log this, throw an exception, or handle it in some other way
    } else {
      System.out.println("<<---- Sending SMS to: " + mobile + " ---->>");
      // Your SMS sending logic here
    }
  }
}