package test.classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import classify.TweetParser;
import io.FileHandler;

public class TestTweetParser extends TweetParser {


	public TestTweetParser() {
		super();
	}

	public static TestTweetParser buildTestInstance(){

		List<String> stopwords = new ArrayList<String>(2);
		List<String> positiveEmoticons = new ArrayList<String>(2);
		List<String> negativeEmoticons = new ArrayList<String>(2);

		boolean removeSymbolesFlag = false;
		String datasetParsingFormat = "STANFORD";

		TestTweetParser parser = new TestTweetParser();
		parser.init(stopwords, 
				positiveEmoticons, negativeEmoticons, datasetParsingFormat,removeSymbolesFlag);
		return parser;



	}
	@Test
	public void test_isURL() {

		TestTweetParser parser = new TestTweetParser();
		Assert.assertEquals(true, parser.isURL("http://www.youtube.com"));
		Assert.assertEquals(true, parser.isURL("https://www.youtube.com"));
		Assert.assertEquals(true, parser.isURL("file://www.youtube.com"));
		Assert.assertEquals(true, parser.isURL("file://www.youtube.com/video"));
		Assert.assertEquals(true, parser.isURL("file://www.youtube.com/video/index.html"));
		Assert.assertEquals(true, parser.isURL("file://www.youtube.ca/video/index.html"));
		Assert.assertEquals(true, parser.isURL("ftp://www.youtube.ca/video/%2f%2findex.html"));
		Assert.assertEquals(false, parser.isURL("youtube"));
		Assert.assertEquals(true, parser.isURL("http://10.0.0.3:3000"));
		Assert.assertEquals(false, parser.isURL(null));

	}

	@Test
	public void test_removeSymboles(){

		TestTweetParser parser = new TestTweetParser();
		String tweet = "hello world (USERNAME), and  $ here is a link to a url: , with a few ~ more words.";
		
		String actual = TestTweetParser.removeSymboles(tweet);
		Assert.assertEquals(true, ("hello world (USERNAME) and   here is a link to a url  with a few  more words").equals(actual));

		//no urls
		tweet = "there is no symbole in this sentence";
		actual = TestTweetParser.removeSymboles(tweet);
		Assert.assertEquals(true, ("there is no symbole in this sentence").equals(actual));
	}
	@Test
	public void test_replaceAddresses(){

		TestTweetParser parser = new TestTweetParser();
		String tweet = "hello world :), and here is a link to a url: http://www.youtube.com , with a few more words.";
		String actual = parser.replaceURLs(tweet);
		Assert.assertEquals(true, ("hello world :), and here is a link to a url: "+TweetParser.URL_TEMPLATE+" , with a few more words.").equals(actual));

		tweet = "a link file://www.youtube.com and another file://www.youtube.ca/video/index.html and another ftp://www.youtube.ca/video/%2f%2findex.html";
		actual = parser.replaceURLs(tweet);
		Assert.assertEquals(true, ("a link "+TweetParser.URL_TEMPLATE+" and another "+TweetParser.URL_TEMPLATE+" and another "+TweetParser.URL_TEMPLATE).equals(actual));


		//no urls
		tweet = "there is no link in this sentence";
		actual = parser.replaceURLs(tweet);
		Assert.assertEquals(true, ("there is no link in this sentence").equals(actual));

		//empty
		tweet = "";
		actual = parser.replaceURLs(tweet);
		Assert.assertEquals(true, ("").equals(actual));

		//null
		tweet = null;
		actual = parser.replaceURLs(tweet);
		Assert.assertEquals(null,actual);
	}

