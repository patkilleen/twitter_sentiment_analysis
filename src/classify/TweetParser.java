package classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.util.Util;
import core.Main;
import io.FileAppender;
import io.FileHandler;
import io.IConfig;
import io.log.Logger;
import io.log.LoggerFactory;

public class TweetParser {

	public enum DatasetParsingFormat{STANFORD,AIRLINE,DATASET3}

	/*
	 * STANFORD DATSET CONSTANTS
	 */
	public static final int STANFORD_DATASET_FORMAT_TWEET_DATA_TOKEN_START_IX = 5;
	public static final int STANFORD_DATASET_FORMAT_TWEET_POLARITY_TOKEN_IX = 0;
	private static final String STANFORD_SVM_POSITIVE_LABEL = "+1";
	private static final String STANFORD_SVM_NEGATIVE_LABEL = "-1";
	
	/*
	 * AIRELINE DATASET CONSTANTS
	 */
	public static final int AIRLINE_DATASET_FORMAT_TWEET_DATA_TOKEN_START_IX = 10;

	private static final String AIR_LINE_POSITIVE_LABEL = "positive";
	private static final String AIR_LINE_NEGATIVE_LABEL = "negative";
	private static final String AIR_LINE_NEUTRAL_LABEL = "neutral";
	
	/*
	 * DATASET 3 CONSTANTS
	 */
	private static final char DATASET3_POSITIVE_LABEL = '1';
	private static final char DATASET3_NEGATIVE_LABEL = '0';
	
	public static final String WHITE_SPACE = " ";
	public static final String USERNAME_TEMPLATE = "(USERNAME)";
	public static final String URL_TEMPLATE = "URL";
	public static final String WHITE_SPACE_REGEX = "\\s+";
	public static final String USER_REFERENCE_SYMBOLE = "@";
	public static final String NEGATIVE_EMOTICON_TEMPLATE = "frown";
	public static final String POSITIVE_EMOTICON_TEMPLATE = "smile";
	public static final String EMPTY_STRING = "";
	public static final String TAB = "\t";
	public static final String NEW_LINE = "\r\n";
	public static final String COMMA = ",";
	public static final String PERIOD_REGEX = "\\.";
	public static final String PERIOD = ".";
	public static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";


	protected List<String> stopwords;
	protected List<String> positiveEmoticons;
	protected List<String> negativeEmoticons;

	protected DatasetParsingFormat datasetParsingFormat;

	private Pattern urlPattern;
	private Pattern airLinePattern;
	protected boolean removingSymbolsFlag;
	/**
	 * 	 Constructor.
	 * 
	 * @param stopwords list of stop words to remove from the tweet 
	 * @param rawDatasetPath the path to raw tweet dataset
	 * @param cleanedDatasetPath the path to output the re-formated tweets to
	 * @param datasetParsingFormatStr id of dataset that we are parsing. Depending one what dataset it is, different parsing logic will take place.
	 * @param positiveEmoticons list of positive emoticons that will be removed from tweets
	 * @param negativeEmoticons list of negative emoticons that will be removed from tweets
	 */
	public TweetParser(List<String> stopwords,List<String> positiveEmoticons, List<String> negativeEmoticons,String datasetParsingFormatStr,boolean removingSymbolsFlag) {
		init(stopwords, positiveEmoticons, negativeEmoticons, datasetParsingFormatStr,removingSymbolsFlag);
	}

	/**
	 * Empty constructor.
	 * Subclasses are responsible for initializing instances of
	 * this class that are built using this constructor.
	 */
	protected TweetParser(){
		//initialize the URL pattern
		urlPattern = Pattern.compile(URL_REGEX);
	}

