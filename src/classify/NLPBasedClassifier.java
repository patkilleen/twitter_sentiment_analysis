package classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import io.FileAppender;
import io.FileHandler;
import io.log.Logger;
import io.log.LoggerFactory;
import io.log.ProgressLogger;

public class NLPBasedClassifier extends AbstractClassifier {


	public static final String ANNOTATORS_KEY = "annotators";
	public static final String ANNOTATORS = "tokenize, ssplit, pos, lemma, parse, sentiment";

	public static final String TOKENIZE_OPTIONS_KEY = "tokenize.options";
	public static final String TOKENIZE_OPTIONS = "untokenizable=noneDelete";


	public static final String TAB_REGEX = "\\t";
	public static final String XML_FILE_EXTENSION = ".xml";
	public static final String XML_SENTIMENT_TAG = "sentence";
	public static final String XML_SENTIMENT_ATTRIBUTE = "sentimentValue";


	public static final String VERY_POSITIVE = "Very positive";
	public static final String POSITIVE = "Positive";
	public static final String NEUTRAL = "Neutral";
	public static final String NEGATIVE = "Negative";
	public static final String VERY_NEGATIVE = "Very negative";

	public static final int VERY_POSITIVE_INT_VALUE = 4;
	public static final int POSITIVE_INT_VALUE = 3;
	public static final int NEUTRAL_INT_VALUE = 2;
	public static final int NEGATIVE_INT_VALUE = 1;
	public static final int VERY_NEGATIVE_INT_VALUE = 0;

	protected static final String [] SENTIMENT_VALUE_MAP = {Classifier.NEGATIVE_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,Classifier.NEUTRAL_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE};

	/**
	 * the path of file to read to make predictions
	 * if the pre-genearted predictions file doesn't exist
	 */
	private String inputTestDatasetPath;

	/**
	 * file path to pre-generated nlp-based predictions.
	 * if the file doesn't exist, it will be created
	 * to avoid re-predicting the same dataset
	 */
	//private String nlpBasedPredictionsFile;


	protected boolean includingNeutralSentiments;
	private Random random;

	public NLPBasedClassifier(String name, String inputTestDatasetPath,boolean includingNeutralSentiments) {
		super(name);
		init(name,inputTestDatasetPath,includingNeutralSentiments);
	}


	/**
	 * empty constructor that can be used by subclasses.
	 * Subclasses need to make sure to initialize this object, however.
	 */
	protected NLPBasedClassifier(){
		super(null);
	}

	protected void init(String name, String inputTestDatasetPath,boolean includingNeutralSentiments) {
		this.name=name;
		this.inputTestDatasetPath = inputTestDatasetPath;
		this.includingNeutralSentiments=includingNeutralSentiments;
		random = new Random();



	}

	public void setInputTestDatasetPath(String file){
		inputTestDatasetPath = file;
	}
	
	public String getInputTestDatasetPath(){
		return inputTestDatasetPath;
	}
	/*
	public void setNLPBasedPredictionsFile(String file){
		//nlpBasedPredictionsFile = file;
	}
	
	public String getNLPBasedPredictionsFile(){
		//return nlpBasedPredictionsFile;
		return null;
	}*/
	
