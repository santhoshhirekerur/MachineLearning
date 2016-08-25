package com.simple.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.ml.commons.domain.MLModel;
import org.wso2.carbon.ml.core.exceptions.MLModelHandlerException;
import org.wso2.carbon.ml.core.impl.Predictor;

public class MLPredictionHelper {
	private static final Log logger = LogFactory.getLog(MLPredictionHelper.class);

	public static void main(String[] args) throws Exception {

		MLPredictionHelper modelUsageSample = new MLPredictionHelper();

		// Path to downloaded-ml-model (downloaded-ml-model can be found inside
		// resources folder)
		URL resource = MLPredictionHelper.class.getClassLoader().getResource(
				"downloaded_ML_model_new_3");
		String pathToDownloadedModel = new File(resource.toURI())
				.getAbsolutePath();

		// Deserialize
		MLModel mlModel = modelUsageSample
				.deserializeMLModel(pathToDownloadedModel);

		// Predict
		String[] featureValueArray1 = new String[] { "CBA Graphic Design team",
				"Upload high-res assets", "1", "Y" };
		String[] featureValueArray2 = new String[] { "Brand",
				"Upload for review", "1", "Y" };
		String[] featureValueArray3 = new String[] { "D&T ? Acquisition and Online",
				"No action", "1", "Y" };
		String[] featureValueArray4 = new String[] { "MOCK PROJECT",
				"Complete a form", "1", "N" };
		
		ArrayList<String[]> list = new ArrayList<String[]>();
		list.add(featureValueArray1);
		list.add(featureValueArray2);
		list.add(featureValueArray3);
		list.add(featureValueArray4);
		List<?> predictions=modelUsageSample.predict(list, mlModel);
		
		for (Object object : predictions) {
			
			logger.info("Prediction : " + object);
		}
	}

	/**
	 * Deserialize to MLModel object
	 * 
	 * @return A {@link org.wso2.carbon.ml.commons.domain.MLModel} object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws URISyntaxException
	 */
	public MLModel deserializeMLModel(String pathToDownloadedModel)
			throws IOException, ClassNotFoundException, URISyntaxException {

		FileInputStream fileInputStream = new FileInputStream(
				pathToDownloadedModel);
		ObjectInputStream in = new ObjectInputStream(fileInputStream);
		MLModel mlModel = (MLModel) in.readObject();

		logger.info("Algorithm Type : " + mlModel.getAlgorithmClass());
		logger.info("Algorithm Name : " + mlModel.getAlgorithmName());
		logger.info("Response Variable : " + mlModel.getResponseVariable());
		logger.info("Features : " + mlModel.getFeatures());
		return mlModel;
	}

	/**
	 * Make Predictions using Predictor
	 * 
	 * @param featureValuesList
	 *            Feature values to be used for the prediction
	 * @param mlModel
	 *            MLModel to be used for predictions
	 * @throws MLModelHandlerException
	 */
	public List<?> predict(List<String[]> featureValuesList, MLModel mlModel)
			throws MLModelHandlerException {

		// Validate number of features
		// Number of feature values in the array should be same as the number of
		// features in the model
		for (String[] featureValues : featureValuesList) {
			if (featureValues.length != mlModel.getFeatures().size()) {
				logger.error("Number of features in the array does not match the number of features in the model.");
				return null;
			}
		}

		Predictor predictor = new Predictor(0, mlModel, featureValuesList);
		List<?> predictions = predictor.predict();

//		logger.info("Feature values list 1 : "
//				+ Arrays.toString(featureValuesList.get(0)));
		//logger.info("Prediction : " + predictions.get(0));

//		logger.info("Feature values list 2 : "
//				+ Arrays.toString(featureValuesList.get(1)));
		//logger.info("Prediction : " + predictions.get(1));
		return  predictions;
	}
}
