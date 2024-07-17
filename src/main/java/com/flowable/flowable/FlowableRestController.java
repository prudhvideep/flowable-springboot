package com.flowable.flowable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.task.api.Task;
import org.json.JSONObject;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.flowable.classes.DeploymentData;
import com.flowable.classes.TaskData;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FlowableRestController {
  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private RepositoryService repositoryService;

  @PostMapping(value = "/deployProcess", consumes = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<DeploymentData> deployProcess(@RequestBody String xmlContent) {
    try {
      // Deploy the process
      Deployment deployment = repositoryService.createDeployment()
          .addString("process.bpmn20.xml", xmlContent)
          .deploy();

      // Get the deployed process definition
      ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
          .deploymentId(deployment.getId())
          .singleResult();

      // Create response
      DeploymentData response = new DeploymentData();
      response.setDeploymentId(deployment.getId());
      response.setProcessDefinitionId(processDefinition.getId());
      response.setProcessDefinitionKey(processDefinition.getKey());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new DeploymentData("Error deploying process: " + e.getMessage()));
    }
  }

  @PostMapping(path = "/startProcess")
  public ResponseEntity<Map<String, String>> startProcess(@RequestBody String request) {
    Map<String, String> responce = new HashMap<>();
    try {
      JSONObject requestJson = new JSONObject(request);

      if (requestJson.get("processKey") == null) {
        responce.put("errorMessage", "Process Key is not valid");
        return new ResponseEntity<>(responce, HttpStatus.BAD_REQUEST);
      }

      String processKey = (String) requestJson.get("processKey");

      ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey);

      

      System.out.println("Process Instance -----> " + processInstance.getId());

      responce.put("processInstanceId", processInstance.getId());
      responce.put("returnMessage", "Process with key : " + processKey + " started successfully!!!");
      return new ResponseEntity<>(responce, HttpStatus.OK);
    } catch (Exception e) {
      responce.put("errorMessage", e.getMessage());
      return new ResponseEntity<>(responce, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/monitorProcess/{processInstanceId}/completedTasks")
  public ResponseEntity<List<TaskData>> getCompletedTasksByInstanceId(
      @PathVariable String processInstanceId) {
    // System.out.println("Process Instance Id ----> " + processInstanceId);

    // Get completed activities from history
    List<HistoricActivityInstance> allHistoricActivities = historyService.createHistoricActivityInstanceQuery()
        .processInstanceId(processInstanceId)
        .finished()
        .orderByHistoricActivityInstanceStartTime().asc()
        .list();

    // System.out.println("All historic task ----> " +
    // allHistoricActivities.size());

    // Filter only the Task nodes
    List<HistoricActivityInstance> filteredActivities = allHistoricActivities.stream()
        .filter(activity -> activity.getActivityId().startsWith("node_"))
        .collect(Collectors.toList());

    // System.out.println("Filtered Historic Activities ----> " +
    // filteredActivities.size());

    // Map the nodes to taskData class
    List<TaskData> taskData = filteredActivities.stream()
        .map(activity -> new TaskData(activity))
        .collect(Collectors.toList());

    return new ResponseEntity<>(taskData, HttpStatus.OK);
  }

  @GetMapping("/monitorProcess/{processInstanceId}/pendingTasks")
  public ResponseEntity<List<TaskData>> getPendingTasksByInstanceId(@PathVariable String processInstanceId) {
    List<Task> pendingTasks = taskService.createTaskQuery()
        .processInstanceId(processInstanceId)
        .list();

    System.out.println("Pending Tasks ----> " + pendingTasks.size());

    List<TaskData> taskData = pendingTasks.stream()
        .map(pendingTask -> new TaskData(pendingTask, processInstanceId))
        .collect(Collectors.toList());

    return new ResponseEntity<>(taskData, HttpStatus.OK);
  }

  @PostMapping("/completeTask/{taskId}")
  public ResponseEntity<String> completeTask(@PathVariable String taskId) {
    // List of task names in the order they need to be completed
    try {
      taskService.complete(taskId);
      return new ResponseEntity<>("Task completed successfully !!!",HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
    }
    
  }

}