	/**
	 * Initializes the attributes of 
	 * @param stopwords list of stop words to remove from the tweet 
	 * @param datasetParsingFormat id of dataset that we are parsing. Depending one what dataset it is, different parsing logic will take place.
	 * @param positiveEmoticons list of positive emoticons that will be removed from tweets
	 * @param negativeEmoticons list of negative emoticons that will be removed from tweets
	 */
	protected void init(List<String> stopwords,List<String> positiveEmoticons, List<String> negativeEmoticons,String datasetParsingFormatStr,boolean removingSymbolsFlag) {


		this.stopwords = stopwords;
		this.positiveEmoticons=positiveEmoticons;
		this.negativeEmoticons=negativeEmoticons;
		this.removingSymbolsFlag=removingSymbolsFlag;
		if(Util.isNullOrEmpty(stopwords)){
			Logger log = LoggerFactory.getInstance();
			log.log_warning("empty stopword list");
			//create empty list, so iteratign the list won't create null pointer exception
			if(stopwords == null){
				this.stopwords = new ArrayList<String>(0);
			}
		}

		if(Util.isNullOrEmpty(positiveEmoticons)){
			Logger log = LoggerFactory.getInstance();
			log.log_warning("empty positive emoticon list");
			//create empty list, so iteratign the list won't create null pointer exception
			if(positiveEmoticons == null){
				this.positiveEmoticons = new ArrayList<String>(0);
			}
		}

		if(Util.isNullOrEmpty(negativeEmoticons)){
			Logger log = LoggerFactory.getInstance();
			log.log_warning("empty negative emoticon list");
			//create empty list, so iteratign the list won't create null pointer exception
			if(negativeEmoticons == null){
				this.negativeEmoticons = new ArrayList<String>(0);
			}
		}

		//convert the string value of parsing format to enum
		this.datasetParsingFormat=DatasetParsingFormat.valueOf(datasetParsingFormatStr);


		//initialize the URL pattern
		urlPattern = Pattern.compile(URL_REGEX);
		airLinePattern = Pattern.compile("^\\d+,");
	}

	/**
	 * Will read a stop word file line by line and return a list of stop words.
	 * The stopwords in the file are expected not to have spaces, since each
	 * line represents a word.
	 * @param stopWordPath Path to stop word file
	 * @return list of stopwords (empty if IOException occured)
	 */
	public static List<String> loadStopwords(String stopWordPath){

		if(stopWordPath == null){
			return new ArrayList<String>(0);
		}

		List<String> stopwords = new ArrayList<String>(1024*32);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(
					stopWordPath.toString()));
			String line = reader.readLine();

			//read each stop word 
			while (line != null) {

				//add stop word to list
				stopwords.add(line);
				// read next line
				line = reader.readLine();



			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stopwords;
	}


	/**
	 * Converts a raw twitter dataset to a format require by StanfordNLP lib.
	 * That is, it reads the raw dataset (<code>rawDatasetPath</code>) line by line
	 * and normlizes each tweet found within. It then writes the normalized tweet
	 * to the <code>cleanedDatasetPath</code> file
	 * @param rawDatasetPath the path to raw tweet dataset
	 * @param cleanedDatasetPath the path to output the re-formated tweets to
	 * @throws IOException thrown when failed to read rawdataset or append to clean dataset file 
	 */
	public void normalizeTweetDatasetFile(Path rawDatasetPath,Path cleanedDatasetPath) throws IOException {
		Util.verifyPath(rawDatasetPath);
		Util.verifyPath(cleanedDatasetPath);
		BufferedReader reader=null;
		FileAppender fileIO = null;
		try {

			//dont create the file if it already exists
			if(!cleanedDatasetPath.toFile().exists()){
				FileHandler.createFile(cleanedDatasetPath);
			}
			fileIO = new FileAppender(cleanedDatasetPath,Main.FILE_BUFFER_SIZE);
			fileIO.open();
			reader = new BufferedReader(new FileReader(
					rawDatasetPath.toString()));
			String line = reader.readLine();

			//String outputBuffer = "";
			//normalize tweets line by line 
			while (line != null) {

				String normTweet = normalizeTweet(line);

				//not an empty tweet after normalization?
				if(normTweet!=null){
					normTweet +=  NEW_LINE;

					//append normalized tweet to target file
					//FileHandler.append(cleanedDatasetPath, normTweet.getBytes());
					fileIO.append(normTweet);
				}
				// read next line
				line = reader.readLine();

			}
			fileIO.flush();

		}finally{
			if(reader!=null){
				reader.close();
			}
			if(fileIO!=null){
				fileIO.close();
			}

		}

	}

