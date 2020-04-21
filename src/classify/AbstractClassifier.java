package classify;

import java.util.ArrayList;
import java.util.List;

import io.IConfig;

public abstract class AbstractClassifier {

	public static final int PREDICTION_BUFFER_SIZE = 32;


	protected String name;
	protected List<LabelPair> predictions;

	protected AbstractClassifier(String name) {
		predictions = new ArrayList<LabelPair>(PREDICTION_BUFFER_SIZE);
		this.name = name;
	}

	public Label getRealLabel(int i){
		return predictions.get(i).getRealLabel();
	}

	public Label getPredictedLabel(int i){
		return predictions.get(i).getPredictedLabel();
	}

	//note that the NLPbased and svm will need overwrite this and perform differently
	public abstract void trainAndClassify();

	/**
	 * Returns the LabelPair at a given index
	 * @param i index of the desired LabelPair
	 * @return the LabelPair at index i
	 */
	public LabelPair getPredictionPair(int i){
		return predictions.get(i);
	}

	/**
	 * Returns the number of instances classified. 
	 * @return Number of instances classified, or zero if not trained and classified yet.
	 */
	public int size(){
		return predictions.size();
	}


	/**
	 * Returns a string that contains all performance measures of the classifier.
	 * for more info on logic see
	 * https://towardsdatascience.com/multi-class-metrics-made-simple-part-i-precision-and-recall-9250280bddc2 
	 * https://datascience.stackexchange.com/questions/15989/micro-average-vs-macro-average-performance-in-a-multiclass-classification-settin
	 * @return a string that contains all performance measures of the classifier.
	 */
	public String stringifyPerformance(List<Label> labelSet){

		//get unique set of labels (some expereiments may only have neg and pos, while others include neutral, pos, and neg)
		//List<Label> labels = findUniqueLabels(this.predictions);

		ConfusionMatrix cm = new ConfusionMatrix(labelSet);
		//Iterate all the predictions to build confusion matrix
		for(LabelPair p : this.predictions){
			cm.incrementCell(p.getPredictedLabel(), p.getRealLabel());
		}

		String res = "Name: "+this.name+TweetParser.NEW_LINE;

		//iterate each class to summarize that label's results
		for(Label l: labelSet){
			res+="Class: "+l.getValue()
			+", TP: "+cm.computeTP(l)
			+", FP: "+cm.computeFP(l)
			+", FN: "+cm.computeFN(l)
			+", recall: "+cm.computeRecall(l)
			+", precision: "+cm.computePrecision(l)
			+", f1-score: "+cm.computeF1Score(l) + TweetParser.NEW_LINE;
		}

		res+="macro recall: "+cm.computeMacroRecall()
		+"macro precision: "+cm.computeMacroPrecision()
		+"macro f1-score: "+cm.computeMacroF1();

		return res;
	}

	
	/**
	 * converts the predictions to a ClassifierResult 
	 * @param labelSet unique set of labels found among predictions
	 * @return a ClassifierResult object when the classifier has been trained and null otherwise.
	 */
	public ClassifierResult computeClassifierResult(List<Label> labelSet){

		if(this.predictions.isEmpty()){
			return null;
		}
		
		ConfusionMatrix cm = new ConfusionMatrix(labelSet);
		//Iterate all the predictions to build confusion matrix
		for(LabelPair p : this.predictions){
			cm.incrementCell(p.getPredictedLabel(), p.getRealLabel());
		}
		
		return new ClassifierResult(this.name,cm);
	}
	/**
	 * Finds all unique real tags in a list of label pairs
	 * @param pairs pair of prediction and real tags
	 * @return unique real labels found in the list, or null if pairs is null

	protected List<Label> findUniqueLabels(List<LabelPair> pairs) {

		if(pairs == null){
			return null;
		}

		if(pairs.size() == 0){
			return new ArrayList<Label>(0);
		}

		 List<Label> uniqueRes = new ArrayList<Label>(pairs.size());

		 //iterate the pairs and add the real tag label to result if it hasn't been added yet
		 //note that the predictions may not span the entire label set
		 //so checking the real tag labels will span accross all possible 
		 //the unique labels
		 for(LabelPair p: pairs){
			 Label real = p.getRealLabel();

		     if(!uniqueRes.contains(real)){
		      	uniqueRes.add(real);
		     }
		 }
		return uniqueRes;
	}

	 */

	public static List<Label> getLabelSet(IConfig config){
		//set set of all possible labels (some datasets may not have neutral tweets)
		List<Label> labelSet = new ArrayList<Label>(3);
		List<String> labelValues = config.getProperties(IConfig.PROPERTY_LABEL_SET);
		for(String s : labelValues){
			labelSet.add(new Label(s));
		}	
		return labelSet;
	}

	@Override
	public String toString() {
		return "Classifier [name=" + name + "]";
	}


}
