package test.classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Test;

import classify.Classifier;
import classify.Label;
import classify.LabelPair;
import classify.NLPBasedClassifier;
import classify.TweetParser;
import io.FileAppender;
import io.FileHandler;
import junit.framework.Assert;

public class TestNLPBased extends NLPBasedClassifier{

	final public static String [] SENTENCES = {"i am happy. ",
			"i love this song. ",
			"i find this song okay. ",
			"i hate this song. ",
			"i am sad. ",
			"hate fuck dislike sad frown. ",
			"very good great loving positive smile. ",
			"smile. ",
	"hate hate bad sad negative."};
	final public static String TEST_DATA= SENTENCES[0]
			+ SENTENCES[1]
					+ SENTENCES[2]
							+ SENTENCES[3]
									+ SENTENCES[4]
											+ SENTENCES[5]
													+ SENTENCES[6]
															+ SENTENCES[7]
																	+ SENTENCES[8]; 

	final public static String [] EXPECTED_SENTIMENT = {"Positive","Positive","Negative","Negative","Neutral","Negative","Very positive","Positive","Very negative"};
	public TestNLPBased(){
		super();
		String name = "test";
		String inputTestDatasetPath = null;
		boolean includingNeutralSentiments = true;
		init(name,inputTestDatasetPath,includingNeutralSentiments); 
	}
	/*@Test
	public void test() throws IOException {
		//StanfordCoreNlpDemo.run(new String[0]);
		SentimentAnalyzer analyzer = new SentimentAnalyzer();
		List<String> sentences = analyzer.analyzeSentiment("hello World, I am happy. hello I am sad. This is a neutral fact.");
		for(String s : sentences){
			System.out.println(s+"\n");
		}

		sentences = analyzer.analyzeSentiment("this is another very great sentence. this sentence suffers from sadness. This is yet another neutral fact.");
		for(String s : sentences){
			System.out.println(s+"\n");
		}
	}*/



	@Test
	public void test_AnalyzeSentiment() {


		TestNLPBased c = new TestNLPBased();
		c.includingNeutralSentiments = true;
		List<String> actualSentiments = c.analyzeSentiment(TEST_DATA);
		for(int i = 0;i<actualSentiments.size();i++){
			Assert.assertEquals(EXPECTED_SENTIMENT[i],actualSentiments.get(i));
		}
		Assert.assertEquals(EXPECTED_SENTIMENT.length,actualSentiments.size());

	}