	@Test
	public void test_loadStopWords() throws IOException{

		File rawDatasetFile;
		rawDatasetFile = File.createTempFile("tmp",".tmp");
		rawDatasetFile.deleteOnExit();
		Path rawDatasetPath = rawDatasetFile.toPath();

		//empty file
		List<String> stopwords = TweetParser.loadStopwords(rawDatasetPath.toString());
		Assert.assertEquals(0,stopwords.size());


		String stopWordOutput = "";
		stopWordOutput+="hello"+TweetParser.NEW_LINE;
		stopWordOutput+="world"+TweetParser.NEW_LINE;
		stopWordOutput+="the"+TweetParser.NEW_LINE;
		stopWordOutput+="and";



		FileHandler.append(rawDatasetPath, stopWordOutput.getBytes());

		stopwords = TweetParser.loadStopwords(rawDatasetPath.toString());
		Assert.assertEquals(true, stopwords.get(0).equals("hello"));
		Assert.assertEquals(true, stopwords.get(1).equals("world"));
		Assert.assertEquals(true, stopwords.get(2).equals("the"));
		Assert.assertEquals(true, stopwords.get(3).equals("and"));
		Assert.assertEquals(4, stopwords.size());

		//null file path
		stopwords = TweetParser.loadStopwords(null);
		Assert.assertEquals(0,stopwords.size());
	}


	@Test
	public void test_removeContainingQuotes(){

		String expected = "string";
		Assert.assertEquals(true,expected.equals(TweetParser.removeContainingQuotes("\""+expected+"\"")));

		expected = "string ";
		Assert.assertEquals(true,expected.equals(TweetParser.removeContainingQuotes("\""+expected+"\"")));

		expected = " string ";
		Assert.assertEquals(true,expected.equals(TweetParser.removeContainingQuotes("\""+expected+"\"")));

		expected = "";
		Assert.assertEquals(true,expected.equals(TweetParser.removeContainingQuotes("\""+expected+"\"")));

		expected = "a";
		Assert.assertEquals(true,expected.equals(TweetParser.removeContainingQuotes("\""+expected+"\"")));

		expected = "\"\"";
		Assert.assertEquals(true,expected.equals(TweetParser.removeContainingQuotes("\""+expected+"\"")));

		Assert.assertEquals(null,TweetParser.removeContainingQuotes(null));
	}

	@Test
	public void test_applyGrammarCorrection(){
		String expected = "good";
		String actual = TweetParser.applyGrammarCorrection("gooooooood");
		Assert.assertEquals(expected,actual);
		expected = "good";
		Assert.assertEquals(true,expected.equals(TweetParser.applyGrammarCorrection("goood")));

		expected = "baad graamar here";
		Assert.assertEquals(true,expected.equals(TweetParser.applyGrammarCorrection("baaaad graaamar here")));

		expected = "a few miistakes arre here";
		Assert.assertEquals(true,expected.equals(TweetParser.applyGrammarCorrection("a few miiiiiistakes arrre here ")));

		expected = "a few miistakes arre here";
		Assert.assertEquals(true,expected.equals(TweetParser.applyGrammarCorrection("   a few miiiiiistakes arrre here ")));

		expected = "";
		Assert.assertEquals(true,expected.equals(TweetParser.applyGrammarCorrection("")));

		expected = "oo";
		Assert.assertEquals(true,expected.equals(TweetParser.applyGrammarCorrection("ooo")));

		expected = "a";
		Assert.assertEquals(true,expected.equals(TweetParser.applyGrammarCorrection("a")));

		Assert.assertEquals(null,TweetParser.applyGrammarCorrection(null));
	}

	@Test
	public void test_replaceUserPointers(){
		String expected = "hello world with "+TweetParser.USERNAME_TEMPLATE+" no user reference";
		String actual = TweetParser.replaceUserPointers("hello world with @user123 no user reference");

		Assert.assertEquals(expected,actual);

		expected = ""+TweetParser.USERNAME_TEMPLATE+" hello world with "+TweetParser.USERNAME_TEMPLATE+" no user reference";
		actual = TweetParser.replaceUserPointers("@anotheruser hello world with @user123 no user reference");

		Assert.assertEquals(expected,actual);

		expected = ""+TweetParser.USERNAME_TEMPLATE+" hello world with "+TweetParser.USERNAME_TEMPLATE+" no user reference "+TweetParser.USERNAME_TEMPLATE;
		actual = TweetParser.replaceUserPointers("@anotheruser hello world with @user123 no user reference @lastuser");

		Assert.assertEquals(expected,actual);

		expected = ""+TweetParser.USERNAME_TEMPLATE+" hello @ world with "+TweetParser.USERNAME_TEMPLATE+" no user reference "+TweetParser.USERNAME_TEMPLATE;
		actual = TweetParser.replaceUserPointers("@anotheruser hello @ world with @user123 no user reference @lastuser");

		Assert.assertEquals(expected,actual);

		expected = "no user in this sentence";
		actual = TweetParser.replaceUserPointers("no user in this sentence");

		Assert.assertEquals(expected,actual);

		expected = "";
		actual = TweetParser.replaceUserPointers("");

		Assert.assertEquals(expected,actual);

		expected = "a";
		actual = TweetParser.replaceUserPointers("a");

		Assert.assertEquals(expected,actual);

		expected = null;
		actual = TweetParser.replaceUserPointers(null);

		Assert.assertEquals(expected,actual);

		expected = ""+TweetParser.USERNAME_TEMPLATE+" hello world with "+TweetParser.USERNAME_TEMPLATE+" no user reference";
		actual = TweetParser.replaceUserPointers("     @anotheruser hello world with @user123 no   user reference   ");

		Assert.assertEquals(expected,actual);
	}