	/**
	 * Parses and normalizes a tweet based on the dataset in question. 
	 * The normalized tweet is returned
	 * @param line the tweet to parse and normalize
	 * @return the normalized tweet, or null if the tweet data is empty after normalization
	 */
	public String normalizeTweet(String line) {

		if(line == null){
			return null;
		}

		String res = null;
		String tweetData = "";
		String tweetPolarityLabel = null;
		String [] tokens = null;
		
		int firstCommaIx = -1;
		int secondCommaIx = -1;
		//CHECK FORMAT OF DATASET TO PARSE
		switch(datasetParsingFormat){


		case STANFORD:


			//extract the tweet's data, and the polarity

			//exmpale tweet format:
			//0         1  2    3            4  5
			//"sentiment",id,date,"NO_QUERY",user,"the tweet data "

			tokens = line.split(COMMA);

			//tweet data may have commas, so we need to build the tweet data
			//by appending all concecutive tokens from the 5th token obtained
			//by splitting the data by comma
			for(int i = STANFORD_DATASET_FORMAT_TWEET_DATA_TOKEN_START_IX; i < tokens.length;i++){
				tweetData+=tokens[i];
				//add the comma that existed before, but only if its not the last token
				if(i < (tokens.length-1)){
					tweetData+=COMMA;
				}

			}
			tweetData = removeContainingQuotes(tweetData);	
			tweetPolarityLabel = removeContainingQuotes(tokens[STANFORD_DATASET_FORMAT_TWEET_POLARITY_TOKEN_IX]);
			break;

		case AIRLINE:

			if(line.isEmpty()){
				return null;
			}
			
			
			int commaCount = 0;
			//count the commas in line, if its not the minimum (number of colums in csv) skip
			for(int i = 0;i < line.length();i++){
				if(line.charAt(i)==','){
					commaCount++;
				}
			}
			
			if(commaCount < 15){
				return null;
			}
			/*Matcher matcher = airLinePattern.matcher(line);
			
			//ignore broken lines (lines that don't start with  a number then comma)
			if(!matcher.matches()){
				return null;
			}*/
		
			//extract the tweet's data, and the polarity
			tokens = line.split(COMMA);

			//exmpale tweet format:
			//tweet_id,airline_sentiment,airline_sentiment_confidence,negativereason,negativereason_confidence,airline,airline_sentiment_gold,name,negativereason_gold,retweet_count,"text",tweet_coord,tweet_created,tweet_location,user_timezone
			//0         1                   2  ............              3                  4                              5
			//0,SENTIMENT,1,2,3,4,5,6,7,8,9,"TEXT with commas, and doble "" quotes",11,12...
			//tokens = line.split(COMMA);

			firstCommaIx = line.indexOf(',');
			secondCommaIx = line.indexOf(',',firstCommaIx+1);

			//extract sentiment 
			tweetPolarityLabel = line.substring(firstCommaIx+1,secondCommaIx);

			//convert airline sentiment to standard sentiment value
			if(tweetPolarityLabel.equals(AIR_LINE_POSITIVE_LABEL)){
				tweetPolarityLabel = Classifier.POSITIVE_LABEL_VALUE;
			}else if(tweetPolarityLabel.equals(AIR_LINE_NEGATIVE_LABEL)){
				tweetPolarityLabel = Classifier.NEGATIVE_LABEL_VALUE;
			}else if(tweetPolarityLabel.equals(AIR_LINE_NEUTRAL_LABEL)){
				tweetPolarityLabel = Classifier.NEUTRAL_LABEL_VALUE;
			}else{
				Logger log = LoggerFactory.getInstance();
				log.log_error("could not find sentiment assosiated to :"+tweetPolarityLabel+", in dataset aireline");
			}


			if(AIRLINE_DATASET_FORMAT_TWEET_DATA_TOKEN_START_IX >= tokens.length){
				//Logger log= LoggerFactory.getInstance();
			//	log.log_error("error parsing tweet: "+line);
				return null;
			}
			tweetData = tokens[AIRLINE_DATASET_FORMAT_TWEET_DATA_TOKEN_START_IX];

			//case where there are quotes to deal with? otherwise tweet data is parsed 
			if(tweetData.startsWith("\"")){

				int tweetStartIx = line.indexOf('"')+1;//+1 1 since we don't want to include the beging quote
				int tweetEndIx = -1;
				boolean endTweetData = false;
				int i = tweetStartIx;
				//iterate the tweet and find the ending quote
				do{

					//found a double quote?
					if(line.charAt(i)=='"'){

						//check for next cahracter, if its a user-quote (not end of tweet data)
						if(line.charAt(i+1)=='"'){
							//we found a user quote (a double quote pair)
							//so skip next character
							i++;
						}else if(line.charAt(i+1)==','){
							endTweetData=true;
							tweetEndIx= i-1;//-1 since we don't want to include the end quote
						}
					}
					i++;
				}while(!endTweetData && (i <line.length()));

				//we excceeded the tweet?
				if(!endTweetData){
					//error at line
					//Logger log= LoggerFactory.getInstance();
					//log.log_error("error parsing tweet: "+line);
					return null;
				}else{
					tweetData=line.substring(tweetStartIx,tweetEndIx);
				}
			}
			/*
			 * SOMETIMES TWEET DATA has not quotes at all
			 * ,data,
				//text can have commas and '"', but the " are "", so any single ", means ends text
				//tweet data may have commas, so we need to build the tweet data
				//by appending all concecutive tokens from the 5th token obtained
				//by splitting the data by comma
				for(int i = STANFORD_DATASET_FORMAT_TWEET_DATA_TOKEN_START_IX; i < tokens.length;i++){
					tweetData+=tokens[i];
					//add the comma that existed before, but only if its not the last token
					if(i < (tokens.length-1)){
						tweetData+=COMMA;
					}

				}*/
			//public static final int AIRLINE_DATASET_FORMAT__TWEET_DATA_TOKEN_START_IX = 10;
			//public static final int AIRLINE_DATASET_FORMAT_TWEET_POLARITY_TOKEN_IX = 1;
			break;
		case DATASET3:
			//ItemID,Sentiment,SentimentText
			
			firstCommaIx = line.indexOf(',');
			secondCommaIx = line.indexOf(',',firstCommaIx+1);

			//extract sentiment (either 0 or 1) 
			tweetPolarityLabel = line.substring(firstCommaIx+1,secondCommaIx);
			
			//check only first character since the sentiment is only 0 or 1
			if(tweetPolarityLabel.charAt(0)==DATASET3_POSITIVE_LABEL){
				tweetPolarityLabel = Classifier.POSITIVE_LABEL_VALUE;
			}else if(tweetPolarityLabel.charAt(0)==DATASET3_NEGATIVE_LABEL){
				//tweetPolarityLabel = Classifier.NEGATIVE_LABEL_VALUE;
				//DO NOTHING, already the proper label
			}else{
				Logger log = LoggerFactory.getInstance();
				log.log_error("unknown sentiment : "+tweetPolarityLabel);
			}
			
			//extract the sentiment text
			tweetData=line.substring(secondCommaIx+1);
			//remove the " on extremities if they exist
			if(tweetData.charAt(0)=='"' && tweetData.charAt(tweetData.length()-1)=='"'){
				tweetData=removeContainingQuotes(tweetData);
			}
			
			//therre is a lot of white space in the tweets, replace it with single spaces
			tweetData= tweetData.replace(TweetParser.WHITE_SPACE_REGEX, TweetParser.WHITE_SPACE);
			break;
		default:
			Logger log = LoggerFactory.getInstance();
			log.log_error("unknown dataset formatting id: "+datasetParsingFormat);
			return null;

		}

		res=tweetData;

		res = replaceURLs(res);
		res = replaceUserPointers(res);
		res = applyGrammarCorrection(res);
		res = replaceEmoticons(res);
		res = removeStopWords(res);
		
		if(removingSymbolsFlag){
			res = removeSymboles(res);	
		}
		//don't bother will empty tweets after normalization
		if(res.isEmpty()){
			return null;
		}

		res = tweetPolarityLabel+TAB+res;

		
		return res;
	}