	@Test
	public void test_classifySentiment_include_neutral_tweet() {


		TestNLPBased c = new TestNLPBased();
		c.includingNeutralSentiments = true;

		Label expected = new Label(Classifier.POSITIVE_LABEL_VALUE);
		Label actual = c.classifySentiment(SENTENCES[0]+SENTENCES[1]);//P+P
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEUTRAL_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[1]+SENTENCES[2]);//P+Neg
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEGATIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[2]+SENTENCES[3]);//Neg+Neg
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEUTRAL_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[3]+SENTENCES[4]);//Neg+Neu
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEUTRAL_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[4]+SENTENCES[5]);//Neu+Neg
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.POSITIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[5]+SENTENCES[6]);//Neu+VERYPOS
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.POSITIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[6]+SENTENCES[7]);//VERYPOS+POS
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEUTRAL_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[6]+SENTENCES[8]);//VERYPOS+VERYNEG
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEUTRAL_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[7]+SENTENCES[8]);//VERYNEG+POS
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEGATIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[2]+SENTENCES[8]);//VERYNEG+NEG
		Assert.assertEquals(expected,actual);
	}

	@Test
	public void test_classifySentiment_exclude_neutral_tweet() {

		Label pos = new Label(Classifier.POSITIVE_LABEL_VALUE);
		Label neg = new Label(Classifier.NEGATIVE_LABEL_VALUE);

		TestNLPBased c = new TestNLPBased();
		c.includingNeutralSentiments = false;

		Label expected = new Label(Classifier.POSITIVE_LABEL_VALUE);
		Label actual = c.classifySentiment(SENTENCES[0]+SENTENCES[1]);//P+P
		Assert.assertEquals(expected,actual);



		actual = c.classifySentiment(SENTENCES[1]+SENTENCES[2]);//P+Neg
		Assert.assertEquals(true,pos.equals(actual) || neg.equals(actual));//random label (would normally be neutral)

		expected = new Label(Classifier.NEGATIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[2]+SENTENCES[3]);//Neg+Neg
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEGATIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[3]+SENTENCES[4]);//Neg+Neu
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEGATIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[4]+SENTENCES[5]);//Neu+Neg
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.POSITIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[5]+SENTENCES[6]);//Neu+VERYPOS
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.POSITIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[6]+SENTENCES[7]);//VERYPOS+POS
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEUTRAL_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[6]+SENTENCES[8]);//VERYPOS+VERYNEG
		Assert.assertEquals(true,pos.equals(actual) || neg.equals(actual));//random label (would normally be neutral)

		expected = new Label(Classifier.NEGATIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[7]+SENTENCES[8]);//VERYNEG+POS
		Assert.assertEquals(expected,actual);

		expected = new Label(Classifier.NEGATIVE_LABEL_VALUE);
		actual = c.classifySentiment(SENTENCES[2]+SENTENCES[8]);//VERYNEG+NEG
		Assert.assertEquals(expected,actual);
	}



	@Test
	public void test_sentimentValueToInt() {


		TestNLPBased c = new TestNLPBased();
		c.includingNeutralSentiments = true;
		Assert.assertEquals(4,c.sentimentValueToInt("Very positive"));
		Assert.assertEquals(3,c.sentimentValueToInt("Positive"));
		Assert.assertEquals(2,c.sentimentValueToInt("Neutral"));
		Assert.assertEquals(1,c.sentimentValueToInt("Negative"));
		Assert.assertEquals(0,c.sentimentValueToInt("Very negative"));

	}

	@Test
	public void test_sentimentValueToLabel_include_neutral_tweets() {

		TestNLPBased c = new TestNLPBased();
		c.includingNeutralSentiments = true;

		Assert.assertEquals(4,c.roundSentimentRealValueToNearestInt(4.0));
		Assert.assertEquals(4,c.roundSentimentRealValueToNearestInt(3.8));
		Assert.assertEquals(4,c.roundSentimentRealValueToNearestInt(3.6));
		Assert.assertEquals(4,c.roundSentimentRealValueToNearestInt(3.5));

		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(3.2));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(3.1));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(3.0));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.9));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.7));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.6));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.5));

		Assert.assertEquals(2,c.roundSentimentRealValueToNearestInt(2.4));
		Assert.assertEquals(2,c.roundSentimentRealValueToNearestInt(2.2));
		Assert.assertEquals(2,c.roundSentimentRealValueToNearestInt(2.0));
		Assert.assertEquals(2,c.roundSentimentRealValueToNearestInt(1.9));
		Assert.assertEquals(2,c.roundSentimentRealValueToNearestInt(1.7));
		Assert.assertEquals(2,c.roundSentimentRealValueToNearestInt(1.5));

		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.4));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.2));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.0));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(0.9));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(0.7));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(0.5));

		Assert.assertEquals(0,c.roundSentimentRealValueToNearestInt(0.4));
		Assert.assertEquals(0,c.roundSentimentRealValueToNearestInt(0.2));
		Assert.assertEquals(0,c.roundSentimentRealValueToNearestInt(0.01));
		Assert.assertEquals(0,c.roundSentimentRealValueToNearestInt(0.0));

	}

	@Test
	public void test_sentimentValueToLabel_exclude_neutral_tweets() {

		TestNLPBased c = new TestNLPBased();
		c.includingNeutralSentiments = false;

		Assert.assertEquals(4,c.roundSentimentRealValueToNearestInt(4.0));
		Assert.assertEquals(4,c.roundSentimentRealValueToNearestInt(3.8));
		Assert.assertEquals(4,c.roundSentimentRealValueToNearestInt(3.6));
		Assert.assertEquals(4,c.roundSentimentRealValueToNearestInt(3.5));

		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(3.4));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(3.2));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(3.1));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(3.0));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.9));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.7));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.6));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.5));

		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.4));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.2));
		Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.1));
		//Assert.assertEquals(3,c.roundSentimentRealValueToNearestInt(2.0)); ignore this case, sinced its random
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.95));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.9));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.7));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.5));

		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.4));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.2));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(1.0));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(0.9));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(0.7));
		Assert.assertEquals(1,c.roundSentimentRealValueToNearestInt(0.5));

		Assert.assertEquals(0,c.roundSentimentRealValueToNearestInt(0.4));
		Assert.assertEquals(0,c.roundSentimentRealValueToNearestInt(0.2));
		Assert.assertEquals(0,c.roundSentimentRealValueToNearestInt(0.01));
		Assert.assertEquals(0,c.roundSentimentRealValueToNearestInt(0.0));

	}
