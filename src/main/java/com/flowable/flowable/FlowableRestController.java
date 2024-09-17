package com.flowable.flowable;

import com.flowable.classes.DeploymentData;
import com.flowable.classes.SmsCallbackRequest;
import com.flowable.classes.TaskData;
import com.flowable.classes.WhatsAppCallbackRequest;
import com.flowable.entity.CampaignEntity;
import com.flowable.services.CampaignService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(
   origins = {"*"},
   allowedHeaders = {"*"}
)
public class FlowableRestController {
   @Autowired
   private CampaignService campaignService;
   @Autowired
   private RuntimeService runtimeService;
   @Autowired
   private TaskService taskService;
   @Autowired
   private HistoryService historyService;
   @Autowired
   private RepositoryService repositoryService;

   @PostMapping(
      value = {"/deployProcess"},
      consumes = {"application/xml"}
   )
   public ResponseEntity<DeploymentData> deployProcess(@RequestBody String xmlContent) {
      try {
         Deployment deployment = this.repositoryService.createDeployment().addString("process.bpmn20.xml", xmlContent).deploy();
         ProcessDefinition processDefinition = (ProcessDefinition)this.repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
         DeploymentData response = new DeploymentData();
         response.setDeploymentId(deployment.getId());
         response.setProcessDefinitionId(processDefinition.getId());
         response.setProcessDefinitionKey(processDefinition.getKey());
         return ResponseEntity.ok(response);
      } catch (Exception var5) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DeploymentData("Error deploying process: " + var5.getMessage()));
      }
   }

   @SuppressWarnings("unchecked")
   @PostMapping(
      path = {"/startProcess"}
   )
   public ResponseEntity<Map<String, String>> startProcess(@RequestBody String request) {
      HashMap responce = new HashMap();

      try {
         Map<String, Object> variables = new HashMap();
         JSONObject requestJson = new JSONObject(request);
         if (requestJson.get("processKey") == null) {
            responce.put("errorMessage", "Process Key is not valid");
            return new ResponseEntity(responce, HttpStatus.BAD_REQUEST);
         } else {
            variables.put("name", requestJson.get("name"));
            variables.put("amount", requestJson.get("amount"));
            variables.put("loanNumber", requestJson.get("loanNumber"));
            variables.put("paymentLink", requestJson.get("paymentLink"));
            System.out.println("Variable ---> " + variables);
            String processKey = (String)requestJson.get("processKey");
            ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(processKey, variables);
            System.out.println("Process Instance -----> " + processInstance.getId());
            responce.put("processInstanceId", processInstance.getId());
            responce.put("returnMessage", "Process with key : " + processKey + " started successfully!!!");
            return new ResponseEntity(responce, HttpStatus.OK);
         }
      } catch (Exception var7) {
         responce.put("errorMessage", var7.getMessage());
         return new ResponseEntity(responce, HttpStatus.BAD_REQUEST);
      }
   }

   @GetMapping({"/monitorProcess/{processInstanceId}/completedTasks"})
   public ResponseEntity<List<TaskData>> getCompletedTasksByInstanceId(@PathVariable String processInstanceId) {
      List<HistoricActivityInstance> allHistoricActivities = ((HistoricActivityInstanceQuery)this.historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).finished().orderByHistoricActivityInstanceStartTime().asc()).list();
      List<HistoricActivityInstance> filteredActivities = (List)allHistoricActivities.stream().filter((activity) -> {
         return activity.getActivityId().startsWith("node_");
      }).collect(Collectors.toList());
      List<TaskData> taskData = (List)filteredActivities.stream().map((activity) -> {
         return new TaskData(activity);
      }).collect(Collectors.toList());
      return new ResponseEntity(taskData, HttpStatus.OK);
   }

   @GetMapping({"/monitorProcess/{processInstanceId}/pendingTasks"})
   public ResponseEntity<List<TaskData>> getPendingTasksByInstanceId(@PathVariable String processInstanceId) {
      List<Task> pendingTasks = ((TaskQuery)this.taskService.createTaskQuery().processInstanceId(processInstanceId)).list();
      System.out.println("Pending Tasks ----> " + pendingTasks.size());
      List<TaskData> taskData = (List)pendingTasks.stream().map((pendingTask) -> {
         return new TaskData(pendingTask, processInstanceId);
      }).collect(Collectors.toList());
      return new ResponseEntity(taskData, HttpStatus.OK);
   }

   @PostMapping({"/completeTask/{taskId}"})
   public ResponseEntity<String> completeTask(@PathVariable String taskId) {
      try {
         this.taskService.complete(taskId);
         return new ResponseEntity("Task completed successfully !!!", HttpStatus.OK);
      } catch (Exception var3) {
         return new ResponseEntity(var3.getMessage(), HttpStatus.OK);
      }
   }

   @PostMapping({"/smsCallback"})
   public void handleSmsCallback(@RequestBody SmsCallbackRequest callbackRequest) {
      String messageSid = callbackRequest.getMessageSid();
      String messageStatus = callbackRequest.getMessageStatus();
      String executionId = callbackRequest.getExecutionId();
      System.out.println("Received callback for MessageSid: " + messageSid);
      System.out.println("Status: " + messageStatus);
      Map<String, Object> variableMap = new HashMap();
      variableMap.put("messageStatus", messageStatus);
      this.runtimeService.trigger(executionId, variableMap);
   }

   @PostMapping({"/whatsAppCallback"})
   public void handleWhatsAppCallback(@RequestBody WhatsAppCallbackRequest callbackRequest) {
      String messageSid = callbackRequest.getMessageSid();
      String messageStatus = callbackRequest.getMessageStatus();
      String executionId = callbackRequest.getExecutionId();
      System.out.println("Received callback for MessageSid: " + messageSid);
      System.out.println("Status: " + messageStatus);
      Map<String, Object> variableMap = new HashMap();
      variableMap.put("messageStatus", messageStatus);
      this.runtimeService.trigger(executionId, variableMap);
   }

   @PostMapping("/campaigns")
   public ResponseEntity<CampaignEntity> createCampaign(@RequestBody CampaignEntity campaignEntity) {
      CampaignEntity savedCampaign = campaignService.saveCampaign(campaignEntity);
      return ResponseEntity.ok(savedCampaign);
   }
   
}
    