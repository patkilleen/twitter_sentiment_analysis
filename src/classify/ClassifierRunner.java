package classify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import common.util.Util;
import io.FileHandler;
import io.FoldInfo;
import io.IConfig;
import io.KFoldCrossValidationConsummer;
import io.log.Logger;
import io.log.LoggerFactory;
import io.log.ProgressLogger;

public class ClassifierRunner{


	private List<ClassifierResult> cummulativeResults;
	private IConfig config;

	public ClassifierRunner(IConfig config) {
		this.config=config;
		cummulativeResults = null;
	}



	public static void runClassifiers(List<AbstractClassifier> classifiers){

		List<Thread> workers = new ArrayList<Thread>(classifiers.size()); 
		for(AbstractClassifier c : classifiers){
			Worker w = new Worker(c);
			Thread t = new Thread(w);
			workers.add(t);
			t.start();
		}

		//wait for all the classifiers
		for(Thread t : workers){
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static List<ClassifierResult> runAll(IConfig config) throws IOException{

		boolean testDatakfoldCVFlag = config.getBooleanProperty(IConfig.PROPERTY_TEST_DATASET_USING_K_FOLD_CROSS_VALIDATION_FLAG);
		boolean devKfoldCVFlag = config.getBooleanProperty(IConfig.PROPERTY_DEV_DATASET_USING_K_FOLD_CROSS_VALIDATION_FLAG);
		List<ClassifierResult> results=null;
		if(!testDatakfoldCVFlag && !devKfoldCVFlag){//no k-fold cross validation?

			results=FusionDatasetCreator.createFusionDatasets(config);
			results.add(FusionClassifier.trainAndTestFusionModel(config));

		}else if(testDatakfoldCVFlag){//doing k-fold cross validation on test dataset?

			Logger log = LoggerFactory.getInstance();
			log.log_debug("starting k-fold cross valiation on test data...");
			Path datasetFile = Util.parseAndVerifyPath(config, IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE);

			int testK = config.getIntProperty(IConfig.PROPERTY_TEST_DATA_CROSS_FOLD_VALIDATION_K_VALUE);
			int devK = config.getIntProperty(IConfig.PROPERTY_DEV_DATA_CROSS_FOLD_VALIDATION_K_VALUE);

			ProgressLogger progLogger = new ProgressLogger(0.05,testK*devK);
			
			boolean randomizeTrainingDataFile  = config.getBooleanProperty(IConfig.PROPERTY_TRAINING_DATASET_RANDOM_SORT_FLAG);
			
			TestDataKFCV testDataKFCV = new TestDataKFCV(devK,config,progLogger); 
			
			//are we randomly sorting the training dta fil?
			if(randomizeTrainingDataFile){
				File randPermDataFile = File.createTempFile("randomPermData", null);
				//randPermDataFile.deleteOnExit();
				log.log_info("creating a random sorted training datafile : "+randPermDataFile.getAbsolutePath());
				FileHandler.randomPermutation(datasetFile,randPermDataFile.toPath());
				FileHandler.performKFoldCrossValidation(testDataKFCV, randPermDataFile.toPath(), testK);
			}else{
				FileHandler.performKFoldCrossValidation(testDataKFCV, datasetFile, testK);//just use training data file, no random sort
			}
			
			
			

			results=testDataKFCV.getResults();
			log.log_debug("finished k-fold cross valiation...");
		}else{////doing k-fold cross validation on dev dataset

			Logger log = LoggerFactory.getInstance();
			log.log_debug("starting k-fold cross valiation on dev data...");

			//random permutation of training file 
			Path datasetFile = Util.parseAndVerifyPath(config, IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE);
			
			//get the k value for k-fold cross validation (how many folds)
			int devK = config.getIntProperty(IConfig.PROPERTY_DEV_DATA_CROSS_FOLD_VALIDATION_K_VALUE);
			//progress logger, log each fold
			ProgressLogger progLogger = new ProgressLogger(1,devK);
			DevDataKFCV devDataKFCV = new DevDataKFCV(config,progLogger);
			
			boolean randomizeTrainingDataFile  = config.getBooleanProperty(IConfig.PROPERTY_TRAINING_DATASET_RANDOM_SORT_FLAG);
			if(randomizeTrainingDataFile){
				File randPermDataFile = File.createTempFile("randomPermData", null);
				//randPermDataFile.deleteOnExit();
				
				log.log_info("creating a random sorted training datafile : "+randPermDataFile.getAbsolutePath());
				FileHandler.randomPermutation(datasetFile,randPermDataFile.toPath());

				//perform k-fold cross validation on the training dataset fragment (the test was produced dynamically
				//using k-fold CV as well)
				
				FileHandler.performKFoldCrossValidation(devDataKFCV, randPermDataFile.toPath(), devK);
			}else{
				FileHandler.performKFoldCrossValidation(devDataKFCV, datasetFile, devK);
			}
			//get aggregated results
			results=devDataKFCV.getResults();
			
			log.log_debug("finished k-fold cross valiation...");
			
			
		}

		return results;
	}

	private static class Worker implements Runnable{
		private AbstractClassifier classifier;

		public Worker(AbstractClassifier classifier){
			this.classifier=classifier;
		}

		@Override
		public void run() {
			this.classifier.trainAndClassify();
		}
	}


	private static class TestDataKFCV implements KFoldCrossValidationConsummer{

		
		private int i;
		private int k;

		//private DevDataKFCV devDataKFCV;
		private IConfig config;

		private List<ClassifierResult> cummulativeResults;
		
		private ProgressLogger progLogger;
		public TestDataKFCV(int k, IConfig config,ProgressLogger progLogger){
			this.k=k;
			cummulativeResults=null;
			this.config =config;
			this.progLogger=progLogger;
		}
		@Override
		public void consumFold(Path training, Path test, FoldInfo info) throws IOException {
			
			

			//PROPERTY_CLEANED_NLP_BASED_LABELED_TESTING_DATA_FILE
			/*
			 * public static final String PROPERTY_CLEANED_NLP_BASED_LABELED_TRAINING_DATA_FILE = "input.nlp-based-labels.train-data-file-path";
	public static final String PROPERTY_CLEANED_NLP_BASED_LABELED_TESTING_DATA_FILE = "input.nlp-based-labels.testing-data-file-path";
	public static final String PROPERTY_CLEANED_NLP_BASED_LABELED_DEV_DATA_FILE = "input.nlp-based-labels.dev-data-file-path";
			 */
			
			//check to see if there is already a NLP-based predictions labels file
		/*	String nlpPredictionsPath = config.getProperty(IConfig.PROPERTY_CLEANED_NLP_BASED_LABELED_TRAINING_DATA_FILE);
			
			
			File nlpPredictionFile = new File(nlpPredictionsPath);
			if(nlpPredictionFile.exists()){
				
				//partition the nlp predictin file exatcly how the fold has
				//been partitioned. This works since instances will be in 
				//identical order in both files, so the indexs of the lines
				//refer to the instance ids
				File testDataNLPPredictionsFile = File.createTempFile("nlpTestPrediction",null);//dont use this file, but api requires one
				testDataNLPPredictionsFile.deleteOnExit();
				FileHandler.subFile(nlpPredictionFile.toPath(),testDataNLPPredictionsFile.toPath(), info.getStartIx(), info.getEndIx());
				
				//make sure key is empty for the NLP's prediction file path to testeing data predctiosn
				config.setProperty(IConfig.PROPERTY_CLEANED_NLP_BASED_LABELED_TESTING_DATA_FILE,testDataNLPPredictionsFile.getAbsolutePath());
			}else{//it doesn't exist
				
				//make sure key is empty for the NLP's prediction file path to testeing data predctiosn
				//config.setProperty(IConfig.PROPERTY_CLEANED_NLP_BASED_LABELED_TESTING_DATA_FILE,null);
			}
			*/
			//set the test file to the fold
			config.setProperty(IConfig.PROPERTY_CLEANED_TESTING_DATA_FILE, test.toString());

			
			List<ClassifierResult> newResults = null;
			
			//check to see if were dong internal folds (for dev data).
			//maybe a dev data file was given
			boolean devKfoldCVFlag = config.getBooleanProperty(IConfig.PROPERTY_DEV_DATASET_USING_K_FOLD_CROSS_VALIDATION_FLAG);
			if(devKfoldCVFlag){
				DevDataKFCV devDataKFCV = new DevDataKFCV(config,progLogger);
	
				//perform k-fold cross validation on the training dataset fragment (the test was produced dynamically
				//using k-fold CV as well)
	
				FileHandler.performKFoldCrossValidation(devDataKFCV, training, k);
	
				newResults= devDataKFCV.getResults();
				
			
			}else{//not doing k-fold cross validation on dev data
				newResults=FusionDatasetCreator.createFusionDatasets(config);
				newResults.add(FusionClassifier.trainAndTestFusionModel(config));
			}
			
			//first test data fold and first getting results of k-fold cross validation
			//on dev data?
			if (cummulativeResults==null){
				cummulativeResults = newResults;
			}else{//results already exist, start to merge results
				ClassifierResult.addClassifierResults(newResults,cummulativeResults);
			}//end check if first time results
			
			i++;
			progLogger.logProgress("high-level (training-testing) fold "+i+"/"+k+" finished");
		}

		public List<ClassifierResult> getResults() {
			// TODO Auto-generated method stub
			return cummulativeResults;
		}
	}

	private static class DevDataKFCV implements KFoldCrossValidationConsummer{
		private List<ClassifierResult> cummulativeResults;
		private IConfig config;
		private int i;

		ProgressLogger progLogger;
		public DevDataKFCV(IConfig config,ProgressLogger progLogger){
			this.config=config;

			cummulativeResults = null;
			this.progLogger=progLogger;
		}

		@Override
		public void consumFold(Path training, Path dev, FoldInfo info) throws IOException {


			//PROPERTY_CLEANED_NLP_BASED_LABELED_TESTING_DATA_FILE
			/*
			 * public static final String PROPERTY_CLEANED_NLP_BASED_LABELED_TRAINING_DATA_FILE = "input.nlp-based-labels.train-data-file-path";
	public static final String PROPERTY_CLEANED_NLP_BASED_LABELED_TESTING_DATA_FILE = "input.nlp-based-labels.testing-data-file-path";
	public static final String PROPERTY_CLEANED_NLP_BASED_LABELED_DEV_DATA_FILE = "input.nlp-based-labels.dev-data-file-path";
			 */
		/*	
			//check to see if there is already a NLP-based predictions labels file
			String nlpPredictionsPath = config.getProperty(IConfig.PROPERTY_CLEANED_NLP_BASED_LABELED_TRAINING_DATA_FILE);
			File nlpPredictionFile = new File(nlpPredictionsPath);
			if(nlpPredictionFile.exists()){
				
				//partition the nlp predictin file exatcly how the fold has
				//been partitioned. This works since instances will be in 
				//identical order in both files, so the indexs of the lines
				//refer to the instance ids
				File devDataNLPPredictionsFile = File.createTempFile("nlpTestPrediction",null);//dont use this file, but api requires one
				devDataNLPPredictionsFile.deleteOnExit();
				FileHandler.subFile(nlpPredictionFile.toPath(),devDataNLPPredictionsFile.toPath(), info.getStartIx(), info.getEndIx());
				
				//make sure key is empty for the NLP's prediction file path to testeing data predctiosn
				config.setProperty(IConfig.PROPERTY_CLEANED_NLP_BASED_LABELED_DEV_DATA_FILE,devDataNLPPredictionsFile.getAbsolutePath());
			}else{//it doesn't exist
				
				//make sure key is empty for the NLP's prediction file path to testeing data predctiosn
			//	config.setProperty(IConfig.PROPERTY_CLEANED_NLP_BASED_LABELED_DEV_DATA_FILE,null);
			}
			*/
		
			//here change the path of training, dev, and test data
			//as well as fusion data paths (make temporary files)


			File fusionTestingOutputPath;
			File fusionTrainOutputPath;

			fusionTestingOutputPath = File.createTempFile("fusionTestingOutput", null);
			fusionTrainOutputPath = File.createTempFile("fusionTrainOutput", null);


			fusionTestingOutputPath.deleteOnExit();
			fusionTrainOutputPath.deleteOnExit();
			
			config.setProperty(IConfig.PROPERTY_FUSION_TESTING_DATA_FILE, fusionTestingOutputPath.getAbsolutePath());
			config.setProperty(IConfig.PROPERTY_FUSION_TRAINING_DATA_FILE, fusionTrainOutputPath.getAbsolutePath());
			config.setProperty(IConfig.PROPERTY_CLEANED_DEV_DATA_FILE, dev.toString());
			config.setProperty(IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE, training.toString());


			List<ClassifierResult> newResults=FusionDatasetCreator.createFusionDatasets(config);
			newResults.add(FusionClassifier.trainAndTestFusionModel(config));

			//first time getting a result?
			if(cummulativeResults == null){	
				cummulativeResults=newResults;
			}else{
				ClassifierResult.addClassifierResults(newResults,cummulativeResults);
			}

			i++;
			
			progLogger.logProgress("low-level (training-dev) fold "+i+" finished");
		}
		public List<ClassifierResult> getResults() {
			// TODO Auto-generated method stub
			return cummulativeResults;
		}
	}
}