	/**
	 * Removes symbols from a tweet (only digits (0-9) and letters(a-z and A-Z) are unfiltered).
	 * tthe parenthesis remain however
	 * @param tweet tweet to remove symboles from
	 * @return tweet with  no symobles other than paranthese ('(' and ')')
	 */
	protected static String removeSymboles(String tweet){
		
		String res = "";
		//iterate all characters to remove special characters (other than '(' and 
		//')' since they are used in the templates replacing USERNAMES)
		for(int i = 0;i<tweet.length();i++){
			
			char c = tweet.charAt(i);
			//only add non-symbole characters to result (except '(' and ')')
			//and white spaces too
			if ((c == '(') || (c == ')') || (c == ' ') || String.valueOf(c).matches("[a-zA-Z0-9]")) {
				res+=c;
			}
		}
		
		return res;
	}
	/**
	 * Removes dots ('.') from a string.
	 * @param tweet the string to remove '.'s from 
	 * @return the string that has its '.'s removed
	 */
	protected static String removeDots(String tweet){

		return removeCharacter(tweet,'.');
	}

	/**
	 * Removes dots ('.') from a string.
	 * @param tweet the string to remove '.'s from 
	 * @return the string that has its '.'s removed
	 */
	protected static String removeCharacter(String tweet, char removalTarget){

		if(tweet == null){
			return null;
		}

		String res = "";
		//remove periods ('.') since the NLPbased approach will use periods to distinguish tweets
		for(int i = 0;i<tweet.length();i++){
			char c = tweet.charAt(i);
			//only append non 'dot' characters to results
			if(c != removalTarget){
				res+=c;
			}
		}

		return res;
	}


