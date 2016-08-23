package com.simple.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.complete;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;


public class SimpleTestCase {
	
	@Rule
	  public ProcessEngineRule rule = new ProcessEngineRule();

	  @org.junit.Test
	  @Deployment(resources = {"testProcess.bpmn"})
	  public void shouldExecuteProcess() {
	    // Given we create a new process instance
	    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("testProcess");
	    // Then it should be active
	    assertThat(processInstance).isActive();
	    // And it should be the only instance
	    assertThat(processInstanceQuery().count()).isEqualTo(1);
	    // And there should exist just a single task within that process instance
	    assertThat(task(processInstance)).isNotNull();

	    // When we complete that task
	    complete(task(processInstance));
	    // Then the process instance should be ended
	    assertThat(processInstance).isEnded();
	  }

}
