package classify;

public class Label {

	private String value;
	
	public Label(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Checks to make sure the value of the label is empty.
	 * @return true if the value is either null or an empty string, and false otherwise.
	 */
	public boolean isEmpty(){
		if(value==null || value.equals("")){
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Label other = (Label) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Label [value=" + value + "]";
	}
	
	
	
}
