package com.simple.test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.processEngine;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.complete;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.camunda.bpm.consulting.process_test_coverage.ProcessTestCoverage;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.simple.camunda.ProcessRequestDelegate;

public class CamundaAPITest {
	private static ProcessEngine processEngine;
	private static TaskService taskService;
	private static  RuntimeService runtimeService;
	static {

		processEngine = StandaloneInMemProcessEngineConfiguration
				.createStandaloneInMemProcessEngineConfiguration()
				.buildProcessEngine();
		taskService=processEngine.getTaskService();
		runtimeService = processEngine.getRuntimeService();
	}

	@Rule
	public ProcessEngineRule processEngineRule = new ProcessEngineRule(
			processEngine);

//	@Test
//	@Deployment(resources = "view-prediction.bpmn")
//	public void testInstantiateProcessInstance() {
//
//		RuntimeService runtimeService = processEngineRule.getRuntimeService();
//
//		ProcessInstance processInstance = runtimeService
//				.startProcessInstanceByKey("view-prediction");
//		Assert.assertNotNull(processInstance);
//
//	}
	
	@Test
	  @Deployment(resources = "view-prediction.bpmn")
	  public void testRejectedPath() {
	    Mocks.register("processRequestAdapter", new ProcessRequestDelegate()); // get expression to work without Spring or CDI in JUnit
	    
	    Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("Project_Groups", "MOCK PROJECT");
	    variables.put("Task_Action", "Complete a form");
	    variables.put("Number_Task_Assignees", "1");
	    variables.put("Task_Completed", "Y");
	    
	    ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey("view-prediction", variables);

	    System.out.println("Started process instance " + processInstance);
	    
	    // check userTask
	    // using camunda-bpm-assert
		/*assertThat(processInstance).isStarted()
				.isWaitingAt("user_action_prediction").task()
				.isAssignedTo("john");*/
	    Task waitStateBefore = taskService.createTaskQuery()
	  	      .taskDefinitionKey("user_action_prediction")
	  	      .processInstanceId(processInstance.getId())
	  	      .singleResult();
	  	    assertNotNull(waitStateBefore);
	  	 
	  	  
	  	    
	  	  taskService.complete(waitStateBefore.getId()); 
	  	  
	  	 variables = runtimeService.getVariables(processInstance.getId());
		    
		    for (Entry<String, Object> entry : variables.entrySet()) {
				
		    	System.out.println(entry.getKey() + "/" + entry.getValue());
		    
			}
	    
	 // check userTask
	    // using camunda-bpm-assert
		assertThat(processInstance).isStarted()
				.isWaitingAt("user_view_prediction").task()
				.isAssignedTo("john");
	    
	  
	    
	    // or using the Java API
//	    Task task = processEngineRule.getTaskService().createTaskQuery().taskCandidateGroup("management").singleResult();
//	    variables.put("approved", Boolean.FALSE);
//	    variables.put("comments", "No, we will not publish this on Twitter");
//	    processEngine().getTaskService().complete(task.getId(), variables);

	    // assert that all activities where passed
		
		
	    assertThat(processInstance) //
	    	.isEnded() //
	    	.hasPassed("startViewPrediction","user_action_prediction","processML","user_view_prediction","finish");
	    
	    //ProcessTestCoverage.calculate(processInstance, processEngineRule.getProcessEngine());
	  }
	  
	  
	  @After
	  public void calculateCoverage() throws Exception {
	    // calculate coverage for all tests
	    ProcessTestCoverage.calculate(processEngineRule.getProcessEngine());
	  }  
}