	@Test
	public void test_replaceEmoticons(){
		TestTweetParser p = buildTestInstance();
		p.positiveEmoticons.add(":)");
		p.positiveEmoticons.add(":D");
		p.positiveEmoticons.add(":-)");
		p.negativeEmoticons.add(":(");
		p.negativeEmoticons.add(":-(");
		p.negativeEmoticons.add(":/");

		String expected = TweetParser.NEGATIVE_EMOTICON_TEMPLATE+" hello world "+TweetParser.POSITIVE_EMOTICON_TEMPLATE;
		String actual = p.replaceEmoticons(":( hello world :)");
		Assert.assertEquals(expected, actual);


		expected = "hello world xD";
		actual = p.replaceEmoticons(" hello world xD");
		Assert.assertEquals(expected, actual);

		expected = TweetParser.POSITIVE_EMOTICON_TEMPLATE;
		actual = p.replaceEmoticons(":)");
		Assert.assertEquals(expected, actual);

		expected = TweetParser.NEGATIVE_EMOTICON_TEMPLATE;
		actual = p.replaceEmoticons(":(");
		Assert.assertEquals(expected, actual);

		expected = TweetParser.POSITIVE_EMOTICON_TEMPLATE;
		actual = p.replaceEmoticons(":D");
		Assert.assertEquals(expected, actual);

		expected = TweetParser.NEGATIVE_EMOTICON_TEMPLATE;
		actual = p.replaceEmoticons(":/");
		Assert.assertEquals(expected, actual);

		expected = "):";
		actual = p.replaceEmoticons("):");
		Assert.assertEquals(expected, actual);

		expected = "hello "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" world";
		actual = p.replaceEmoticons("hello :D world   ");
		Assert.assertEquals(expected, actual);

		expected = "";
		actual = p.replaceEmoticons("");
		Assert.assertEquals(expected, actual);

		expected = null;
		actual = p.replaceEmoticons(null);
		Assert.assertEquals(expected, actual);

	}

	@Test
	public void test_removeDots(){

		String expected = "hello world";
		String actual = TweetParser.removeDots("hello world.");
		Assert.assertEquals(expected, actual);

		expected = "hello world";
		actual = TweetParser.removeDots(".hello world.");
		Assert.assertEquals(expected, actual);

		expected = "hello world";
		actual = TweetParser.removeDots(".hello .world.");
		Assert.assertEquals(expected, actual);

		expected = "hello world";
		actual = TweetParser.removeDots(".hello. .world.");
		Assert.assertEquals(expected, actual);

		expected = "hello  world";
		actual = TweetParser.removeDots(".hello. . world.");
		Assert.assertEquals(expected, actual);

		expected = " hello  world ";
		actual = TweetParser.removeDots(" .hello. . world. ");
		Assert.assertEquals(expected, actual);

		expected = " hello  world ";
		actual = TweetParser.removeDots(". hello. . world .");
		Assert.assertEquals(expected, actual);

		expected = "";
		actual = TweetParser.removeDots("");
		Assert.assertEquals(expected, actual);

		expected = null;
		actual = TweetParser.removeDots(null);
		Assert.assertEquals(expected, actual);

	}
	@Test
	public void test_removeStopwords(){
		TestTweetParser p = buildTestInstance();
		p.stopwords.add("the");
		p.stopwords.add("and");
		p.stopwords.add("it");

		String expected = "hello world";
		String actual = p.removeStopWords("the hello and world");
		Assert.assertEquals(expected, actual);

		expected = "";
		actual = p.removeStopWords("the and it");
		Assert.assertEquals(expected, actual);

		expected = "hello world";
		actual = p.removeStopWords("and hello it world the  ");
		Assert.assertEquals(expected, actual);

		expected = "hello world";
		actual = p.removeStopWords("   and hello it world the  ");
		Assert.assertEquals(expected, actual);

		expected = "hello world";
		actual = p.removeStopWords("hello it world the  ");
		Assert.assertEquals(expected, actual);

		expected = "";
		actual = p.removeStopWords("");
		Assert.assertEquals(expected, actual);

		expected = null;
		actual = p.removeStopWords(null);
		Assert.assertEquals(expected, actual);

	} 