	/**
	 * Removes the double quotes containing a string.
	 * For example the string '"my string"' would become 'my string'
	 * @param str string to remove containing quotes from
	 * @return the string witout quotes in front and at end
	 */
	protected static String removeContainingQuotes(String str){
		if(str == null){
			return null;
		}

		str = str.trim();
		//remove the first quote
		str = str.substring(1);
		//remove last quote
		str = str.substring(0,str.length()-1);

		return str;
	}


	/**
	 * Removes stopwords from a tweet.
	 * @param tweet the tweet to remove stopwords from
	 * @return stopword-free tweet
	 */
	public String removeStopWords(String tweet) {

		if(tweet == null){
			return null;
		}

		String res = "";

		//iterate each word in the tweet
		String [] words = tweet.split(WHITE_SPACE_REGEX);

		for(String word : words){
			//only add word to result if not a stop word?
			if(!stopwords.contains(word)){
				res+=word;
				res+=TweetParser.WHITE_SPACE;
			}

		}

		return res.trim();
	}

	/**
	 * Replaces all the emoticons in a tweet with constants.
	 * Positive emoticons are replaced with "smile", and
	 * negative emoticons are replaced with "frown".
	 * @param tweet tweet to remove emoticons from
	 * @return emoticon-free tweet with no trailing white spaces
	 */
	public String replaceEmoticons(String tweet) {

		if(tweet == null){
			return null;
		}

		String res = "";

		//iterate each word in the tweet
		String [] words = tweet.split(WHITE_SPACE_REGEX);

		for(String word : words){
			//not positive emoticon?
			if(!positiveEmoticons.contains(word)){
				//not a negative emoticon?
				if(!negativeEmoticons.contains(word)){
					//we can safely include the word, its neither a positive emoticon nor a negative emoticon
					res+=word;

				}else{
					//replace emoticon with template
					res+=TweetParser.NEGATIVE_EMOTICON_TEMPLATE;
				}//end if :-(
			}else{
				//replace emoticon with template
				res+=TweetParser.POSITIVE_EMOTICON_TEMPLATE;
			}//end if :-)

			res+=TweetParser.WHITE_SPACE;
		}

		return res.trim();
	}

