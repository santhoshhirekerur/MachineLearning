/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simple.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.wso2.carbon.ml.commons.domain.MLModel;

import com.simple.util.MLPredictionHelper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProcessRequestDelegate implements JavaDelegate {

	private final static Logger LOGGER = Logger.getLogger("Predict-REQUESTS");

	public void execute(DelegateExecution execution) throws Exception {
    LOGGER.info("Project_Groups '"+execution.getVariable("Project_Groups")+"'...");
    LOGGER.info("Task_Action '"+execution.getVariable("Task_Action")+"'...");
    LOGGER.info("Number_Task_Assignees '"+execution.getVariable("Number_Task_Assignees")+"'...");
    LOGGER.info("Task_Completed '"+execution.getVariable("Task_Completed")+"'...");
    
    MLPredictionHelper modelUsageSample = new MLPredictionHelper();

	// Path to downloaded-ml-model (downloaded-ml-model can be found inside
	// resources folder)
	URL resource = MLPredictionHelper.class.getClassLoader().getResource(
			"downloaded_ML_model_new");
	String pathToDownloadedModel = new File(resource.toURI())
			.getAbsolutePath();

	// Deserialize
	MLModel mlModel = modelUsageSample
			.deserializeMLModel(pathToDownloadedModel);

	// Predict
	
	String Project_Groups=String.valueOf(execution.getVariable("Project_Groups"));
	String Task_Action=String.valueOf(execution.getVariable("Task_Action"));
	String Number_Task_Assignees=String.valueOf(execution.getVariable("Number_Task_Assignees"));
	String Task_Completed=String.valueOf(execution.getVariable("Task_Completed"));
	
//	String[] featureValueArray1 = new String[] { "Home Loans",
//			"Upload for review", "1", "Y" };
//	String[] featureValueArray2 = new String[] { "Regional Agri Business",
//			"Upload for review", "1", "Y" };
	
	String[] featureValueArray1 = new String[4];
	
	featureValueArray1[0]=Project_Groups;
	featureValueArray1[1]=Task_Action;
	featureValueArray1[2]=Number_Task_Assignees;
	featureValueArray1[3]=Task_Completed;
									
			
	ArrayList<String[]> list = new ArrayList<String[]>();
	list.add(featureValueArray1);
	
	List<?> predictions=modelUsageSample.predict(list, mlModel);
	
	for (Object object : predictions) {
		
		LOGGER.info("Prediction : " + object);
	}
	
	execution.setVariable("Prediction", predictions.get(0));
	
  }
}
