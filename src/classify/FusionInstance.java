package classify;

import java.util.ArrayList;
import java.util.List;

public class FusionInstance {

	private Label realTag;
	List<Label> predictions;
	
	/**
	 * Constructor of the fusion instance. The fusion instance is
	 * initially empty (contains no predictions made by classifiers).
	 * 
	 * @param realTag the actual/real label of the instance
	 */
	public FusionInstance(Label realTag) {
		if(realTag == null || realTag.isEmpty()){
			throw new IllegalArgumentException("cannot create fusion instance, real tag was emtpy: "+realTag);
		}
		
		this.realTag = realTag;
		predictions = new ArrayList<Label>(4);
	}
	
	/**
	 * Adds a prediction  to the fusion instance.
	 * @param prediction the prediction of the classifier
	 */
	public void addPrediction(Label prediction){
		if(prediction == null || prediction.isEmpty()){
			throw new IllegalArgumentException("cannot add prediciont fusion instance, prediction was emtpy: "+prediction);
		}
		predictions.add(prediction);
	}

	/**
	 * Returns a tuple representation of the fusion instance in the following
	 * form: prediction1,prediction2,...,predictionn-1,real tag
	 * Where predictioni is the ith label of this instance
	 * 
	 * @return string representation of the instance
	 */
	public String toString(){
		String res = "";
		//create comma-seperated tuple of predictions
		for(Label l : predictions){
			res+=l.getValue();
			res+=",";
		}
		//the real tag is last element
		res+=realTag.getValue();
		return res;
	}
}
