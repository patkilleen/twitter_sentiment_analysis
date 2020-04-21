package classify;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import common.util.Util;
import io.IConfig;
import io.ProcessRunner;
import io.ProcessStreamReader;
import io.log.Logger;
import io.log.LoggerFactory;

public class FusionClassifier  extends Classifier implements ProcessStreamReader{

	public static final int FUSION_PREDICTION_IX=0;
	public static final int REAL_LABEL_IX=1;
	
	private Path fusionResultOuputPath;
	
	private List<Label> labelSet;
	
	public FusionClassifier(String name, String command,Path fusionResultOuputPath, List<Label> labelSet) {
		super(name, command);
		this.labelSet=labelSet;
		this.fusionResultOuputPath=fusionResultOuputPath;
	}

	/**
	 * overide the function, since instead of reading from the
	 * fusion model R process' output, we will instead parse the file it
	 * output.
	 */
	@Override
	public void trainAndClassify(){
		Logger log = LoggerFactory.getInstance();
		log.log_debug("About to exectue: "+this.toString());
		ProcessRunner runner = new ProcessRunner(this);
		runner.exec(command);

		//now read the output file and parse the results
		BufferedReader reader;
		try {
			
			
			log.log_debug("reading from fusion result file :"+fusionResultOuputPath.toString());
			
			reader = new BufferedReader(new FileReader(
					fusionResultOuputPath.toFile()));
			String line = reader.readLine();
			while (line != null) {



				//the first element (delimited by tab) is the label.
				//the rest of data are the desired features
				String[] tokens= line.split(TweetParser.COMMA);
				
				
				//make sure the line isn't empty when parsing it into a labeled instance
				if(tokens.length > 0){
					String rawprediction = tokens[FUSION_PREDICTION_IX];
					String realTag = tokens[REAL_LABEL_IX];
					
					//here the prediciton is a real numer, we need to convert it to nearest class/label enum
					//for example, with positive = 4, and negative = 0, a prediction value of 1.5 would be classifier
					// as negative, a value of 3.2 would be considered positive, and gona have to think how ties ar broken
					//evenly randomly ?
					
					double pD = Double.parseDouble(rawprediction);
					
					Label predictionLabel = toActualPrediction(pD);
					Label rLabel = new Label(realTag);
					LabelPair p = new LabelPair(rLabel, predictionLabel);
					this.predictions.add(p);
					
				}

				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	
	}

	/**
	 * takes a real value prediction are converts to nearest class value
	 * @param pD the predeiction double value
	 * @return nearest prediction class to double value
	 */
	private Label toActualPrediction(double pD) {
		int nearestInt = (int) Math.round(pD);
		
		int delta = Integer.MAX_VALUE;
		Label result = null;
		
		//iterate all possible labels and check for nearest match
		for(Label l : this.labelSet){
			
			try{
				int value = Integer.parseInt(l.getValue());
				int tmpDelta = Math.abs(nearestInt - value);
				//found nearest label for prediction?
				if(tmpDelta < delta){
					delta = tmpDelta;
					result = l;
				}
			}catch(NumberFormatException e){
				Logger log = LoggerFactory.getInstance();
				log.log_error("cannot convert fusion real-number prediction to int, since the labels aren't intergers. Check the following key in config file: "+IConfig.PROPERTY_LABEL_SET);
			}
			
		}
		return result;
	}

	@Override
	public void readLine(String line) {
		//we basiclly ignore the line, we have nothing to do with the output
		//otherwise we can print the output of R script for debugging purposes
		//Logger log = LoggerFactory.getInstance();
		//log.log_debug(line);
		
	}
	
	/**
	 * Trains and tests the fusion model. It will call an external R script 
	 * to perform modeling. The r scrpt will read the fusion training dataset
	 * to find the u measures using HLMS algorithm, and will then read the test fusion dataset
	 * to then predict the labels for each sample.
	 * Finally, when R script is finished outputing, this function will have parsed
	 * its output into predictions, and then output the performance of the fusion model.
	 * 
	 * @param config
	 * @return the string representation of results of classification
	 */
	public static ClassifierResult trainAndTestFusionModel(IConfig config) {
		
		
	
		//Path fusionTestingDatasetPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_FUSION_TESTING_DATA_FILE);
		//Path fusionTrainDatasetPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_FUSION_TRAINING_DATA_FILE);
		
		Path fusionTestingDatasetPath = Util.tryParsePath(config,IConfig.PROPERTY_FUSION_TESTING_DATA_FILE);
		Path fusionTrainDatasetPath = Util.tryParsePath(config,IConfig.PROPERTY_FUSION_TRAINING_DATA_FILE);
		
		AbstractClassifier classifier = ClassifierBuilder.build(ClassifierBuilder.FUSION,fusionTestingDatasetPath.toString(),fusionTrainDatasetPath.toString(),config);
		
		classifier.trainAndClassify();//trains the model off training data and tests on the test data
		
		List<Label> labelset = getLabelSet(config);
		
		return classifier.computeClassifierResult(labelset);
		
	}

}
