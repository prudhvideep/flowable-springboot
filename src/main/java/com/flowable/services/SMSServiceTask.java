package com.flowable.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.delegate.TriggerableActivityBehavior;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SMSServiceTask implements TriggerableActivityBehavior {
   public void execute(DelegateExecution execution) {
      String executionId = execution.getId();
      String activityId = execution.getCurrentActivityId();
      String body = (String)execution.getVariable(activityId + "$body");
      String mobileNumber = (String)execution.getVariable(activityId + "$mobileNumber");
      String waitTimeStr = (String)execution.getVariable(activityId + "$waitTime");
      System.out.println("ExecutionId " + executionId);
      if (mobileNumber != null && !mobileNumber.trim().isEmpty()) {
         String smsId = this.sendSms(execution, mobileNumber, body, activityId);
         System.out.println("Received smsId from sms SDK : " + smsId);
         this.waitTimeExecution(waitTimeStr);
      } else {
         throw new IllegalArgumentException("Mobile number is null or empty");
      }
   }

   public void trigger(DelegateExecution execution, String signalName, Object signalData) {
      System.out.println("Inside Trigger function");
      String activityId = execution.getCurrentActivityId();
      String messageStatus = (String)execution.getVariable("messageStatus");
      System.out.println("Message Status: " + messageStatus);
      execution.setVariable("smsStatus", messageStatus);
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

   public String sendSms(DelegateExecution execution, String toNumber, String body, String activityId) {
      System.out.println("< ----- Sending Sms ----- >");
      String user = (String)execution.getVariable("name");
      String amount = (String)execution.getVariable("amount");
      String loanNumber = (String)execution.getVariable("loanNumber");
      String paymentLink = (String)execution.getVariable("paymentLink");
      System.out.println("< ----- To Mobile Number : " + toNumber + " ----- >");
      body = this.replaceVariables(body, amount, user, loanNumber, paymentLink);
      System.out.println("< ----- Message body : " + body + "----- >");
      String smsId = UUID.randomUUID().toString();
      Map<String, String> payload = new HashMap();
      payload.put("content", body);
      payload.put("executionId", execution.getId());
      payload.put("loanNumber", loanNumber);
      payload.put("messageSid", smsId);
      payload.put("name", user);
      payload.put("phoneNumber", toNumber);
      HttpHeaders headers = new HttpHeaders();
      headers.set("api-key", "80516d36-4bc4-4570-8e02-1cdac4ac4e88");
      headers.set("Content-Type", "application/json");
      RestTemplate restTemplate = new RestTemplate();
      HttpEntity<Map<String, String>> requestEntity = new HttpEntity(payload, headers);
      String apiUrl = "https://qa.clucloud.com/api/sms/sendSms";
      ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class, new Object[0]);
      if (response.getStatusCode().is2xxSuccessful()) {
         System.out.println("SMS sent successfully. Response: " + (String)response.getBody());
      } else {
         System.out.println("Failed to send SMS. Status code: " + response.getStatusCodeValue());
      }

      return smsId;
   }

   public String replaceVariables(String template, String amount, String user, String loanNumber, String paymentLink) {
      String body = template.replace("{name}", user != null && !user.isEmpty() ? user : "User").replace("{loan_num}", loanNumber != null && !loanNumber.isEmpty() ? loanNumber : "N/A").replace("{amount}", amount != null && !amount.isEmpty() ? amount : "N/A").replace("{link}", paymentLink != null && !paymentLink.isEmpty() ? paymentLink : "No link provided");
      return body;
   }
}
    