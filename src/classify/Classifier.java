package classify;

import java.util.ArrayList;
import java.util.List;

import io.ProcessRunner;
import io.ProcessStreamReader;
import io.log.Logger;
import io.log.LoggerFactory;

public class Classifier extends AbstractClassifier{

	public static final String POSITIVE_LABEL_VALUE = "4";
	public static final String NEUTRAL_LABEL_VALUE = "2";
	public static final String NEGATIVE_LABEL_VALUE = "0";
	
	public static final int ACTUAL_LABEL_IX = 1;
	public static final int EXPECTED_LABEL_IX = 2;
	
	protected String command;
	
	protected Classifier(String name,String command) {
		super(name);
		this.command=command;
		
	}
	
	/**
	 * Hook to be implemented by subclasses if they
	 * wish to implement the label parsing differently
	 * @return process stream reader used to parse labels
	 */
	protected ProcessStreamReader getStreamReaderHook(){
		return new ClassifierSlave(this);
	}
	@Override
	public void trainAndClassify(){
		Logger log = LoggerFactory.getInstance();
		log.log_debug("Training classifier, about to exectue: "+command);
		ProcessRunner runner = new ProcessRunner(getStreamReaderHook());
		int rc = runner.exec(command);
		
		if(rc != 0){
			throw new IllegalStateException("failed to run the process: "+this.toString());
		}
		
		log.log_debug("finished executing: "+this.name+": "+this.command);
	}
	


	/**
	 * helpler class to parse the classifier process' output
	 * into prediction pairs
	 * @author Patrick Killeen
	 *
	 */
	public static class ClassifierSlave implements ProcessStreamReader{
		Classifier master;
		
		public ClassifierSlave(Classifier master) {
			this.master=master;
			
		}

		/**
		 * Parses the output of the classifier process' outputstream into 
		 * LabelPairs, and adds the pairs to the Classifer's prediction list
		 */
		@Override
		public void readLine(String line) {
		
			String [] tokens = line.split("\\s+");
			int actualLabelIx = tokens.length-3;
			int expectedLabelIx = tokens.length-4;
			//if((ACTUAL_LABEL_IX >= tokens.length) || EXPECTED_LABEL_IX >= tokens.length){
			if((actualLabelIx >= tokens.length) || expectedLabelIx >= tokens.length){
				Logger log = LoggerFactory.getInstance();
				log.log_error("error parsing line: '"+line+"' for classifier: "+master.name);
				return;
			}
			LabelPair p = new LabelPair(new Label(tokens[expectedLabelIx]),new Label(tokens[actualLabelIx]));
			master.predictions.add(p);
		}
		
	}


	@Override
	public String toString() {
		return "Classifier [name=" + name + ", command=" + command + "]";
	}
	
	
}