	/**
	 * Makes words with triple + consecutive letters down to only 2 letters.
	 * For example, 'goooooooood' would be replaced with 'good'
	 * It will not be smart enough to handle grammar mistakes such as:
	 * 'baaad' since it will reduce it to 'baad', assuming that 
	 * many occurrences of a reapeted characters indicates the
	 * correction is to leave only 2 characters.  
	 * @param tweet the tweet to fix most grammar mistakes
	 * @return the grammar-mistake free tweet with trimmed white space
	 */
	protected static String applyGrammarCorrection(String tweet) {

		if(tweet == null){
			return null;
		}

		String resTweet = "";

		//iterate each word in the tweet
		String [] words = tweet.split(WHITE_SPACE_REGEX);
		int i = 0;
		for(String word : words){

			//we can skip words of 2 characters or fewer
			if(word.length() > 2){
				String newword = "";
				//there shoul be no empty/white-spcace character since words parsed using whitespace
				//therefore, white space is a good 'null' value
				char oldC = ' ';
				int charCount = 1;

				//iterate characters in the word
				for(int cix = 0;cix < word.length();cix++){
					char c = word.charAt(cix);

					if(c == oldC){//concecutive character?
						charCount++;
					}else{
						charCount=1;//nope, reset coutner
					}
					//don't append characters that occur too frequently
					if(charCount<3){
						newword+=c;
					}

					oldC = c;
				}//end iterate charaters of word
				resTweet+=(newword);
			}else{
				resTweet+=(word);
			}

			//only append a white space if not the last word
			if(i < (words.length -1)){
				resTweet+=(WHITE_SPACE);
			}
			i++;
		}//end iterate all words
		return resTweet.trim();


	}

	/**
	 * Removes addresses (for example, URLs) from a tweet and replaces it with the constant 'URL'
	 * @param tweet tweet to remove URLS from 
	 * @return URL-free tweet
	 */
	protected String replaceURLs(String tweet) {

		if(tweet == null){
			return null;
		}

		String resTweet = "";

		//iterate each word
		String [] words = tweet.split(WHITE_SPACE_REGEX);
		for(String word : words){
			resTweet+=WHITE_SPACE;
			if(isURL(word)){
				resTweet+=URL_TEMPLATE;
			}else{
				resTweet+=word;
			}
		}
		return resTweet.trim();
	}

	/**
	 * Returns true when the provided string is a URL, and false otherwise
	 * @param word target word
	 * @return true when the provided string is a URL, and false otherwise
	 */
	protected boolean isURL(String word) {

		if(word==null){
			return false;
		}

		try {

			Matcher matcher = urlPattern.matcher(word);
			return matcher.matches();
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Replaces all Twitter user references with the constant "(USERNAME)"
	 * 
	 * For example, the sentence "helloworld from user \@user123" would become "helloworld from user (USERNAME)"
	 * @param tweet the target tweet
	 * @return tweet with replaced user pointers
	 */
	protected static String replaceUserPointers(String tweet) {

		if(tweet == null){
			return null;
		}
		String resTweet = "";
		String [] words = tweet.split(WHITE_SPACE_REGEX);
		//iterate all words in tweet
		for(String word : words){
			if(word.startsWith(USER_REFERENCE_SYMBOLE) && word.length() > 1){// is tagging user?
				resTweet+=USERNAME_TEMPLATE;
			}else{

				resTweet+=word;
			}
			resTweet+=TweetParser.WHITE_SPACE;
		}
		return resTweet.trim();
	}


	public static void cleanDatasets(IConfig config) throws IOException {

		/*
		 * this function will read the raw twitter dataset (test tweets and training tweets)
		 * perform the normlizion steps below on them, and then convert the normalized tweets
		 * to training, dev, and test file for stanford nlp core lib to handle (i.e., in
		 * following format: "label			features separated by spaces")
		 * 
		 */


		//input datasets of raw twitter data
		Path rawtestDataPath = (new File(config.getProperty(IConfig.PROPERTY_RAW_TESTING_DATA_INPUT_FILE)).toPath());//dont verify test path, since no test data may exist
		Path rawdevDatasetPath = (new File(config.getProperty(IConfig.PROPERTY_RAW_DEV_DATA_INPUT_FILE)).toPath());//dont verify dev path, since no test data may exist
		Path rawtrainDatasetPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_RAW_TRAIN_DATA_INPUT_FILE);


		//test data file to output to
		Path testDataPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLEANED_TESTING_DATA_FILE);	
		//dev data file to output to
		Path devDatasetPath =Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLEANED_DEV_DATA_FILE);
		//TRAIN data file to output to
		Path trainDatasetPath = Util.parseAndVerifyPath(config,IConfig.PROPERTY_CLEANED_TRAINING_DATA_FILE);

