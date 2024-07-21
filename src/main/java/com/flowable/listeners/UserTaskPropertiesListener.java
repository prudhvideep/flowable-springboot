package com.flowable.listeners;

import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;

import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.Process;

public class UserTaskPropertiesListener implements ExecutionListener {
  @Override
  public void notify(DelegateExecution execution) {
    String processDefinitionId = execution.getProcessDefinitionId();
    String activityId = execution.getCurrentActivityId();

    Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
    UserTask userTask = (UserTask) process.getFlowElement(activityId);

    if (userTask != null) {
      Map<String, List<ExtensionElement>> extensionElements = userTask.getExtensionElements();
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
