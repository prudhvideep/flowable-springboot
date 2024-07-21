package com.flowable.listeners;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.bpmn.model.ExtensionElement;

import java.util.List;
import java.util.Map;

public class ServiceTaskPropertiesListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) {
        String processDefinitionId = execution.getProcessDefinitionId();
        String activityId = execution.getCurrentActivityId();

        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        ServiceTask serviceTask = (ServiceTask) process.getFlowElement(activityId);

        if (serviceTask != null) {
            Map<String, List<ExtensionElement>> extensionElements = serviceTask.getExtensionElements();
            if (extensionElements != null && extensionElements.containsKey("formProperty")) {
                List<ExtensionElement> formProperties = extensionElements.get("formProperty");
                for (ExtensionElement formProperty : formProperties) {
                    String propertyId = formProperty.getAttributeValue(null, "id");
                    String propertyValue = formProperty.getAttributeValue(null, "name");
                    if (propertyId != null && propertyValue != null) {
                        execution.setVariable(propertyId, propertyValue);
                    }
                }
            }
        }
    }
}