	//note that this modeled isn't trained, only classified (unsupervised)
	public void trainAndClassify(){

		int numTweets=-1;
		try {
			numTweets = FileHandler.countLines(inputTestDatasetPath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//log 10% intervals of completion
		ProgressLogger progress = new ProgressLogger(0.05,numTweets);
		//	List<Label> realLabels = loadRealLabels(new File(inputTestDatasetPath).toPath());
		Logger log = LoggerFactory.getInstance();

		log.log_debug("running NLPBased classifier and reading dataset file: "+inputTestDatasetPath);
		BufferedReader reader;

		
		try{
			//only create file appender if the pre-generated predictions
			//file doesn't exist. In case file exists, we will read from it instead of
			//making our predictions. If it doens't exist, create it.
			//this way we don't have to re-predict the same dataset over
			//many experiments
			
		//	File predictionOutputFile=null;
	/*		
			if(nlpBasedPredictionsFile != null){
				predictionOutputFile= new File(nlpBasedPredictionsFile);
			}
		*//*	
			//the prediction file doesn't exist, or we are not using prediction file? we will create it
			if(nlpBasedPredictionsFile ==null || !predictionOutputFile.exists()){
				log.log_info("writing prediction of NLP-Based approach to: "+nlpBasedPredictionsFile);

				FileAppender predictionsOutput = null;
				
				if(nlpBasedPredictionsFile!=null){
					predictionOutputFile.createNewFile();
					predictionsOutput = new FileAppender(nlpBasedPredictionsFile);
					predictionsOutput.open();
				}*/
				//reader = new BufferedReader(new FileReader(
				//	inputTestDatasetPath));
				reader = new BufferedReader(new InputStreamReader(new  FileInputStream(inputTestDatasetPath), Charset.forName("UTF-8")));
				String line = reader.readLine();
				while (line != null) {
					//split the line by white spaces 
					//String[] tokens= line.split("\\s+");

					//first element is the real tag/label of line (then a tab)
					//the rest is the cleaned data 

					int tabIx = line.indexOf(TweetParser.TAB);

					String realLabelValue = line.substring(0,tabIx);				
					String data = line.substring(tabIx,line.length());

					Label  realLabel = new Label(realLabelValue);
					Label prediction = this.classifySentiment(data);

/*
					if(nlpBasedPredictionsFile!=null){
						//output the real value and  prediction file the prediction
						String output = prediction.getValue()+TweetParser.COMMA+realLabel.getValue()+TweetParser.NEW_LINE;
						predictionsOutput.append(output);
					}*/
					LabelPair p = new LabelPair(realLabel,prediction);
					this.predictions.add(p);



					// read next line
					line = reader.readLine();
					//	progress.logProgress("classifying tweet sentiment using NLP-based method");
				}
				/*
				if(nlpBasedPredictionsFile!=null){
					predictionsOutput.flush();
					predictionsOutput.close();
				}*/
/*
			}else{//else, were reading from pregenerate file
				log.log_info("reading from prediction file  of NLP-Based approach: "+nlpBasedPredictionsFile);
				reader = new BufferedReader(new InputStreamReader(new  FileInputStream(nlpBasedPredictionsFile), Charset.forName("UTF-8")));
				String line = reader.readLine();
				while (line != null) {

					//prediction,realtag
					//first element is the predicted tag
					//,
					//the 2nd element is the real tag/label

					String realLabelValue = line.charAt(2)+"";
					String predictionValue = line.charAt(0)+"";

					Label  realLabel = new Label(realLabelValue);
					Label prediction = new Label(predictionValue);

					LabelPair p = new LabelPair(realLabel,prediction);
					this.predictions.add(p);



					// read next line
					line = reader.readLine();
					//	progress.logProgress("classifying tweet sentiment using NLP-based method");
				}
				
			//	predictionsOutput.flush();
				//predictionsOutput.close();

			} 
		*/	
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		log.log_debug("finished NLPBased classifier");
	}

	/**
	 * converts the string-represented sentiment to its integer equivalent value
	 * @param sentiment string-represented sentiment
	 * @return the integer value of the sentiment
	 */
	public int sentimentValueToInt(String sentiment){
		if(sentiment.equals(POSITIVE)){
			return POSITIVE_INT_VALUE;
		}else if(sentiment.equals(VERY_POSITIVE)){
			return VERY_POSITIVE_INT_VALUE;
		}else if(sentiment.equals(VERY_NEGATIVE)){
			return VERY_NEGATIVE_INT_VALUE;
		}else if(sentiment.equals(NEUTRAL)){
			return NEUTRAL_INT_VALUE;
		}else if(sentiment.contains(NEGATIVE)){
			return NEGATIVE_INT_VALUE;
		}else{
			throw new IllegalArgumentException("unknown sentiment ("+sentiment+"), could not convert to int.");
		}
	}

	/**
	 * Classifies data into sentiment
	 * @param string the data to classify
	 * @return the sentiment of the text
	 */
	public Label classifySentiment(String string){

		List<String> sentiments = analyzeSentiment(string);

		double sentimentSum = 0;

		for(String sentiment : sentiments){
			sentimentSum+=sentimentValueToInt(sentiment);
		}

		double avgSentiment = (sentimentSum)/((double)sentiments.size());



		int intSentimentIx = roundSentimentRealValueToNearestInt(avgSentiment);


		return new Label(SENTIMENT_VALUE_MAP[intSentimentIx]);

	}

	/**
	 * rounds the sentiment real value to nearest sentiment-integer
	 * excluding or including neutral sentiments
	 * @param sentimentValue the real value to be interpreted as a sentiment value
	 * @return rounded sentiment value
	 */
	public int roundSentimentRealValueToNearestInt(double sentimentValue){
		if(!includingNeutralSentiments){

			//case: closer to negative values
			if(sentimentValue < NEUTRAL_INT_VALUE){

				//make sure avg sentiment is closest to very neg or neg depending
				//on its value, casue were going to round it to nearest int
				//e.g.: 1.9 should be ~1 and 0.4 should be ~ 0, and 1.4 ~= 1 and 0.7 ~= 1
				if(sentimentValue >=  NEGATIVE_INT_VALUE){
					sentimentValue=NEGATIVE_INT_VALUE;
				}
			}else if (sentimentValue > NEUTRAL_INT_VALUE){//case closer to positive valuess


				//make sure avg sentiment is closest to very positive or positive depending
				//on its value, casue were going to round it to nearest int
				//e.g.: 2.9 should be ~3 and 3.6 should be ~ 4, and 2.6 ~= 3 and 3.7 ~= 4
				if(sentimentValue <=  POSITIVE_INT_VALUE){
					sentimentValue=POSITIVE_INT_VALUE;
				}

			}else{// nerutral tweet, so randomly assign to either positive or negative
				//int max = 1;
				//int min = 0;
				//random int between 0 and 1
				//int randomInt = random.nextInt((max - min) + 1) + min; = t((1) + 1) + 0; = (2)
				int randomInt = random.nextInt(2);
				if(randomInt == 0){
					sentimentValue =NEGATIVE_INT_VALUE;
				}else{
					sentimentValue =POSITIVE_INT_VALUE;
				}

			}

		}//end not considering neutral tweets?

		//round average sentiment to nearest int to 
		return(int) Math.round(sentimentValue);
	}

	/** Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]] */
	/**
	 * returns list of sentiments found in setence
	 * @param text
	 * @return
	 * @throws IOException
	 */
	protected List<String> analyzeSentiment(String text) {

		//remove non-ascii from the string (its replaced (very slowly) by standorf lib anayway)
		//example non ascii: é
		text = text.replaceAll("[^\\p{ASCII}]", "");

		List<String> sentiments = new ArrayList<String>();

		// Initialize an Annotation with some text to be annotated.
		Annotation annotation;
		annotation = new Annotation(text);
		//ByteArrayOutputStream out = new ByteArrayOutputStream();

		// run all the selected Annotators on this text
		StanfordCoreNLP pipeline = StanfordPipelineFactory.getPipeline();

		pipeline.annotate(annotation);


		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		if (sentences != null && ! sentences.isEmpty()) {
			for(CoreMap sentence : sentences){

				sentiments.add(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
			}
		}
		return sentiments;

	}


}
