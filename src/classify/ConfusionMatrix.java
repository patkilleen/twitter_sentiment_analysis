package classify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.util.Util;

/**
 * Used to count prediction successes and failures.
 * Recall that in a confusion matrix the rows represent the predicted lable
 * and the columns are the actual/real label/class)
 * 
 * Note that this classed is based on the tutorial found here: https://towardsdatascience.com/multi-class-metrics-made-simple-part-i-precision-and-recall-9250280bddc2
 * @author Not admin
 *
 */
public class ConfusionMatrix {

	/**
	 * unique labels used to build this confusion matrix. This list should have n elements
	 */
	private List<Label> labels;


	/**
	 * maps the labels to their respective index (column or row) in the confusion matrix.
	 */
	private HashMap<String,Integer> indexMap;

	/**
	 * NxN matrix (where N is number of labels) that stores the number of true vs. false predictions based on class/label
	 */
	private List<List<Integer>> matrix;


	/**
	 * Constructor
	 * @param labels the unique list of labels that are used to create confusion matrix
	 */
	public ConfusionMatrix(List<Label> labels ) {

		init(labels);
	}

	/**
	 * Empty constructor that doesn't initialize the matrix.
	 * Subclasses are responsible for initializing instances of
	 * this class that are built using this constructor.
	 */
	protected ConfusionMatrix(){

	}

	protected void init(List<Label> labels){
		//validity check
		if(Util.isNullOrEmpty(labels)){
			throw new IllegalArgumentException("cannot create a confusion matrix due to non-existing classes/labels");
		}

		//make sure lables are unique
		if(!Util.allElementsUnique(labels)){
			throw new IllegalArgumentException("cannot create a confusion matrix due to duplicate classes/labels. The labels need to be unique.");
		}

		//make a deep copy of the label list to make 
		//sure no other class manipulates the content of the 
		//list of labels
		this.labels=new ArrayList<Label>(labels);

		//create and populate map that store indexs of labels/classes
		indexMap = new HashMap<String,Integer>();
		for(int i=0;i<this.size();i++){
			Label label = labels.get(i);
			String key = label.getValue();
			Integer index = new Integer(i);
			indexMap.put(key,index);
		}

		//populate and create matrix cells (initiatlly counts are 0)
		matrix = new ArrayList<List<Integer>>(this.size());

		for(int i = 0;i < this.size();i++){
			List<Integer> row = new ArrayList<Integer>(this.size());
			matrix.add(row);
			//populate the row's cells
			for(int j=0;j < labels.size();j++){
				row.add(new Integer(0));
			}
		}
	}
	/**
	 * returns number of rows and columns.
	 * @return number of rows/columns in matrix
	 */
	public int size(){
		return labels==null ? 0 : labels.size();
	}

	/**
	 * 
	 * returns index associated to a label
	 * @param l label to fetch index for
	 * @return index of the label
	 */
	private int resolveIndex(Label l){
		if(l == null){
			throw new NullPointerException("cannot resolve index of ConfusionMatrix of null label/class (null Label object)");
		}

		String key = l.getValue();

		if(l.isEmpty()){
			throw new IllegalArgumentException("cannot resolve index of ConfusionMatrix of emtpy label/class value");
		}

		if(!indexMap.containsKey(key)){
			throw new IllegalArgumentException("could not resolve ConfusionMatrix index for unknown label: "+key);
		}
		return indexMap.get(key);

	}


	/**
	 * Retreives the integer object associated to the given label pair
	 * The integer returned represents the frequency that such a pair occured.
	 * @param prediction predicted label
	 * @param real real/actual label
	 * @throws NullPointerException when a provided label is null
	 * @throws IllegalArgumentException when a provided label is not found in the confusion matrix
	 * @return the frequency that this prediction pair has been counted
	 */
	public Integer getFrequency(Label prediction, Label real){

		int rowIx = resolveIndex(prediction);
		int colIx = resolveIndex(real);


		return matrix.get(rowIx).get(colIx);
	}

