package classify;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordPipelineFactory {

	private static StanfordCoreNLP PIPELINE = null;
	
	public static synchronized StanfordCoreNLP getPipeline(){
		if(PIPELINE == null){
			Properties props = new Properties();
			
			//this set options for specific parsers and sentiment annotations
			props.setProperty(NLPBasedClassifier.ANNOTATORS_KEY, NLPBasedClassifier.ANNOTATORS);

			
			PrintStream stdErr = System.err;
			//disable the standard error stream (don't need lots of verbose from NLP libraries) temporarily
			System.setErr(new PrintStream(new OutputStream() {
			    public void write(int b) {
			    }
			}));
			
			PIPELINE = new StanfordCoreNLP(props);
			System.setErr(stdErr);
			
		}
		return PIPELINE;
	}
}
