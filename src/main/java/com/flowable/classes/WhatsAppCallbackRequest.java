package com.flowable.classes;

public class WhatsAppCallbackRequest {
  private String messageSid;
  private String messageStatus;
  private String executionId;
  public String getMessageSid() {
    return messageSid;
  }
  public void setMessageSid(String messageSid) {
    this.messageSid = messageSid;
  }
  public String getMessageStatus() {
    return messageStatus;
  }
  public void setMessageStatus(String messageStatus) {
    this.messageStatus = messageStatus;
  }
  public String getExecutionId() {
    return executionId;
  }
  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }


}
