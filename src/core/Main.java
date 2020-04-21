package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import classify.Classifier;
import classify.ClassifierResult;
import classify.ClassifierRunner;
import classify.TweetParser;
import io.Configuration;
import io.IConfig;
import io.log.Logger;
import io.log.LoggerFactory;

public class Main {


	public static final int FILE_BUFFER_SIZE = 1024*256;
	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws InvalidPropertiesFormatException, IOException {
		
		
		
		String usageOutputString = "Usage: -prop <path to java configuration xml file>";
		//check for desired arguments (-prop <config path>)
		if(args.length != 2){
			
			System.out.println(usageOutputString);
			return;
		}
		if(!args[0].equals("-prop")){
			System.out.println(usageOutputString);
			return;
		}
		
		//configure global log instance to output to stdout
		Logger log = LoggerFactory.getInstance();
		log.addOutputStream(System.out);
		
		
		IConfig config = new Configuration(args[1]);
		
		String logLevel = config.getProperty(IConfig.PROPERTY_LOG_LEVEL);
		Logger.setLogLevel(logLevel);
		
		
		//check if were considering neutral tweets
		boolean consideringNeutralTweets = config.getBooleanProperty(IConfig.PROPERTY_INCLUDING_NEUTRAL_TWEETS);
		
		//populate the list of labels used in this run depending on
		//if we considering neutral tweets
		List<String> labels = new ArrayList<String>(3);
		if(consideringNeutralTweets){
			
			labels.add(Classifier.POSITIVE_LABEL_VALUE);
			labels.add(Classifier.NEUTRAL_LABEL_VALUE);
			labels.add(Classifier.NEGATIVE_LABEL_VALUE);
			
		}else{
			labels.add(Classifier.POSITIVE_LABEL_VALUE);
			labels.add(Classifier.NEGATIVE_LABEL_VALUE);
		}
		config.setProperties(IConfig.PROPERTY_LABEL_SET,labels);
		
		log.log_info("Twitter Sentiment Anlysis COMP5118 W2020 Project starting...");
		TweetParser.cleanDatasets(config);
		
		List<ClassifierResult> results=null;
		//results=FusionDatasetCreator.createFusionDatasets(config);
		//results.add(FusionClassifier.trainAndTestFusionModel(config));
		results = ClassifierRunner.runAll(config);
		
		String output = "";
		for(ClassifierResult r : results){
			output+=r.toString();
		}
		
		log.log_info(output);
		log.log_info("Termination...");
		//log.log_info("CLASSIFICATION RESULTS"+TweetParser.NEW_LINE+result);
	}
	
	
	
	

	
	
}
