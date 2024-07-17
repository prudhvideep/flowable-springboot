package com.flowable.services;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class WhatsAppSendTask implements JavaDelegate{
   @Override
    public void execute(DelegateExecution execution) {
        System.out.println("<<---- Sending WhatsApp message ---->>");
    }
}
