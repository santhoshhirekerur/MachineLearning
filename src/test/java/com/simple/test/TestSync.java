package com.simple.test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.Rule;
import org.junit.Test;

import com.simple.camunda.ProcessRequestDelegate;

public class TestSync {
	private static ProcessEngine processEngine;

	static {

		processEngine = StandaloneInMemProcessEngineConfiguration
				.createStandaloneInMemProcessEngineConfiguration()
				.buildProcessEngine();
	}

	@Rule
	public ProcessEngineRule processEngineRule = new ProcessEngineRule(
			processEngine);

	@Test
	@Deployment(resources = { "view-prediction.bpmn" })
	public void test() {

		//Mocks.register("processRequestAdapter", new ProcessRequestDelegate()); // get expression to work without Spring or CDI in JUnit
	    final RuntimeService runtimeService = processEngineRule.getRuntimeService();
	    final TaskService taskService = processEngineRule.getTaskService();

	     //this invocation should NOT fail
	    Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("Project_Groups", "MOCK PROJECT");
	    variables.put("Task_Action", "Complete a form");
	    variables.put("Number_Task_Assignees", "1");
	    variables.put("Task_Completed", "Y");
	     //start the process instance
	    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("view-prediction", variables);

	     //the process instance is now waiting in the first wait state (user task):
	    Task waitStateBefore = taskService.createTaskQuery()
	      .taskDefinitionKey("user_action_prediction")
	      .processInstanceId(processInstance.getId())
	      .singleResult();
	    assertNotNull(waitStateBefore);

	    assertThat(processInstance).isStarted()
		.isWaitingAt("user_action_prediction").task()
		.isAssignedTo("john");
	    
//	     Complete this task. This triggers the synchronous invocation of the
//	     service task. This method invocation returns after the service task 
//	     has been executed and the process instance has advanced to the second waitstate.
	    taskService.complete(waitStateBefore.getId());

	    
	  //check for variable set by the service task:
	    variables = runtimeService.getVariables(processInstance.getId());
	    
	    for (Entry<String, Object> entry : variables.entrySet()) {
			
	    	System.out.println(entry.getKey() + "/" + entry.getValue());
	    
		}
	    
	     //the process instance is now waiting in the second wait state (user task):
	    Task waitStateAfter = taskService.createTaskQuery()
	      .taskDefinitionKey("user_view_prediction")
	      .processInstanceId(processInstance.getId())
	      .singleResult();
	    assertNotNull(waitStateAfter);
	    
	    taskService.complete(waitStateAfter.getId());
	    
	     
	    //assertEquals(SynchronousServiceTask.PRICE, variables.get(SynchronousServiceTask.PRICE_VAR_NAME));

	  
	}

}