	/**
	 * Increments the appropriate cell in confusion matrix
	 * associated to given label pair.
	 * @param prediction predicted label
	 * @param real real/actual label
	 */
	public void incrementCell(Label prediction, Label real){
		int rowIx = resolveIndex(prediction);
		int colIx = resolveIndex(real);

		Integer cell = matrix.get(rowIx).get(colIx);
		Integer newValue = cell+1;
		matrix.get(rowIx).set(colIx, newValue);

	}

	
	
	/**
	 * Computes the sum of all frequencies in a specified column
	 * @param l label used to identify the column to sum
	 * @return sum of target column's cells
	 */
	protected int sumColumn(Label l){
		int sum = 0;

		int colIx = resolveIndex(l);

		//iterate all cells in the column
		for(int i = 0;i<size();i++){
			Integer cell = matrix.get(i).get(colIx);
			sum+=cell;
		}
		return sum;
	}


	/**
	 * Computes the sum of all frequencies in a specified row
	 * @param l label used to identify the row to sum
	 * @return sum of target row's cells
	 */
	protected int sumRow(Label l){
		int sum = 0;

		int rowIx = resolveIndex(l);

		//iterate all cells in the column
		for(int i = 0;i<size();i++){
			Integer cell = matrix.get(rowIx).get(i);
			sum+=cell;
		}
		return sum;
	}

	/**
	 * Returns the number of true positive readings found by this classifier
	 * @param classLabel the label of the class to fetch the true positive count for
	 * @return the true positive count
	 */
	public int computeTP(Label classLabel){
		return getFrequency(classLabel,classLabel);
	}

	/**
	 * Returns the number of false positive readings found by this classifier
	 * @param classLabel the label of the class to fetch the false positive count for
	 * @return the false positive count
	 */
	public int computeFP(Label classLabel){

		int tp = this.computeTP(classLabel);
		//the total number of predictions that guess 'classLabel'
		int totalPredictions = this.sumRow(classLabel);

		return totalPredictions - tp;

	}


	/**
	 * Returns the number of false negative readings found by this classifier
	 * @param classLabel the label of the class to fetch the false negative count for
	 * @return the false negative count
	 */
	public int computeFN(Label classLabel){

		int tp = this.computeTP(classLabel);
		//the total number of real labels of 'classLabel'
		int totalReal = this.sumColumn(classLabel);

		return totalReal - tp;

	}

	/**
	 * Returns the number of true negative readings found by this classifier
	 * @param classLabel the label of the class to fetch the true negative count for
	 * @return the true negative count
	 */
	public int computeTN(Label classLabel){

		/*
		 * TODO: optimize this loggic
		 */
		//count the totla number of predictions
		int total = sumAll();



		int tp = this.computeTP(classLabel);
		int fp = this.computeFP(classLabel);
		int fn = this.computeFN(classLabel);

		return total - tp - fp - fn;
	}


	/**
	 * summarizes all frequencies found in each cell
	 * @return total counts of each cell
	 */
	protected int sumAll() {
		int total = 0;
		//iterate all rows
		for(int i = 0;i<size();i++){
			//itreate all columns
			for(int j = 0;j<size();j++){
				Integer cell = matrix.get(i).get(j);
				total+=cell;
			}
		}
		return total;
	}

	/**
	 * Computes the recall using confusion matrix cell frequencies.
	 * @param classLabel label of class to comptue recall for
	 * @return recall of desired label
	 */
	public double computeRecall(Label classLabel){
		double tp = this.computeTP(classLabel);
		//total number of instances of class 'classLabel'
		double totalReal = this.sumColumn(classLabel);
		return tp/totalReal;
	}

