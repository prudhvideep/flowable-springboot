package com.flowable.services;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceTask implements JavaDelegate {
   public void execute(DelegateExecution execution) {
      String activityId = execution.getCurrentActivityId();
      String user = (String)execution.getVariable("name");
      String amount = (String)execution.getVariable("amount");
      String loanNumber = (String)execution.getVariable("loanNumber");
      String paymentLink = (String)execution.getVariable("paymentLink");
      String body = (String)execution.getVariable(activityId + "$body");
      String emailId = (String)execution.getVariable(activityId + "$email");
      String waitTimeStr = (String)execution.getVariable(activityId + "$waitTime");
      System.out.println("<<---- Sending Email ---->>");
      System.out.println("< ----- To email : " + emailId + " ----- >");
      System.out.println("< ----- Message body : " + body + "----- >");
      if (emailId != null && !emailId.trim().isEmpty()) {
         String subject = "Important Notice About Your Loan";
         this.sendEmail(execution, emailId, subject, body, user, amount, loanNumber, paymentLink);
         this.waitTimeExecution(waitTimeStr);
      } else {
         throw new IllegalArgumentException("Email id is null or empty");
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

   public String replaceVariables(String template, String amount, String user, String loanNumber, String paymentLink) {
      String body = template.replace("{name}", user != null && !user.isEmpty() ? user : "User").replace("{loan_num}", loanNumber != null && !loanNumber.isEmpty() ? loanNumber : "N/A").replace("{amount}", amount != null && !amount.isEmpty() ? amount : "N/A").replace("{link}", paymentLink != null && !paymentLink.isEmpty() ? paymentLink : "No link provided");
      return body;
   }

   public void sendEmail(DelegateExecution execution, String to, String subject, String body, String user, String amount, String loanNumber, String paymentLink) {
      try {
         body = this.replaceVariables(body, amount, user, loanNumber, paymentLink);
         EmailService emailService = new EmailService();
         emailService.sendEmail(to, body, subject);
         execution.setVariable("emailStatus", "Sent");
         System.out.println("< ----- Email - Sent Successfully ----- >");
      } catch (Exception var10) {
         execution.setVariable("emailStatus", "Sent");
         System.out.println("Failed to send email to " + to + ": " + var10.getMessage());
      }

   }
}
    