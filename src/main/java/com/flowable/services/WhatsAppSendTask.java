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

public class WhatsAppSendTask implements TriggerableActivityBehavior {
   public void execute(DelegateExecution execution) {
      String executionId = execution.getId();
      String activityId = execution.getCurrentActivityId();
      String body = (String)execution.getVariable(activityId + "$body");
      String mobileNumber = (String)execution.getVariable(activityId + "$mobileNumber");
      String waitTimeStr = (String)execution.getVariable(activityId + "$waitTime");
      System.out.println("ExecutionId " + executionId);
      if (mobileNumber != null && !mobileNumber.trim().isEmpty()) {
         String messageId = this.sendWhatsAppMessage(execution, mobileNumber, body, activityId);
         System.out.println("Received from whatsapp api : " + messageId);
         System.out.println("Setting variable " + activityId + "$smsId - " + messageId);
         execution.setVariable(activityId + "$smsId", messageId);
         this.waitTimeExecution(waitTimeStr);
      } else {
         throw new IllegalArgumentException("Mobile number is null or empty");
      }
   }

   public void trigger(DelegateExecution execution, String signalName, Object signalData) {
      System.out.println("Triggered from whatsapp callBack");
      String messageStatus = (String)execution.getVariable("messageStatus");
      System.out.println("Message Status: " + messageStatus);
      execution.setVariable("messageStatus", messageStatus);
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

   public String sendWhatsAppMessage(DelegateExecution execution, String toNumber, String body, String activityId) {
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
      payload.put("text", body);
      payload.put("executionId", execution.getId());
      payload.put("flowableMessageId", smsId);
      payload.put("number", toNumber);
      payload.put("type", "text");
      HttpHeaders headers = new HttpHeaders();
      headers.set("Content-Type", "application/json");
      RestTemplate restTemplate = new RestTemplate();
      HttpEntity<Map<String, String>> requestEntity = new HttpEntity(payload, headers);
      String apiUrl = "https://qa.clucloud.com/api/msg/sendmessage";
      ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class, new Object[0]);
      if (response.getStatusCode().is2xxSuccessful()) {
         System.out.println("Message Sent successfully. Response: " + (String)response.getBody());
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
    