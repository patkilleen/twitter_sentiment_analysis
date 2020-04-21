package classify;

public class LabeledInstance {

	/**
	 * sample/object that is labeled (an instance from a training or testing dataset, for example)
	 */
	private Object instance;
	
	/**
	 * The label/tag of the instance. 
	 */
	private Label label;

	
	
	public LabeledInstance(Object instance, Label label) {
		super();
		this.instance = instance;
		this.label = label;
	}

	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}
	
	

}
