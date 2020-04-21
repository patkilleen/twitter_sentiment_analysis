package classify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import common.util.Util;
import io.IConfig;

public class ClassifierBuilder {

	
	public static final String SVM = "SVM";
	public static final String NB = "NB";
	public static final String MAX_ENT = "MaxEnt";
	public static final String NLP_BASED = "NLP-Based";
	public static final String FUSION = "Fusion";
	
	/**
	 * Empty constructor. 
	 */
	public ClassifierBuilder() {
		
	}
	
	
	/**
	 * Creates and configures a Classifier object based on the provided name.
	 * @param name The unique name that identifies the target classifier to build.
	 * @param testDatasetPath the path to the dataset that will be used as a test dataset. The data found in this file can be classified by calling trainAndClassify method of classifier.
	 * @return An instance of Classifier or null when a classifier for the given name cannot be resolved.  
	 */
	/*public static Classifier build(String name, String testDatasetPath,IConfig config){
		return build(name,testDatasetPath,null,config);
	}*/
	
	/**
	 * Creates and configures a Classifier object based on the provided name.
	 * @param name The unique name that identifies the target classifier to build.
	 * @param testDatasetPath the path to the dataset that will be used as a test dataset. The data found in this file can be classified by calling trainAndClassify method of classifier.
	 * @param trainingDatasetPath the path to the dataset for training the classifier, or null if classifier doesn't use it
	 * @return An instance of Classifier or null when a classifier for the given name cannot be resolved.  
	 */
	public static AbstractClassifier build(String name, String testDatasetPath,String trainingDatasetPath,IConfig config){
		AbstractClassifier result = null;
		//here we decide what type of object to build based on name
		//then we assign appropriate command
		//for now its hardcoded command
		String cmd = null;
		
		List<Label> labelSet = AbstractClassifier.getLabelSet(config);
		if(name.equals(NB)){
			
			String stanfordNLPJarString = config.getProperty(IConfig.PROPERTY_CORE_STANFORD_NLP_JAR_PATH);
			Path propPath =Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLASSIFIER_PROP_FILE);
			
			
			cmd = "java -cp \""+stanfordNLPJarString+"\" edu.stanford.nlp.classify.ColumnDataClassifier  "
					+ "-prop \""+propPath.toString()+"\" -useNB -testFile "+testDatasetPath+" -trainFile "+trainingDatasetPath;
			
			result = new Classifier(name,cmd);
		}else if(name.equals(MAX_ENT)){
			
			String stanfordNLPJarString = config.getProperty(IConfig.PROPERTY_CORE_STANFORD_NLP_JAR_PATH);
			
			Path propPath =Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLASSIFIER_PROP_FILE);

			
			cmd = "java -cp "+stanfordNLPJarString+" edu.stanford.nlp.classify.ColumnDataClassifier  "
					+ "-prop "+propPath.toString()+" -testFile "+testDatasetPath +" -trainFile "+trainingDatasetPath;
			result = new Classifier(name,cmd);
		}else if(name.equals(FUSION)){
			//need to build command to call the appropriate R script and pass proper parameters
			//gona need to decide what parameter is given
			//gotta make sure the stdout of the R script follows identical output format as
			//the standford classifiers. This way the Classifier class will parse the output
			//in the same way for simplicity
			//this will most likely be
			//>		fusion training file
			//> 	fusion testing file
			
			
			String rScriptExecutablePath = config.getProperty(IConfig.PROPERTY_R_SCRIPT_EXECUTABLE_PATH);	
			Path rScriptFusionModelPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_R_SCRIPT_FUSION_MODEL_PATH);		
			
			String alpha =  config.getProperty(IConfig.PROPERTY_HLMS_ALPHA);		
			

			//create a temporary file used to append all the fusion model's prediction results
			File file;
			try {
				file = File.createTempFile("tempFusionOutput", null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			//file.deleteOnExit();
			Path fusionResultOuputPath = file.toPath();
			
			//Path fusionTestingDatasetPath = Paths.get(config.getProperty(IConfig.PROPERTY_ROOT_PATH),config.getProperty(IConfig.PROPERTY_FUSION_TESTING_DATA_FILE));
			//verifyPath(fusionTestingDatasetPath);
			
			//note that here the testDatasetPath is path to test dataset predictions of format
			//prediction-by-model1,prediction-by-model2,prediction-by-model3,...,prediction-by-modeln,real-tag
			cmd = rScriptExecutablePath+" "+rScriptFusionModelPath+" "+trainingDatasetPath+" "+testDatasetPath+" "+fusionResultOuputPath.toString()+" "+alpha;
			
			/*
			 * cmd: "C:\Users\Not admin\Documents\R\R-3.6.0\\bin\Rscript.exe"  %1 %2 %3 
			 * where %1 is the rscript path, and %2 and %3 are argumentent to r script
			 * in rscript, they are processes as follows:
			 *		args <- commandArgs(TRUE)
	   		 *		output_csv <- args[1]
			 *		output_roc_curve_file <- args[2]
			 */
			result = new FusionClassifier(name,cmd,fusionResultOuputPath,labelSet);	
		}else if(name.equals(NLP_BASED)){
			/*String stanfordNLPJarString = config.getProperty(IConfig.PROPERTY_CORE_STANFORD_NLP_JAR_PATH); 

			//note that the end of the command is not complete. the classifier will dynamically create the file and append it to the command
			//note that the jar lib path should be as follows: .\stanford-corenlp-full-2018-10-05\* 
			//it should have star, casue many jars required
			
			//create a temporary file used to append all the NLP-based model's prediction results
			File file;
			try {
				file = File.createTempFile("tempNLP-BasedOutput", null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			//file.deleteOnExit();
			Path nlpBasedResultOuputPath = file.toPath();
			
			
			//cmd = "java -cp \""+stanfordNLPJarString+"\"  edu.stanford.nlp.pipeline.StanfordCoreNLP -annotators tokenize,ssplit,pos,lemma,parse,sentiment"
			//		+ " -file "+nlpBasedResultOuputPath.toString() +" -outputDirectory "+nlpBasedResultOuputPath.getParent().toString();
			//cmd = "java -cp \""+stanfordNLPJarString+"\"  edu.stanford.nlp.pipeline.StanfordCoreNLP -annotators tokenize,ssplit,pos,lemma,parse,sentiment"
						//+ " -stdIn";
					
			//cmd = "-annotators tokenize,ssplit,pos,lemma,parse,sentiment";
			*/
			
			boolean includingNeutralTweets = Boolean.parseBoolean(config.getProperty(IConfig.PROPERTY_INCLUDING_NEUTRAL_TWEETS));
			result = new NLPBasedClassifier(name,testDatasetPath,includingNeutralTweets);
		}else if(name.equals(SVM)){
			String stanfordNLPJarString = config.getProperty(IConfig.PROPERTY_CORE_STANFORD_NLP_JAR_PATH);
			Path propPath =Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLASSIFIER_PROP_FILE);
			
			
			//convert the test and training data to svmlight format
			boolean consideringNeutralTweets = config.getBooleanProperty(IConfig.PROPERTY_INCLUDING_NEUTRAL_TWEETS);
			Path svmLightTrainingData = SVMClassifier.toSVMLightFormat(stanfordNLPJarString, trainingDatasetPath.toString(), propPath.toString(), testDatasetPath.toString());
			Path svmLightTestingData = SVMClassifier.toSVMLightFormat(stanfordNLPJarString, testDatasetPath.toString(), propPath.toString(), testDatasetPath.toString());
			cmd = "java -cp \""+stanfordNLPJarString+"\" edu.stanford.nlp.classify.ColumnDataClassifier  "
					+ "-prop \""+propPath.toString()+"\" -testFile "+svmLightTestingData+" -trainFile "+svmLightTrainingData+" -trainFromSVMLight true -testFromSVMLight true";
			
			result = new SVMClassifier(name,cmd,consideringNeutralTweets);
		}
		
		
		return result;
	}
}
