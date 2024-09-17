package com.flowable.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BotCallServiceTask implements JavaDelegate {
   public void execute(DelegateExecution execution) {
      String activityId = execution.getCurrentActivityId();
      String body = (String)execution.getVariable(activityId + "$body");
      String mobileNumber = (String)execution.getVariable(activityId + "$mobileNumber");
      String waitTimeStr = (String)execution.getVariable(activityId + "$waitTime");
      if (mobileNumber != null && !mobileNumber.trim().isEmpty()) {
         String callId = this.sendBotCall(execution, mobileNumber, body, activityId);
         System.out.println("Received call id from botcall SDK : " + callId);
         this.waitTimeExecution(waitTimeStr);
         System.out.println("< ----- Setting callStatus - Answered ----- >");
         execution.setVariable("callStatus", "Answered");
      } else {
         throw new IllegalArgumentException(" Mobile number is null or empty");
      }
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

   public String sendBotCall(DelegateExecution execution, String toNumber, String body, String activityId) {
      System.out.println("< ----- Bot Call ----- >");
      String user = (String)execution.getVariable("name");
      String amount = (String)execution.getVariable("amount");
      String loanNumber = (String)execution.getVariable("loanNumber");
      String paymentLink = (String)execution.getVariable("paymentLink");
      System.out.println("< ----- To Mobile Number : " + toNumber + " ----- >");
      body = this.replaceVariables(body, amount, user, loanNumber, paymentLink);
      System.out.println("< ----- Message body : " + body + "----- >");
      String callId = UUID.randomUUID().toString();
      Map<String, String> payload = new HashMap();
      payload.put("text", body);
      payload.put("loanNumber", loanNumber);
      payload.put("customerName", user);
      payload.put("customerNumber", toNumber);
      System.out.println("Payload ----> " + payload);
      HttpHeaders headers = new HttpHeaders();
      headers.set("Content-Type", "application/json");
      RestTemplate restTemplate = new RestTemplate();
      HttpEntity<Map<String, String>> requestEntity = new HttpEntity(payload, headers);
      String apiUrl = "https://qa.clucloud.com/api/telecmi/intiatebotcall";
      ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class, new Object[0]);
      if (response.getStatusCode().is2xxSuccessful()) {
         System.out.println("SMS sent successfully. Response: " + (String)response.getBody());
      } else {
         System.out.println("Failed to send BotCall.");
      }

      return callId;
   }

   public String replaceVariables(String template, String amount, String user, String loanNumber, String paymentLink) {
      String body = template.replace("{name}", user != null && !user.isEmpty() ? user : "User").replace("{loan_num}", loanNumber != null && !loanNumber.isEmpty() ? loanNumber : "N/A").replace("{amount}", amount != null && !amount.isEmpty() ? amount : "N/A").replace("{link}", paymentLink != null && !paymentLink.isEmpty() ? paymentLink : "No link provided");
      return body;
   }
}