		//stopword file path
		String stopWordPathStr = config.getProperty(IConfig.PROPERTY_STOP_WORD_INPUT_FILE);
		Path stopWordPath = null;
		List<String> stopwords;
		if(stopWordPathStr != null){
			stopWordPath =Util.parseAndVerifyPath(config,IConfig.PROPERTY_STOP_WORD_INPUT_FILE);
			stopwords= TweetParser.loadStopwords(stopWordPath.toString());
		}else{
			stopwords= TweetParser.loadStopwords(stopWordPathStr);
		}
		 

		//get id of dataset that were are working with.
		String datasetParsingFormatId = config.getProperty(IConfig.PROPERTY_DATASET_PARSING_FORMAT_ID);

		List<String> posEmoticons = config.getProperties(IConfig.PROPERTY_POSITIVE_EMOTICON_LIST);
		List<String> negEmoticons = config.getProperties(IConfig.PROPERTY_NEGATIVE_EMOTICON_LIST);

		boolean removeSymbolesFlag = config.getBooleanProperty(IConfig.PROPERTY_REMOVING_SYMBOLES_FLAG);
		TweetParser tweetParser = new TweetParser(stopwords,posEmoticons,negEmoticons,datasetParsingFormatId,removeSymbolesFlag);

		Logger log = LoggerFactory.getInstance();
		log.log_info("normalizing tweets...");

		//don't reclean the files if already done
		if(!testDataPath.toFile().exists()){
			//don't normalize a file that doesnt exist
			if(Util.isValidFilePath(rawtestDataPath.toString())){
				tweetParser.normalizeTweetDatasetFile(rawtestDataPath,testDataPath);
			}else{
				log.log_info(""+rawtestDataPath.toString()+" doesn't exists, skipping the normalization since we will be generating the test data from traiing data");
			}
		}else{
			log.log_info("clean dataset "+testDataPath.toString()+" already exists, skipping the normalization of "+rawtestDataPath.toString());
		}

		if(!devDatasetPath.toFile().exists()){
			//don't normalize a file that doesnt exist
			if(Util.isValidFilePath(rawdevDatasetPath.toString())){
				tweetParser.normalizeTweetDatasetFile(rawdevDatasetPath,devDatasetPath);
			}else{
				log.log_info(""+rawdevDatasetPath.toString()+" doesn't exists, skipping the normalization since we will be generating the dev data from traiing data");
			}
		}else{
			log.log_info("clean dataset "+devDatasetPath.toString()+" already exists, skipping the normalization of "+rawdevDatasetPath.toString());
		}

		if(!trainDatasetPath.toFile().exists()){
			tweetParser.normalizeTweetDatasetFile(rawtrainDatasetPath,trainDatasetPath);
		}else{
			log.log_info("clean dataset "+trainDatasetPath.toString()+" already exists, skipping the normalization of "+rawtrainDatasetPath.toString());
		}

		log.log_info("...finished normalizing tweets");
	}

}
