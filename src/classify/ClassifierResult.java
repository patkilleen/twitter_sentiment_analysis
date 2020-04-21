package classify;

import java.util.List;

import io.log.Logger;
import io.log.LoggerFactory;

public class ClassifierResult {

	private String classifierName;
	private ConfusionMatrix confusionMatrix;
	
	public ClassifierResult(String classifierName, ConfusionMatrix confusionMatrix) {
		super();
		this.classifierName = classifierName;
		this.confusionMatrix = confusionMatrix;
	}
	public String getClassifierName() {
		return classifierName;
	}
	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}
	public ConfusionMatrix getConfusionMatrix() {
		return confusionMatrix;
	}
	public void setConfusionMatrix(ConfusionMatrix confusionMatrix) {
		this.confusionMatrix = confusionMatrix;
	}
	

	public String toString(){
		String res = "Name: "+classifierName+TweetParser.NEW_LINE;
		res+= confusionMatrix.toString();
		return res;
	}
	
	/**
	 * adds a set of new results to a target set list
	 * @param newResults the new classification results to add to the target
	 * @param targetResultList target set of results to get their ConfusionMatrix's added to
	 */
	public static void addClassifierResults(List<ClassifierResult> newResults,List<ClassifierResult> targetResultList){
		
		
		if( newResults.size() != targetResultList.size()){
			Logger log = LoggerFactory.getInstance();
			log.log_error("could not merge results since size ("+newResults.size() +") is different."
					+ "than the target set's size: "+targetResultList.size());
		}
		
		//merge confusion matrix results (add cells together)
		//this assumes the classifier results are always in same order
		for(int i = 0;i <targetResultList.size();i++){
			ClassifierResult newRes = newResults.get(i);
			ClassifierResult targetRes = targetResultList.get(i);
			
			ConfusionMatrix newCM = newRes.getConfusionMatrix();
			ConfusionMatrix targetCM = targetRes.getConfusionMatrix();
			
			//merge new into old
			targetCM.add(newCM);
		}
	}
}