/*
	@Test
	public void test_trainAndClassify_empty_predictions_file() throws IOException {

		TestNLPBased c = new TestNLPBased();
		c.includingNeutralSentiments=true;
		File inputDataSet = File.createTempFile("testNlpTest",null);
		File predictionDataset = File.createTempFile("testNlpTest",null);
		predictionDataset.delete();//delete so the trainAndClassify will create it
		inputDataSet.deleteOnExit();
		
		int j = 0;
		String [] dummyRealTags = {Classifier.POSITIVE_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,
				Classifier.POSITIVE_LABEL_VALUE,Classifier.NEUTRAL_LABEL_VALUE,Classifier.NEUTRAL_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,
				Classifier.POSITIVE_LABEL_VALUE,Classifier.NEUTRAL_LABEL_VALUE};
		

		//write the sentences to file
		for(int i  =0;i<SENTENCES.length;i++){
			String s = SENTENCES[i];
			String rT = dummyRealTags[i];
			String out = rT+TweetParser.TAB+s+TweetParser.NEW_LINE;
			FileHandler.append(inputDataSet.toPath(),out.getBytes());
		}

	
		 /*{"i am happy. ",
				"i love this song. ",
				"i find this song okay. ",
				"i hate this song. ",
				"i am sad. ",
				"hate fuck dislike sad frown. ",
				"very good great loving positive smile. ",
				"smile. ",
		"hate hate bad sad negative."};

		c.setInputTestDatasetPath(inputDataSet.getAbsolutePath());
		c.setNLPBasedPredictionsFile(predictionDataset.getAbsolutePath());

		c.trainAndClassify();

		//final public static String [] EXPECTED_SENTIMENT = {"Positive","Positive","Negative","Negative","Neutral","Negative","Very positive","Positive","Very negative"};
		String [] expected = {Classifier.POSITIVE_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,
				Classifier.NEUTRAL_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE};

		int i = 0;
		
			BufferedReader reader;



			reader = new BufferedReader(new InputStreamReader(new  FileInputStream(predictionDataset.getAbsoluteFile()), Charset.forName("UTF-8")));
			String line = reader.readLine();
			while (line != null) {

				
				String expectedPrediction = expected[i];
				String expcetedRealTag = dummyRealTags[i];
				
				String actualPrediction = line.charAt(0)+"";
				String actualRealTag = line.charAt(2)+"";
				Assert.assertEquals(expectedPrediction, actualPrediction);
				Assert.assertEquals(expcetedRealTag, actualRealTag);
				
				LabelPair p = c.predictions.get(i);
				Assert.assertEquals(expectedPrediction, p.getPredictedLabel().getValue());
				Assert.assertEquals(expcetedRealTag, p.getRealLabel().getValue());
				i++;
				line = reader.readLine();

			}
			
			Assert.assertEquals(SENTENCES.length,c.predictions.size());
			reader.close();


	}
	
	

	@Test
	public void test_trainAndClassify_filled_predictions_file() throws IOException {

		TestNLPBased c = new TestNLPBased();
		c.includingNeutralSentiments=true;
		File inputDataSet = File.createTempFile("testNlpTest",null);
		File predictionDataset = File.createTempFile("testNlpTest",null);
		//predictionDataset.delete();//delete so the trainAndClassify will create it
		predictionDataset.deleteOnExit();
		inputDataSet.deleteOnExit();
		
		int j = 0;
		String [] dummyRealTags = {Classifier.POSITIVE_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,
				Classifier.POSITIVE_LABEL_VALUE,Classifier.NEUTRAL_LABEL_VALUE,Classifier.NEUTRAL_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,
				Classifier.POSITIVE_LABEL_VALUE,Classifier.NEUTRAL_LABEL_VALUE};
		
		String [] expected = {Classifier.POSITIVE_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,
				Classifier.NEUTRAL_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE,Classifier.POSITIVE_LABEL_VALUE,Classifier.NEGATIVE_LABEL_VALUE};
		

		//write the sentences to file
		for(int i  =0;i<SENTENCES.length;i++){
			String s = expected[i];
			String rT = dummyRealTags[i];
			String out = s+TweetParser.COMMA+rT+TweetParser.NEW_LINE;
			FileHandler.append(predictionDataset.toPath(),out.getBytes());
		}

	
		 /*{"i am happy. ",
				"i love this song. ",
				"i find this song okay. ",
				"i hate this song. ",
				"i am sad. ",
				"hate fuck dislike sad frown. ",
				"very good great loving positive smile. ",
				"smile. ",
		"hate hate bad sad negative."};

		c.setInputTestDatasetPath(inputDataSet.getAbsolutePath());
		c.setNLPBasedPredictionsFile(predictionDataset.getAbsolutePath());

		c.trainAndClassify();

		//final public static String [] EXPECTED_SENTIMENT = {"Positive","Positive","Negative","Negative","Neutral","Negative","Very positive","Positive","Very negative"};


		int i = 0;
		
			BufferedReader reader;



			reader = new BufferedReader(new InputStreamReader(new  FileInputStream(predictionDataset.getAbsoluteFile()), Charset.forName("UTF-8")));
			String line = reader.readLine();
			while (line != null) {

				
				String expectedPrediction = expected[i];
				String expcetedRealTag = dummyRealTags[i];
				
				String actualPrediction = line.charAt(0)+"";
				String actualRealTag = line.charAt(2)+"";
				Assert.assertEquals(expectedPrediction, actualPrediction);
				Assert.assertEquals(expcetedRealTag, actualRealTag);
				
				LabelPair p = c.predictions.get(i);
				
				Assert.assertEquals(expectedPrediction, p.getPredictedLabel().getValue());
				Assert.assertEquals(expcetedRealTag, p.getRealLabel().getValue());
				i++;
				line = reader.readLine();

			}
			
			Assert.assertEquals(SENTENCES.length,c.predictions.size());
			reader.close();


	}*/
	/* This class demonstrates building and using a Stanford CoreNLP pipeline. 
	public static class StanfordCoreNlpDemo {

	  private StanfordCoreNlpDemo() { } // static meain metho

	  /** Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]] 
	  public static void run(String[] args) throws IOException {
	    // set up optional output files
	    PrintWriter out;
	    if (args.length > 1) {
	      out = new PrintWriter(args[1]);
	    } else {
	      out = new PrintWriter(System.out);
	    }
	    PrintWriter xmlOut = null;
	    if (args.length > 2) {
	      xmlOut = new PrintWriter(args[2]);
	    }

	    // Create a CoreNLP pipeline. To build the default pipeline, you can just use:
	    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    // Here's a more complex setup example:
	    //   Properties props = new Properties();
	    //   props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
	    //   props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
	    //   props.put("ner.applyNumericClassifiers", "false");
	    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    // Add in sentiment
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");

	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
	    Annotation annotation;
	    if (args.length > 0) {
	      annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
	    } else {
	      annotation = new Annotation("Kosgi Santosh sent an email to Stanford University. He didn't get a reply.");
	    }

	    // run all the selected Annotators on this text
	    pipeline.annotate(annotation);

	    // this prints out the results of sentence analysis to file(s) in good formats
	    pipeline.prettyPrint(annotation, out);
	    if (xmlOut != null) {
	      pipeline.xmlPrint(annotation, xmlOut);
	    }

	    // Access the Annotation in code
	    // The toString() method on an Annotation just prints the text of the Annotation
	    // But you can see what is in it with other methods like toShorterString()
	    out.println();
	    out.println("The top level annotation");
	    out.println(annotation.toShorterString());
	    out.println();

	    // An Annotation is a Map with Class keys for the linguistic analysis types.
	    // You can get and use the various analyses individually.
	    // For instance, this gets the parse tree of the first sentence in the text.
	    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	    if (sentences != null && ! sentences.isEmpty()) {
	      CoreMap sentence = sentences.get(0);
	      out.println("The keys of the first sentence's CoreMap are:");
	      out.println(sentence.keySet());
	      out.println();
	      out.println("The first sentence is:");
	      out.println(sentence.toShorterString());
	      out.println();
	      out.println("The first sentence tokens are:");
	      for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
	        out.println(token.toShorterString());
	      }
	      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
	      out.println();
	      out.println("The first sentence parse tree is:");
	      tree.pennPrint(out);
	      out.println();
	      out.println("The first sentence basic dependencies are:");
	      out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
	      out.println("The first sentence collapsed, CC-processed dependencies are:");
	      SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
	      out.println(graph.toString(SemanticGraph.OutputFormat.LIST));

	      // Print out dependency structure around one word
	      // This give some idea of how to navigate the dependency structure in a SemanticGraph
	      IndexedWord node = graph.getNodeByIndexSafe(5);
	      // The below way also works
	      // IndexedWord node = new IndexedWord(sentences.get(0).get(CoreAnnotations.TokensAnnotation.class).get(5 - 1));
	      out.println("Printing dependencies around \"" + node.word() + "\" index " + node.index());
	      List<SemanticGraphEdge> edgeList = graph.getIncomingEdgesSorted(node);
	      if (! edgeList.isEmpty()) {
	        assert edgeList.size() == 1;
	        int head = edgeList.get(0).getGovernor().index();
	        String headWord = edgeList.get(0).getGovernor().word();
	        String deprel = edgeList.get(0).getRelation().toString();
	        out.println("Parent is word \"" + headWord + "\" index " + head + " via " + deprel);
	      } else  {
	        out.println("Parent is ROOT via root");
	      }
	      edgeList = graph.outgoingEdgeList(node);
	      for (SemanticGraphEdge edge : edgeList) {
	        String depWord = edge.getDependent().word();
	        int depIdx = edgeList.get(0).getDependent().index();
	        String deprel = edge.getRelation().toString();
	        out.println("Child is \"" + depWord + "\" (" + depIdx + ") via " + deprel);
	      }
	      out.println();


	      // Access coreference. In the coreference link graph,
	      // each chain stores a set of mentions that co-refer with each other,
	      // along with a method for getting the most representative mention.
	      // Both sentence and token offsets start at 1!
	      out.println("Coreference information");
	      Map<Integer, CorefChain> corefChains =
	          annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
	      if (corefChains == null) { return; }
	      for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
	        out.println("Chain " + entry.getKey());
	        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
	          // We need to subtract one since the indices count from 1 but the Lists start from 0
	          List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
	          // We subtract two for end: one for 0-based indexing, and one because we want last token of mention not one following.
	          out.println("  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition() +
	                  ", " + tokens.get(m.endIndex - 2).endPosition() + ')');
	        }
	      }
	      out.println();

	      out.println("The first sentence overall sentiment rating is " + sentence.get(SentimentCoreAnnotations.SentimentClass.class));
	    }
	    IOUtils.closeIgnoringExceptions(out);
	    IOUtils.closeIgnoringExceptions(xmlOut);
	  }

	}*/
}
