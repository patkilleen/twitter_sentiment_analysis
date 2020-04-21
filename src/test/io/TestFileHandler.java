package test.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import classify.TweetParser;
import io.FileHandler;
import io.FoldInfo;
import io.KFoldCrossValidationConsummer;
import junit.framework.Assert;

public class TestFileHandler {

	@Test
	public void test_subFile() throws IOException {
		File src = File.createTempFile("subFileTest", null);
		File p1 = File.createTempFile("subFileTest", null);
		File p2 = File.createTempFile("subFileTest", null);
		src.deleteOnExit();
		p1.deleteOnExit();
		p2.deleteOnExit();
		for(int i = 0;i<100;i++){
			String output = i+TweetParser.NEW_LINE;
			FileHandler.append(src.toPath(), output.getBytes());
		}

		int fromLineIx = 10;
		int toLineIx = 100;
		FileHandler.subFile(src.toPath(), p1.toPath(), p2.toPath(), fromLineIx, toLineIx);



		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(p1.getAbsoluteFile()), Charset.forName("UTF-8")));
		String line = reader.readLine();

		for(int i = 0;i< 10;i++){
			Assert.assertEquals(""+i,line);
			line = reader.readLine();
		}
		Assert.assertEquals(null,line);
		reader.close();
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(p2.getAbsoluteFile()), Charset.forName("UTF-8")));
		line = reader.readLine();

		for(int i = 10;i< 100;i++){
			Assert.assertEquals(""+i,line);
			line = reader.readLine();
		}
		Assert.assertEquals(null,line);
		reader.close();

	}


	@Test
	public void test_subFile2() throws IOException {
		File src = File.createTempFile("subFileTest", null);
		File p1 = File.createTempFile("subFileTest", null);
		File p2 = File.createTempFile("subFileTest", null);
		src.deleteOnExit();
		p1.deleteOnExit();
		p2.deleteOnExit();
		for(int i = 0;i<100;i++){
			String output = i+TweetParser.NEW_LINE;
			FileHandler.append(src.toPath(), output.getBytes());
		}

		int fromLineIx = 10;
		int toLineIx = 50;
		FileHandler.subFile(src.toPath(), p1.toPath(), p2.toPath(), fromLineIx, toLineIx);



		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(p1.getAbsoluteFile()), Charset.forName("UTF-8")));
		String line = reader.readLine();

		for(int i = 0;i< 10;i++){
			Assert.assertEquals(""+i,line);
			line = reader.readLine();
		}

		for(int i = 50;i< 100;i++){
			Assert.assertEquals(""+i,line);
			line = reader.readLine();
		}
		Assert.assertEquals(null,line);
		reader.close();
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(p2.getAbsoluteFile()), Charset.forName("UTF-8")));
		line = reader.readLine();

		for(int i = 10;i< 50;i++){
			Assert.assertEquals(""+i,line);
			line = reader.readLine();
		}
		Assert.assertEquals(null,line);
		reader.close();

	}

	@Test
	public void test_subFile3() throws IOException {
		File src = File.createTempFile("subFileTest", null);
		File p1 = File.createTempFile("subFileTest", null);
		File p2 = File.createTempFile("subFileTest", null);
		src.deleteOnExit();
		p1.deleteOnExit();
		p2.deleteOnExit();
		for(int i = 0;i<100;i++){
			String output = i+TweetParser.NEW_LINE;
			FileHandler.append(src.toPath(), output.getBytes());
		}

		int fromLineIx = 10;
		int toLineIx = 100;
		FileHandler.subFile(src.toPath(), p1.toPath(), p2.toPath(), fromLineIx);



		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(p1.getAbsoluteFile()), Charset.forName("UTF-8")));
		String line = reader.readLine();

		for(int i = 0;i< 10;i++){
			Assert.assertEquals(""+i,line);
			line = reader.readLine();
		}
		Assert.assertEquals(null,line);
		reader.close();
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(p2.getAbsoluteFile()), Charset.forName("UTF-8")));
		line = reader.readLine();

		for(int i = 10;i< 100;i++){
			Assert.assertEquals(""+i,line);
			line = reader.readLine();
		}
		Assert.assertEquals(null,line);
		reader.close();

	}

	@Test
	public void test_subFile4() throws IOException {
		File src = File.createTempFile("subFileTest", null);
		File p1 = File.createTempFile("subFileTest", null);
		File p2 = File.createTempFile("subFileTest", null);
		src.deleteOnExit();
		p1.deleteOnExit();
		p2.deleteOnExit();
		for(int i = 0;i<100;i++){
			String output = i+TweetParser.NEW_LINE;
			FileHandler.append(src.toPath(), output.getBytes());
		}

		int fromLineIx = 10;
		int toLineIx = 150;//overflow line should matter
		FileHandler.subFile(src.toPath(), p1.toPath(), p2.toPath(), fromLineIx, toLineIx);



		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(p1.getAbsoluteFile()), Charset.forName("UTF-8")));
		String line = reader.readLine();

		for(int i = 0;i< 10;i++){
			Assert.assertEquals(""+i,line);
			line = reader.readLine();
		}
		Assert.assertEquals(null,line);
		reader.close();
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(p2.getAbsoluteFile()), Charset.forName("UTF-8")));
		line = reader.readLine();

		for(int i = 10;i< 100;i++){
			Assert.assertEquals(""+i,line);
			line = reader.readLine();
		}
		Assert.assertEquals(null,line);
		reader.close();

	}

	@Test
	public void test_createSampleFile() throws IOException {
		File src = File.createTempFile("createSampleFileTest", null);
		File fdelta = File.createTempFile("createSampleFileTest", null);
		File dest = File.createTempFile("createSampleFileTest", null);
		src.deleteOnExit();
		fdelta.deleteOnExit();
		dest.deleteOnExit();
		for(char c = 'a';c<'z';c++){
			String output = c+TweetParser.NEW_LINE;
			FileHandler.append(src.toPath(), output.getBytes());
		}

		FileHandler.createSampleFile(src.getAbsolutePath(), fdelta.getAbsolutePath(), dest.getAbsolutePath(),0.5);

		int srcNumLines = FileHandler.countLines(src.toString());
		int fdeltaNumLines = FileHandler.countLines(fdelta.toString());
		int destNumLines = FileHandler.countLines(dest.toString());

		//same number of lines
		Assert.assertEquals(srcNumLines, fdeltaNumLines+destNumLines);

		String fdeltaData = FileHandler.readFile(fdelta.getAbsolutePath());
		String destData = FileHandler.readFile(dest.getAbsolutePath());

		//make sure the content is uninque in each file

		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(fdelta.getAbsoluteFile()), Charset.forName("UTF-8")));
		String line = reader.readLine();
		while (line != null) {

			char c = line.charAt(0);

			Assert.assertEquals(-1,destData.indexOf(c));
			// read next line
			line = reader.readLine();
		}
		reader.close();

		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(dest.getAbsoluteFile()), Charset.forName("UTF-8")));
		line = reader.readLine();
		while (line != null) {

			char c = line.charAt(0);

			Assert.assertEquals(-1,fdeltaData.indexOf(c));
			// read next line
			line = reader.readLine();
		}
		reader.close();
	}

	@Test
	public void test_kfold_crossValidation() throws IOException {
		File src = File.createTempFile("subFileTest", null);
		src.deleteOnExit();
		for(int i = 0;i<100;i++){
			String output = i+TweetParser.NEW_LINE;
			FileHandler.append(src.toPath(), output.getBytes());
		}


		List<String> expectedTrainingFileContents = new ArrayList<String>(4);
		List<String> expectedTestingFileContents = new ArrayList<String>(4);

		/*
		 * first fold
		 * testing: 0-24
		 * training: 25-100
		 */
		String expected = "";

		//testing
		for(int i = 0;i<25;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTestingFileContents.add(expected);

		expected = "";

		//training
		for(int i = 25;i<100;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTrainingFileContents.add(expected);
		
		/*
		 * 2nd fold
		 * testing: 25-49
		 * training: 0-24 and 50-100
		 */
		expected = "";

		//testing
		for(int i = 25;i<50;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTestingFileContents.add(expected);

		expected = "";

		//training
		for(int i = 0;i<25;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		for(int i = 50;i<100;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTrainingFileContents.add(expected);
		

		/*
		 * 3rd fold
		 * testing: 50-74
		 * training: 0-49 and 75-100
		 */
		expected = "";

		//testing
		for(int i = 50;i<75;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTestingFileContents.add(expected);

		expected = "";

		//training
		for(int i = 0;i<50;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		for(int i = 75;i<100;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTrainingFileContents.add(expected);
		
		/*
		 * 4th fold
		 * testing: 75-100
		 * training: 0-74
		 */
		expected = "";

		//testing
		for(int i = 75;i<100;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTestingFileContents.add(expected);

		expected = "";

		//training
		for(int i = 0;i<75;i++){
			expected+=i+TweetParser.NEW_LINE;

		}

		expectedTrainingFileContents.add(expected);


		KFCVHelper helper = new KFCVHelper(expectedTrainingFileContents,expectedTestingFileContents);
		int k = 4;
		FileHandler.performKFoldCrossValidation(helper, src.toPath(), k);


	}
	
	@Test
	public void test_kfold_crossValidation2() throws IOException {
		File src = File.createTempFile("subFileTest", null);
		src.deleteOnExit();
		for(int i = 0;i<101;i++){
			String output = i+TweetParser.NEW_LINE;
			FileHandler.append(src.toPath(), output.getBytes());
		}


		List<String> expectedTrainingFileContents = new ArrayList<String>(4);
		List<String> expectedTestingFileContents = new ArrayList<String>(4);

		/*
		 * first fold
		 * testing: 0-24
		 * training: 25-101
		 */
		String expected = "";

		//testing
		for(int i = 0;i<25;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTestingFileContents.add(expected);

		expected = "";

		//training
		for(int i = 25;i<101;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTrainingFileContents.add(expected);
		
		/*
		 * 2nd fold
		 * testing: 25-49
		 * training: 0-24 and 50-101
		 */
		expected = "";

		//testing
		for(int i = 25;i<50;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTestingFileContents.add(expected);

		expected = "";

		//training
		for(int i = 0;i<25;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		for(int i = 50;i<101;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTrainingFileContents.add(expected);
		

		/*
		 * 3rd fold
		 * testing: 50-74
		 * training: 0-49 and 75-101
		 */
		expected = "";

		//testing
		for(int i = 50;i<75;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTestingFileContents.add(expected);

		expected = "";

		//training
		for(int i = 0;i<50;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		for(int i = 75;i<101;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTrainingFileContents.add(expected);
		
		/*
		 * 4th fold
		 * testing: 75-101
		 * training: 0-74
		 */
		expected = "";

		//testing
		for(int i = 75;i<101;i++){
			expected+=i+TweetParser.NEW_LINE;

		}
		expectedTestingFileContents.add(expected);

		expected = "";

		//training
		for(int i = 0;i<75;i++){
			expected+=i+TweetParser.NEW_LINE;

		}

		expectedTrainingFileContents.add(expected);


		KFCVHelper helper = new KFCVHelper(expectedTrainingFileContents,expectedTestingFileContents);
		int k = 4;
		FileHandler.performKFoldCrossValidation(helper, src.toPath(), k);


	}


	private static class KFCVHelper implements KFoldCrossValidationConsummer{

		protected List<String> expectedTrainingFileContents;
		protected List<String> expectedTestingFileContents;

		private int i;



		public KFCVHelper(List<String> expectedTrainingFileContents, List<String> expectedTestingFileContents) {
			super();
			this.expectedTrainingFileContents = expectedTrainingFileContents;
			this.expectedTestingFileContents = expectedTestingFileContents;
			this.i = 0;
		}



		@Override
		public void consumFold(Path training, Path test, FoldInfo info){
			try {
				String trainingData = FileHandler.readFile(training.toString());
				String testingData = FileHandler.readFile(test.toString());

				String expectedTrainingData =expectedTrainingFileContents.get(i);
				String expectedTestingData =expectedTestingFileContents.get(i);

				Assert.assertEquals(expectedTrainingData, trainingData);
				Assert.assertEquals(expectedTestingData, testingData);
				i++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