	/**
	 * Computes the precision using confusion matrix cell frequencies.
	 * @param classLabel label of class to compute precision for
	 * @return precision of desired label
	 */
	public double computePrecision(Label classLabel){
		double tp = this.computeTP(classLabel);
		//total number of predicted instances of class 'classLabel'
		double totalPredicted = this.sumRow(classLabel);
		return tp/totalPredicted;
	}

	/**
	 * Computes the F1 score of chosen label
	 * @param classLabel label to compute f1 score for
	 * @return the f1score of target label
	 */
	public double computeF1Score(Label classLabel){
		double r = this.computeRecall(classLabel);
		double p = this.computePrecision(classLabel);
		return (2.0 * p * r)/(p+r);
	}

	/**
	 * Computes the macro-averaged F1 score.
	 * @return macro f1 score
	 */
	public double computeMacroF1(){
		double res = 0;
		//iterate all labels to compute their individual f1 scores
		for(Label l : labels){
			res += this.computeF1Score(l);
		}

		//take the average
		res = res/((double)labels.size());
		return res;

	}

	/**
	 * Computes the macro-averaged precision
	 * @return macro precision
	 */
	public double computeMacroPrecision(){
		double res = 0;
		//iterate all labels to compute their individual precision values
		for(Label l : labels){
			res += this.computePrecision(l);
		}

		//take the average
		res = res/((double)labels.size());
		return res;

	}

	/**
	 * Computes the macro-averaged recall
	 * @return macro recall
	 */
	public double computeMacroRecall(){
		double res = 0;
		//iterate all labels to compute their individual recall values
		for(Label l : labels){
			res += this.computeRecall(l);
		}

		//take the average
		res = res/((double)labels.size());
		return res;

	}

	/**
	 * Computes the accuracy (aka micro f1 score)
	 * @return accuracy
	 */
	public double computeAccuracy(){

		//count the totla number of predictions
		double total = sumAll();

		double truePredictions = 0;
		//iterate all correct guesses
		for(Label l : labels){
			truePredictions += this.computeTP(l);
			
		}

		return truePredictions/(total);

	}
	
	
	/**
	 * Adds a confusion matrix's cell to this confusion matrix's cells 
	 * @param other the confusion matrix to add to this matrix's cells
	 * @return
	 */
	public void add(ConfusionMatrix other){
		
		//the confusion matrix must  be of same dimesions
		if(other.size() != this.size()){
			throw new IllegalArgumentException("cannot add confusino matrix's together. The other confusion matrix is of different dimensions");
		}
		
		//make sure the labels match
		for(int i = 0; i < this.size(); i++){
			Label lable = this.labels.get(i);
			Label otherLabel = other.labels.get(i);
			
			if(!lable.equals(otherLabel)){
				throw new IllegalArgumentException("cannot add confusino matrix's together. The other confusion matrix has different labels");
			}
		}
		
		//iterate the labels and add the cells
		for(Label row : this.labels){
			for(Label col : this.labels){
				int rowIx = resolveIndex(row);
				int colIx = resolveIndex(col);

				Integer cell = matrix.get(rowIx).get(colIx);
				Integer otherCell = other.matrix.get(rowIx).get(colIx);
				
				Integer newValue = cell+otherCell;
				matrix.get(rowIx).set(colIx, newValue);
			}	
		}//end iterate labels
			
	}//end add
	
	public String toString(){
		String res = "";
		//iterate each class to summarize that label's results
				for(Label l: this.labels){
					res+="Class: "+l.getValue()
					+", TP: "+computeTP(l)
					+", FP: "+computeFP(l)
					+", FN: "+computeFN(l)
					+", TN: "+computeFN(l)
					+", recall: "+computeRecall(l)
					+", precision: "+computePrecision(l)
					+", f1-score: "+computeF1Score(l) + TweetParser.NEW_LINE;
				}

				res+="macro recall: "+computeMacroRecall()
				+" macro precision: "+computeMacroPrecision()
				+" macro f1-score: "+computeMacroF1() + TweetParser.NEW_LINE;
				return res;
	}
}