	@Test
	public void test_normalizeTweet_empty_tweet(){
		TestTweetParser p = buildTestInstance();
		p.stopwords.add("the");
		p.stopwords.add("and");
		p.stopwords.add("it");
		p.positiveEmoticons.add(":)");
		p.positiveEmoticons.add(":D");
		p.positiveEmoticons.add(":-)");
		p.negativeEmoticons.add(":(");
		p.negativeEmoticons.add(":-(");
		p.negativeEmoticons.add(":/");

		String expected = null;
		String actual = p.normalizeTweet("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"and it the      the\"");
		Assert.assertEquals(expected, actual);
	}
	@Test
	public void test_normalizeTweet(){
		TestTweetParser p = buildTestInstance();
		p.stopwords.add("the");
		p.stopwords.add("and");
		p.stopwords.add("it");
		p.positiveEmoticons.add(":)");
		p.positiveEmoticons.add(":D");
		p.positiveEmoticons.add(":-)");
		p.negativeEmoticons.add(":(");
		p.negativeEmoticons.add(":-(");
		p.negativeEmoticons.add(":/");

		String expected = "4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" a couple . , etc.";
		String actual = p.normalizeTweet("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) and a url http://www.youtube.com/ and a couple . the , etc. \"");
		Assert.assertEquals(expected, actual);

		expected = "4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" a couple . , etc. ,";
		actual = p.normalizeTweet("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) and a url http://www.youtube.com/ and a couple . the , etc. ,\"");
		Assert.assertEquals(expected, actual);

		expected = "4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" ,a, couple . , etc. ,";
		actual = p.normalizeTweet("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) and a url http://www.youtube.com/ and ,a, couple . the , etc. ,\"");
		Assert.assertEquals(expected, actual);

		expected = "4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" ,a, couple . , etc. ,";
		actual = p.normalizeTweet("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) a url http://www.youtube.com/ and ,a, couple . the , etc. ,\"");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void test_normalizeTweetDataset() throws IOException{ 
		TestTweetParser p = buildTestInstance();
		p.stopwords.add("the");
		p.stopwords.add("and");
		p.stopwords.add("it");
		p.positiveEmoticons.add(":)");
		p.positiveEmoticons.add(":D");
		p.positiveEmoticons.add(":-)");
		p.negativeEmoticons.add(":(");
		p.negativeEmoticons.add(":-(");
		p.negativeEmoticons.add(":/");

		List<String> rawTweets = new ArrayList<String>(4);
		rawTweets.add("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+"\"hellooooooooo woooorld from @user12345 with :) and a url http://www.youtube.com/ and a couple . the , etc. \"");
		rawTweets.add("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) and a url http://www.youtube.com/ and a couple . the , etc. ,\"");
		rawTweets.add("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) and a url http://www.youtube.com/ and ,a, couple . the , etc. ,\"");
		rawTweets.add("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) a url http://www.youtube.com/ and ,a, couple . the , etc. ,\"");

		List<String> normalizedTweets = new ArrayList<String>(4);
		normalizedTweets.add("4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" a couple . , etc.");
		normalizedTweets.add("4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" a couple . , etc. ,");
		normalizedTweets.add("4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" ,a, couple . , etc. ,");
		normalizedTweets.add("4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" ,a, couple . , etc. ,");

		File rawDatasetFile;
		rawDatasetFile = File.createTempFile("tmp",".tmp");
		rawDatasetFile.deleteOnExit();
		Path rawDatasetPath = rawDatasetFile.toPath();
		File formatedDatasetFile = File.createTempFile("tmp",".tmp");
		formatedDatasetFile.deleteOnExit();
		Path formatedDatasetPath = formatedDatasetFile.toPath();
		
		//append the raw tweets to temporary rawDataset file
		for(String s : rawTweets){
			String tmp = s;//deep copy, just to be safe that were not modifying the rawTweet list
			tmp+=TweetParser.NEW_LINE;
			FileHandler.append(rawDatasetPath, tmp.getBytes());	
		}

		//now normlize and output to file
		p.normalizeTweetDatasetFile(rawDatasetPath,formatedDatasetPath);

		//read the file are confirm the normalization was done for each line in file

		BufferedReader reader;
		int i = 0;
		try {
			reader = new BufferedReader(new FileReader(
					formatedDatasetPath.toString()));
			String line = reader.readLine();

			//read each stop word 
			while (line != null) {

				Assert.assertEquals(normalizedTweets.get(i),line);

				// read next line
				line = reader.readLine();

				i++;	

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void test_normalizeTweetDataset_remove_symboles() throws IOException{ 
		TestTweetParser p = buildTestInstance();
		p.removingSymbolsFlag=true;
		p.stopwords.add("the");
		p.stopwords.add("and");
		p.stopwords.add("it");
		p.positiveEmoticons.add(":)");
		p.positiveEmoticons.add(":D");
		p.positiveEmoticons.add(":-)");
		p.negativeEmoticons.add(":(");
		p.negativeEmoticons.add(":-(");
		p.negativeEmoticons.add(":/");

		List<String> rawTweets = new ArrayList<String>(4);
		rawTweets.add("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+"\"hellooooooooo woooorld from @user12345 with :) and a url http://www.youtube.com/ and a couple . the , etc. \"");
		rawTweets.add("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) and a url http://www.youtube.com/ and a couple . the , etc. ,\"");
		rawTweets.add("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) and a url http://www.youtube.com/ and ,a, couple . the , etc. ,\"");
		rawTweets.add("\"4\",\"12345\",\"Mon Jan 01 12:30:35 PDT 2020\",\"NO_QUERY\",\"user12345\","
				+ "\"hellooooooooo woooorld from @user12345 with :) a url http://www.youtube.com/ and ,a, couple . the , etc. ,\"");

		List<String> normalizedTweets = new ArrayList<String>(4);
		normalizedTweets.add("4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" a couple   etc");
		normalizedTweets.add("4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" a couple   etc ");
		normalizedTweets.add("4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" a couple   etc ");
		normalizedTweets.add("4"+TweetParser.TAB+"helloo woorld from "+TweetParser.USERNAME_TEMPLATE+" with "+TweetParser.POSITIVE_EMOTICON_TEMPLATE+" a url "+TweetParser.URL_TEMPLATE+" a couple   etc ");

		File rawDatasetFile;
		rawDatasetFile = File.createTempFile("tmp",".tmp");
		rawDatasetFile.deleteOnExit();
		Path rawDatasetPath = rawDatasetFile.toPath();
		File formatedDatasetFile = File.createTempFile("tmp",".tmp");
		formatedDatasetFile.deleteOnExit();
		Path formatedDatasetPath = formatedDatasetFile.toPath();
		
		//append the raw tweets to temporary rawDataset file
		for(String s : rawTweets){
			String tmp = s;//deep copy, just to be safe that were not modifying the rawTweet list
			tmp+=TweetParser.NEW_LINE;
			FileHandler.append(rawDatasetPath, tmp.getBytes());	
		}

		//now normlize and output to file
		p.normalizeTweetDatasetFile(rawDatasetPath,formatedDatasetPath);

		//read the file are confirm the normalization was done for each line in file

		BufferedReader reader;
		int i = 0;
		try {
			reader = new BufferedReader(new FileReader(
					formatedDatasetPath.toString()));
			String line = reader.readLine();

			//read each stop word 
			while (line != null) {

				Assert.assertEquals(normalizedTweets.get(i),line);

				// read next line
				line = reader.readLine();

				i++;	

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
