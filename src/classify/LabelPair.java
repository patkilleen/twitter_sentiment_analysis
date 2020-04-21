package classify;

public class LabelPair {

	private Label realTag;
	private Label predicted;

	/**
	 * Constructor
	 * @param realTag non empty real label
	 * @param predicted non empty predicted label
	 */
	public LabelPair(Label realTag, Label predicted) {
		super();
		if(realTag == null || realTag.isEmpty() || predicted==null || predicted.isEmpty()){
			throw new IllegalArgumentException("cannot create label pair due to empty label: [realTag: "+realTag+",predicted: "+predicted+"]");
		}
		this.realTag = realTag;
		this.predicted = predicted;
	}
	
	public Label getRealLabel() {
		return realTag;
	}
	
	/**
	 * sets the real tag
	 * @param realTag non empty real label
	 */
	public void setRealLabel(Label realTag) {
		if(realTag == null || realTag.isEmpty()){
			throw new IllegalArgumentException("cannot set real label due to empty label: "+realTag);
		}
		this.realTag = realTag;
	}
	
	public Label getPredictedLabel() {
		return predicted;
	}
	
	/**
	 * sets the predicted label
	 * @param predicted non-empty predicted label
	 */
	public void setPredictedLabel(Label predicted) {
		if(predicted == null || predicted.isEmpty()){
			throw new IllegalArgumentException("cannot set predicted label due to empty label: "+predicted);
		}
		this.predicted = predicted;
	}
	
	public String toString(){
		return "[realTag: "+realTag.getValue()+",predicted: "+predicted.getValue()+"]";
	}
	
	
	
}
