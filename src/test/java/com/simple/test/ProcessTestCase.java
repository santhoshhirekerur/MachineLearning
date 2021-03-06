package com.simple.test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.processEngine;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.complete;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.consulting.process_test_coverage.ProcessTestCoverage;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.util.LogUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.simple.camunda.ProcessRequestDelegate;

public class ProcessTestCase {

	private static ProcessEngine processEngine;

	static {

		processEngine = StandaloneInMemProcessEngineConfiguration
				.createStandaloneInMemProcessEngineConfiguration()
				.buildProcessEngine();
	}
	  
  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRule(processEngine);

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
    assertThat(processInstance).isStarted() //
    	.isWaitingAt("user_action_prediction") //
    	.task() //
    	.isAssignedTo("demo");
    
    //complete(task(), Variables.createVariables().putValue("approved", Boolean.FALSE));
    
    // or using the Java API
//    Task task = processEngineRule.getTaskService().createTaskQuery().taskCandidateGroup("management").singleResult();
//    variables.put("approved", Boolean.FALSE);
//    variables.put("comments", "No, we will not publish this on Twitter");
//    processEngine().getTaskService().complete(task.getId(), variables);

    // assert that all activities where passed
    assertThat(processInstance) //
    	.isEnded() //
    	.hasPassed("startViewPrediction","user_action_prediction");

    ProcessTestCoverage.calculate(processInstance, processEngineRule.getProcessEngine());
  }
  
  
  @After
  public void calculateCoverage() throws Exception {
    // calculate coverage for all tests
    ProcessTestCoverage.calculate(processEngineRule.getProcessEngine());
  }  

}