package generator;

public enum GraphType {
	
	MARKOV("Markov"), BAYESIAN("Bayesian");
	
	private String type;

	GraphType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
	
	public static GraphType fromString(String type) {
		if (type != null) {
			for (GraphType b : GraphType.values()) {
				if (type.equalsIgnoreCase(b.type)) {
					return b;
				}
			}
		}
		return null;
	}
}
