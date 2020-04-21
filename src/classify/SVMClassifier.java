package classify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.ProcessRunner;
import io.ProcessStreamReader;
import io.log.Logger;
import io.log.LoggerFactory;

public class SVMClassifier extends Classifier {
	
	private static final int SVM_POSITIVE_LABEL_IX = 2;
	private static final int SVM_NEUTRAL_LABEL_IX = 1;
	private static final int SVM_NEGATIVE_LABEL_IX = 0;
	
	private List<String> SVM_labels;
	public SVMClassifier(String name, String command,boolean includingNeutralTweets) {
		super(name, command);
		
		
		SVM_labels = new ArrayList<String>(3);
		SVM_labels.add(null);
		SVM_labels.add(null);
		SVM_labels.add(null);
		
		//adjust the svmlight labels depending on if neutral tweets are considered
		if(includingNeutralTweets){
			SVM_labels.set(SVM_POSITIVE_LABEL_IX, "3");//label is 3 since SVM light outputs positive labels (4) as 3
			SVM_labels.set(SVM_NEUTRAL_LABEL_IX, "2");
			SVM_labels.set(SVM_NEGATIVE_LABEL_IX, "1");
		}else{
			SVM_labels.set(SVM_POSITIVE_LABEL_IX, "+1");//+1 since SVM light will considedr positive (4) this label
			SVM_labels.set(SVM_NEUTRAL_LABEL_IX, "N/A");
			SVM_labels.set(SVM_NEGATIVE_LABEL_IX, "-1");
		}
		
		
	}

	public static Path toSVMLightFormat(String stanfordLibPath, String src,String stanfordPropFile,String testDataset){
		
		
		
		File svmLightOut;
		try {
			svmLightOut = File.createTempFile("svmLight", null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		Logger log = LoggerFactory.getInstance();
		log.log_debug("converting "+src+" to svmlight format: "+svmLightOut.getAbsolutePath());
		//svmLightOut.deleteOnExit();
		
		//find a way to optimize this, since Max ENt is being run, even though the only goal is to convert file to SVM light format
		String cmd = "java -cp "+stanfordLibPath+"  edu.stanford.nlp.classify.ColumnDataClassifier "
				+ "-trainFile "+src.toString()+" "
				+ "-printSVMLightFormatTo "+svmLightOut.getAbsolutePath()+" "
				+ "-prop "+stanfordPropFile.toString()+" "
				+ "-testFile "+testDataset.toString();
		
		ProcessStreamReader reader = new ProcessStreamReader(){
			@Override
			public void readLine(String line){
				//System.out.println(line);
			}
		};
		ProcessRunner runner = new ProcessRunner(reader);
		int rc = runner.exec(cmd);
		
		if(rc != 0){
			log.log_error(runner.getErrorMessage());
			throw new IllegalStateException("failed to run the process: "+cmd);
		}
		
		return svmLightOut.toPath();
	}
	
	protected String parseSVMLabel(String label){
		String res = "";
		//convert from SVM (+1 and -1) to consistent prediction labels
		if(label.equals(SVM_labels.get(SVM_POSITIVE_LABEL_IX))){
			res = Classifier.POSITIVE_LABEL_VALUE;
		}else if(label.equals(SVM_labels.get(SVM_NEGATIVE_LABEL_IX))){
			res = Classifier.NEGATIVE_LABEL_VALUE;
		}else if(label.equals(SVM_labels.get(SVM_NEUTRAL_LABEL_IX))){
			res = Classifier.NEUTRAL_LABEL_VALUE;
		}
		
		return res;
	}
	/**
	 * Hook to be implemented by subclasses if they
	 * wish to implement the label parsing differently
	 * @return process stream reader used to parse labels
	 */
	protected ProcessStreamReader getStreamReaderHook(){
		return new SVMProcessStreamReader(this);
	}
	
	private static class SVMProcessStreamReader implements ProcessStreamReader{
	

			protected SVMClassifier master;
			public SVMProcessStreamReader(SVMClassifier master){
				this.master=master;
			}
			@Override
			public void readLine(String line) {
				Logger log = LoggerFactory.getInstance();
				
				
				String [] tokens = line.split("\\s+");
				int actualLabelIx = tokens.length-3;
				int expectedLabelIx = tokens.length-4;
				//if((ACTUAL_LABEL_IX >= tokens.length) || EXPECTED_LABEL_IX >= tokens.length){
				if((actualLabelIx >= tokens.length) || expectedLabelIx >= tokens.length){
			
					log.log_error("error parsing line: '"+line+"' for classifier: "+master.name);
					return;
				}
				
				String expected = tokens[expectedLabelIx];
				String prediction = tokens[actualLabelIx];
				
				expected = master.parseSVMLabel(expected);
				prediction = master.parseSVMLabel(prediction);
				LabelPair p = new LabelPair(new Label(expected),new Label(prediction));
				master.predictions.add(p);
			}
	}
}
