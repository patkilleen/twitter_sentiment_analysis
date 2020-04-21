package classify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import common.util.Util;
import core.Main;
import io.FileAppender;
import io.FileHandler;
import io.IConfig;
import io.log.Logger;
import io.log.LoggerFactory;

public class FusionDatasetCreator {

	//public static final String [] CLASSIFIERS_USED = {ClassifierBuilder.MAX_ENT,ClassifierBuilder.NB,ClassifierBuilder.SVM,ClassifierBuilder.NLP_BASED};
	public static final String [] CLASSIFIERS_USED = {ClassifierBuilder.MAX_ENT,ClassifierBuilder.NB,ClassifierBuilder.SVM,ClassifierBuilder.NLP_BASED};
	//public static final String [] CLASSIFIERS_USED = {ClassifierBuilder.MAX_ENT,ClassifierBuilder.NB,ClassifierBuilder.NLP_BASED};
	public FusionDatasetCreator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * runs the classifiers on development data and training data, creates the fusion datasets
	 * , and returns the classifier results
	 * @param config
	 * @return classifier results used to create fusion datasets
	 */
	public static List<ClassifierResult>  createFusionDatasets(IConfig config){

/*		boolean testDataSampling =config.getBooleanProperty(IConfig.PROPERTY_SAMPLING_TEST_DATA_FLAG); 	
		boolean devDataSampling =config.getBooleanProperty(IConfig.PROPERTY_SAMPLING_DEV_DATA_FLAG); 
		
		//if both test and dev data sampling from training data, can't do it concurrently,
		//since training data will shrink from both, and we don't want duplicate tweets
		List<ClassifierResult>  result=null;
		if(testDataSampling && devDataSampling){
			//String result = "";
			//need to run both in serial
			try {
				result =createFusionTrainingDataset(config);
				result.addAll(createFusionTestingDataset(config));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}else{//can run concurrently
*/			
		List<ClassifierResult>  result=null;
			FusionTestingDatasetWorker worker1 = new FusionTestingDatasetWorker(config);
			FusionTrainingDatasetWorker worker2 = new FusionTrainingDatasetWorker(config);

			Thread t1 = new Thread(worker1);
			Thread t2 = new Thread(worker2);
	
			
			t1.start();
			t2.start();
			try {
				t1.join();
				t2.join();
				
				//result+="development data classification results: "+TweetParser.NEW_LINE+worker1.result;
				//result+="testing data classification results: "+TweetParser.NEW_LINE+worker2.result;
				result = worker1.result;
				result.addAll(worker2.result);
				return result;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	//	}
		return null;
	}



	/**
	 * Creates a fusion training dataset by a) reading the input development data, b) training the 
	 * classifiers on the training data using the Core StanfordNLP Library, c) excluding classifiers
	 * that are to similar (are not diverse enough) using the Q diversity measure, and d) outputing
	 * fusion training data tuples (of the form <classifier 1 prediction, c2 prediction, ..., cn prediction, actual label>)
	 * to an output file.
	 * This training dataset will be used by the fusion model (R script) to learn its u measures using the HLMS algorithm.
	 * 
	 * The parameters used from the config instance are as follows:
	 * 	>PROPERTY_ROOT_PATH : the base root path for resolving relative file paths
	 *  >PROPERTY_CORE_STANFORD_NLP_JAR_PATH : relative path to core stanford NLP library jar file
	 *  >PROPERTY_CLASSIFIER_PROP_FILE : relative path to the properties file (.prop) used by stanfor NLP library
	 *  >PROPERTY_FUSION_TRAINING_DATA_FILE : the relative file path to append the fusion training samples to
	 *  
	 * @param config Confiruation instance used to parse parameters to the system.
	 * @throws IOException thrown fail to write to fusion output file
	 * @return the classifier results of the classifiers used to create the fusion dataset
	 */
	public static List<ClassifierResult> createFusionTrainingDataset(IConfig config) throws IOException {


		
		Path devDatasetPath = null;
		Path trainDatasetPath = null;
		
	/*	boolean devDataSampling =config.getBooleanProperty(IConfig.PROPERTY_SAMPLING_DEV_DATA_FLAG); 	
		
		//are we sampling from the training dataset to create the clean dev dataset?
		if(devDataSampling){
			
			
			File sampledDevDataFile = File.createTempFile("sampledDevData", null);
			File newTrainignDataFile = File.createTempFile("newTrainingData", null);
			
			double sampleProb = config.getDoubleProperty(IConfig.PROPERTY_DEV_DATA_PROPORTION);
			
			String src = config.getProperty(IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE);
			
			//create the dev data and remove samples from training data
			FileHandler.createSampleFile(src, newTrainignDataFile.getAbsolutePath(), sampledDevDataFile.getAbsolutePath(), sampleProb);
			
			devDatasetPath=sampledDevDataFile.toPath();
			trainDatasetPath=newTrainignDataFile.toPath();
				
		}else{//not sampling, just use the files as usual
		*/	//devDatasetPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLEANED_DEV_DATA_FILE);
			
			devDatasetPath =Util.tryParsePath(config,IConfig.PROPERTY_CLEANED_DEV_DATA_FILE);
			
			
		
		
			//trainDatasetPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE);
			trainDatasetPath = Util.tryParsePath(config,IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE);
			
		//}
		
		Logger log = LoggerFactory.getInstance();
		log.log_info("creating fusion training data by running classifiers on development data...");
		/*
		 * train and then run the development data trhough the classifiers
		 */
		
		List<AbstractClassifier> classifiers = new ArrayList<AbstractClassifier>(4);

		for(String classifierId : CLASSIFIERS_USED){
			//pass the development dataset to classifiers. It will act as the test dataset
			//the predictions of dev data will be ussed to create fusion training data set
			AbstractClassifier classifier = ClassifierBuilder.build(classifierId,devDatasetPath.toString(),trainDatasetPath.toString(),config);

			//is it the nlp-based appraoch?
		/*	if(classifier instanceof NLPBasedClassifier){
				
				//set the predictions fiel (if it exists) to nlp classifier
				String nlpPredictionsFile = config.getProperty(IConfig.PROPERTY_CLEANED_NLP_BASED_LABELED_DEV_DATA_FILE);
				NLPBasedClassifier nlpClassifier = (NLPBasedClassifier) classifier;
				nlpClassifier.setNLPBasedPredictionsFile(nlpPredictionsFile);
			}*/
			//i sholud do this in parralele
			//classifier.trainAndClassify();//trains (if applicable) the model off training data and tests on the development data
			classifiers.add(classifier);
		}

		//train in parrallel
		ClassifierRunner.runClassifiers(classifiers);
		log.log_info("finished running all in fusino training");
		
		//String result = "";
		
		//List<Label> labelset = AbstractClassifier.getLabelSet(config);
		
		/*for(AbstractClassifier c : classifiers){
			result += c.stringifyPerformance(labelset) +TweetParser.NEW_LINE;
		}*/
		//here should joint threadsa
		//throw new IllegagelStaeExeption("need to parreleilize");
		//for completness, make sure classifiers exist
		if(classifiers.isEmpty()){
			log.log_error("failed to classify any classifier succesfully, exiting...");
			System.exit(1);
		}
		//note that size of first classifier is equal to all others
		int numberOfInstances = classifiers.get(0).size();

		double qThreshold= config.getDoubleProperty(IConfig.PROPERTY_Q_DIVERSITY_THRESHOLD);
		//apply Q mesure to remove any classifier that don't have diverse results
		//classifiers = diversifyClassifiers(classifiers,qThreshold);

		log.log_debug("merging classifications into fusion tuples...");
		//throw new IllegalStateException("need to consider case that classifiers are not diverse enogh (empty list)");
		List<FusionInstance> fusionTrainingData = new ArrayList<FusionInstance>(numberOfInstances);

		//iterate all the dev data preditions for each classifier and create fusion sample/datum
		for(int i = 0; i < numberOfInstances;i++){
			Label devSample = classifiers.get(0).getRealLabel(i);

			//create the fusion training sample
			FusionInstance fusionInstance = new FusionInstance(devSample);

			//iterate all the classifier's predictions for the ith dev sample and create a fusion sample
			for(AbstractClassifier c : classifiers){
				Label prediction = c.getPredictedLabel(i);
				fusionInstance.addPrediction(prediction);
			}



			//add fusion sample to fusion training data
			fusionTrainingData.add(fusionInstance);
		}

		
		//Path fusionTrainOutputPath = Paths.get(config.getProperty(IConfig.PROPERTY_ROOT_PATH),config.getProperty(IConfig.PROPERTY_FUSION_TRAINING_DATA_FILE));
		Path fusionTrainOutputPath=Util.tryParsePath(config,IConfig.PROPERTY_FUSION_TRAINING_DATA_FILE);

		if(!fusionTrainOutputPath.toFile().exists()){
			FileHandler.createFile(fusionTrainOutputPath);
		}
		FileAppender fileIO = new FileAppender(fusionTrainOutputPath,Main.FILE_BUFFER_SIZE);
		
		fileIO.open();

		//now create the fusion training dataset file to allow new phase to train fusion model
		for(FusionInstance fd: fusionTrainingData){

			String outputLine = fd.toString() + TweetParser.NEW_LINE;
			//FileHandler.append(fusionTrainOutputPath, outputLine.getBytes());
			fileIO.append(outputLine);
		}
		fileIO.flush();
		fileIO.close();
		
		
		log.log_debug("finished appending training fusion instances...");
		
		List<Label> labelset = AbstractClassifier.getLabelSet(config);
		List<ClassifierResult> results = new ArrayList<ClassifierResult>(classifiers.size());
		
		for(AbstractClassifier c : classifiers){
			ClassifierResult res = c.computeClassifierResult(labelset);
			String name = res.getClassifierName();
			
			name= "Fusion Training result: "+name;
			res.setClassifierName(name);
			
			results.add(res);
		}
		return results;
	}


	/**
	 * Creates a fusion test dataset by a) reading the input testing data, b) training the 
	 * classifiers on the training data using the Core StanfordNLP Library, and d) outputing
	 * fusion test data tuples (of the form <classifier 1 prediction, c2 prediction, ..., cn prediction, actual label>)
	 * to an output file.
	 * This testing dataset will be used by the fusion model (R script) to classify the fusion tuples 
	 * using the u measure it learned from the HLMS algorithm, and then predicts the fusion test tuple' class/label
	 * by applying the Choquet integral
	 * 
	 * The parameters used from the config instance are as follows:
	 * 	>PROPERTY_ROOT_PATH : the base root path for resolving relative file paths
	 *  >PROPERTY_CORE_STANFORD_NLP_JAR_PATH : relative path to core stanford NLP library jar file
	 *  >PROPERTY_CLASSIFIER_PROP_FILE : relative path to the properties file (.prop) used by stanfor NLP library
	 *  >PROPERTY_FUSION_TESTING_DATA_FILE : the relative file path to append the fusion training samples to
	 *  
	 * @param config Confiruation instance used to parse parameters to the system.
	 * @throws IOException thrown fail to write to fusion output file
	 * @return the classifier results of the classifiers used to create the fusion dataset
	 */
	public static List<ClassifierResult> createFusionTestingDataset(IConfig config) throws IOException {


		//get the absoulute path by concatenating root path and relative path of files
		//Path stanfordNLPJarPath = Paths.get(config.getProperty(IConfig.PROPERTY_ROOT_PATH),config.getProperty(IConfig.PROPERTY_CORE_STANFORD_NLP_JAR_PATH));
		//Path propPath = Paths.get(config.getProperty(IConfig.PROPERTY_ROOT_PATH),config.getProperty(IConfig.PROPERTY_CLASSIFIER_PROP_FILE));

		//ClassifierBuilder classifierBuilder = new ClassifierBuilder(stanfordNLPJarPath.toString(),propPath.toString());

		
		

		Path testDataPath = null;
		Path trainDatasetPath = null;
		
		/*boolean testDataSampling =config.getBooleanProperty(IConfig.PROPERTY_SAMPLING_TEST_DATA_FLAG); 	
		
		//are we sampling from the training dataset to create the clean test dataset?
		if(testDataSampling){
			
			
			File sampledTestDataFile = File.createTempFile("sampledTestData", null);
			File newTrainignDataFile = File.createTempFile("newTrainingData", null);
			
			double sampleProb = config.getDoubleProperty(IConfig.PROPERTY_TEST_DATA_PROPORTION);
			
			String src = config.getProperty(IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE);
			
			//create the dev data and remove samples from training data
			FileHandler.createSampleFile(src, newTrainignDataFile.getAbsolutePath(), sampledTestDataFile.getAbsolutePath(), sampleProb);
			
			testDataPath=sampledTestDataFile.toPath();
			trainDatasetPath=newTrainignDataFile.toPath();
				
		}else{//not sampling, just use the files as usual
*/
			//testDataPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLEANED_TESTING_DATA_FILE);
			
		
			testDataPath = Util.tryParsePath(config,IConfig.PROPERTY_CLEANED_TESTING_DATA_FILE);
			

	//		trainDatasetPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE);
			
			trainDatasetPath=Util.tryParsePath(config,IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE);
			
		//}
		
		

		Logger log = LoggerFactory.getInstance();
		log.log_info("creating fusion testing data by running classifiers on test data...");
		/*
		 * train and then run the development data trhough the classifiers
		 * Note that all classifiers trained on same dataset, so they will
		 * have the same number of classifier instances
		 */
		List<AbstractClassifier> classifiers = new ArrayList<AbstractClassifier>(4);

		for(String classifierId : CLASSIFIERS_USED){
			AbstractClassifier classifier = ClassifierBuilder.build(classifierId,testDataPath.toString(),trainDatasetPath.toString(),config);
			//make new trheads
			//classifier.trainAndClassify();//trains (if applicatble) the model off training data and tests on the test data
/*
			//is it the nlp-based appraoch?
			if(classifier instanceof NLPBasedClassifier){
				
				//set the predictions fiel (if it exists) to nlp classifier
				String nlpPredictionsFile = config.getProperty(IConfig.PROPERTY_CLEANED_NLP_BASED_LABELED_TESTING_DATA_FILE);
				NLPBasedClassifier nlpClassifier = (NLPBasedClassifier) classifier;
				nlpClassifier.setNLPBasedPredictionsFile(nlpPredictionsFile);
			}*/
			
			classifiers.add(classifier);
		}

		//train in parrallel
		ClassifierRunner.runClassifiers(classifiers);
		log.log_info("finished running all in fusino testing");
		
	/*	String result = "";
		
		List<Label> labelset = AbstractClassifier.getLabelSet(config);
		
		//print performance of each classifier
		for(AbstractClassifier c : classifiers){
			//output the performance of each classifier
			
			result += c.stringifyPerformance(labelset) +TweetParser.NEW_LINE;
			

		}
*/

		//throw new IllegagelStaeExeption("need to parreleilize");

		//for completness, make sure classifiers exist
		if(classifiers.isEmpty()){
			log.log_error("failed to classify any classifier succesfully, exiting...");
			System.exit(1);
		}
		//note that size of first classifier is equal to all others
		int numberOfInstances = classifiers.get(0).size();


		log.log_debug("merging test fusion instances...");

		List<FusionInstance> fusionTestData = new ArrayList<FusionInstance>(numberOfInstances);

		//iterate all the dev data preditions for each classifier and create fusion sample/datum
		for(int i = 0; i < numberOfInstances;i++){
			Label testSample = classifiers.get(0).getRealLabel(i);

			//create the fusion testing sample
			FusionInstance fusionDatum = new FusionInstance(testSample);

			//iterate all the classifier's predictions for the ith test sample and create a fusion test sample
			for(AbstractClassifier c : classifiers){
				Label prediction = c.getPredictedLabel(i);
				fusionDatum.addPrediction(prediction);
			}



			//add fusion sample to fusion testing data
			fusionTestData.add(fusionDatum);
		}

		//Path fusionTestingOutputPath = Paths.get(config.getProperty(IConfig.PROPERTY_ROOT_PATH),config.getProperty(IConfig.PROPERTY_FUSION_TESTING_DATA_FILE));
		Path fusionTestingOutputPath=Util.tryParsePath(config,IConfig.PROPERTY_FUSION_TESTING_DATA_FILE);

		if(!fusionTestingOutputPath.toFile().exists()){
			FileHandler.createFile(fusionTestingOutputPath);
		}


		FileAppender fileIO = new FileAppender(fusionTestingOutputPath,Main.FILE_BUFFER_SIZE);
		log.log_debug("appending test fusion instances to :"+fusionTestingOutputPath.toString());
		fileIO.open();

		//now create the fusion testing dataset file to allow new phase (R script) to test fusion model
		for(FusionInstance fd: fusionTestData){

			String outputLine = fd.toString()+TweetParser.NEW_LINE;
			//FileHandler.append(fusionTestingOutputPath, outputLine.getBytes());
			fileIO.append(outputLine);
		}

		fileIO.flush();
		fileIO.close();
		
		
		log.log_info("finished appending test fusion instances...");
		
		List<Label> labelset = AbstractClassifier.getLabelSet(config);
		List<ClassifierResult> results = new ArrayList<ClassifierResult>(classifiers.size());
		
		for(AbstractClassifier c : classifiers){
			ClassifierResult res = c.computeClassifierResult(labelset);
			String name = res.getClassifierName();
			
			name= "Fusion Testing result: "+name;
			res.setClassifierName(name);
			
			results.add(res);
		}
		return results;
	}


	/**
	 * Will remove classifiers that aren't diverse from the provided list. The Q measure is used
	 * For example:
	 * need to consider only diversity of clasifiers. IE, don't fuse SVM and MaxEnt predicitons in fusion training output if they the same
	 * only fuse the output if all classifiers are diverse (Q<1)
	 * Q = (a*d-b*c)/(a*d+b*c), where a is # cases where both classifiers guessed correctly, b is # cases where first classifier made a wrong guess but 2nd classifier made correct guess
	 * #c is # cases where 2nd classifier made wroing guess but 1st made correct, and d is # cases where both made wrong guess
	 * @param classifiers list of classifiers to diversity
	 * @param qThreshold the threshold used to determine what classifier consider diverse
	 */
	private static List<Classifier> diversifyClassifiers(List<Classifier> classifiers, double qThreshold) {

		Logger log = LoggerFactory.getInstance();
		log.log_debug("diversifying classifyiers");

		if((classifiers == null) || classifiers.isEmpty()){
			log.log_error("cannot diversify classifers while building decision profile: empty dataset or classifiers");
			return null;
		}

		List<Classifier> res = new ArrayList<Classifier>(classifiers.size());

		//build a confusion matrix for each classifier pair
		//iterate all classifiers to compare them to each other
		for(int i = 0;i<classifiers.size();i++){

			Classifier c1 = classifiers.get(i);
			//don't re-compare a classifier that's already been compared to all others
			//that is, have j index start from i to skip unecessary checks
			//that have already been done. (we don't want to include a classifier twice into the
			//diverse classifier result)

			for(int j = i+1;j<classifiers.size();j++){// +1 to j to avoid building a confusion matrix comparing the same classifier to itself
				Classifier c2 = classifiers.get(j);

				//about to build the confusion matrix

				int bothCorrect = 0;//(a)number of instances where both classifiers predicted corerctly
				int c1Correctc2Wrong = 0;//(c)number of instances where classifier c1 predicted corerctly but c2 did not
				int c1Wrongc2Correct = 0;//(b)number of instances where classifier c1 predicted wrong but c2 was correct
				int bothWrong = 0;//(d)number of instances where both classifiers predicted wrong

				//iterate all the test instances to get the actual labels and compare to predictions
				int numInstances = c1.size();
				for(int k=0;k<numInstances;k++){
					Label realTag = c1.getRealLabel(k);
					Label c1Prediction = c1.getPredictedLabel(k);
					Label c2Prediction = c2.getPredictedLabel(k);

					//both the same?
					if(c1Prediction.equals(c2Prediction)){

						//correct?
						if(c1Prediction.equals(realTag)){
							bothCorrect++;
						}else{//wrong
							bothWrong++;
						}
					}else{
						//c1 correct?
						if(c1Prediction.equals(realTag)){
							c1Correctc2Wrong++;
						}else{// c2 wrong
							c1Wrongc2Correct++;
						}
					}//end checking prediction equalness
				}//end iterate isntances


				double q = ((double)((bothCorrect*bothWrong) - (c1Wrongc2Correct*c1Correctc2Wrong) ))/(double)((bothCorrect*bothWrong) + (c1Wrongc2Correct*c1Correctc2Wrong));

				//note that the below logic could be a source of error, since the paper wasn't clear on how they decided if a classifier should be
				//added or not, since q measure is for pairs of classifiers, yet the decision is left to determining if a classifier is generally
				//diverse to add it. So logic here is if Q statistic meets the requirement, then we add both classifiers, otherwise don't (their too similar)

				//divesrse classifiers?
				if(q < qThreshold){
					//classifiers are diverse, add them if not already added
					boolean c1FoundInResult = false;
					boolean c2FoundInResult = false;
					//see if the classifiers c1 and c2 already added to result?
					for(Classifier c :res){
						if(c == c1){
							c1FoundInResult=true;
						}else if (c==c2){
							c2FoundInResult=true;
						}

					}

					//not already added c1 to result?
					if(!c1FoundInResult){
						res.add(c1);
					}
					//not already added c1 to result?
					if(!c2FoundInResult){
						res.add(c2);
					}
				}//end diversity threshold satisfied
			}//end internal loop
		}//end external forloop

		return res;
	}

	/**
	 * Reads a dataset file into memory and parses it
	 * into a list of labeled instances. The dataset is
	 * expected to have the following format for each line, where a line
	 * represent a training instance:
	 *    label		features
	 * where the label and features are separated by a tab, and the
	 * features are each separated by white space characters. 
	 * @param datasetPath path to the development training dataset
	 * @return list of labeled instances, where the instances are simply represented using an id based on occurrence
	 */
	/*private static List<LabeledInstance> loadDataset(Path datasetPath) {

	Logger log = LoggerFactory.getInstance();

	log.log_debug("reading dataset from file: "+datasetPath);

	//given data file doesn't exist
	if(datasetPath == null || !datasetPath.toFile().exists()){
		throw new IllegalArgumentException("Cannot read dataset: "+datasetPath);
	}

	List<LabeledInstance> res = new ArrayList<LabeledInstance>(1024*32);
	BufferedReader reader;
	try {
		reader = new BufferedReader(new FileReader(
				datasetPath.toString()));
		String line = reader.readLine();
		int id = 0;
		while (line != null) {
		//the first element (delimited by white space) is the label.
			//don't bother storing the data, just use a counter for id of instance
			String[] tokens= line.split("\\s+");

			//make sure the line isn't empty when parsing it into a labeled instance
			if(tokens.length > 0){
				Label l = new Label(tokens[0]);
				LabeledInstance instance = new LabeledInstance(new Integer(id),l);
				res.add(instance);
				id++;
			}

			// read next line
			line = reader.readLine();
		}
		reader.close();
	} catch (IOException e) {
		e.printStackTrace();
	}

	log.log_debug(res.size()+" instances read.");
	return res;
}
	 */



	private static class FusionTrainingDatasetWorker implements Runnable{
		private IConfig config;

		List<ClassifierResult> result;
		public FusionTrainingDatasetWorker(IConfig config){
			this.config = config;
		}

		@Override
		public void run() {
			try {
				result=createFusionTrainingDataset(config);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private static class FusionTestingDatasetWorker implements Runnable{
		private IConfig config;

		List<ClassifierResult> result;
		public FusionTestingDatasetWorker(IConfig config){
			this.config = config;
		}

		@Override
		public void run() {
			try {
				result = createFusionTestingDataset(config);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
