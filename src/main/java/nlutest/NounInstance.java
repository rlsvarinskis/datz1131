package nlutest;

public interface NounInstance {
	public static Verb DEFAULT_ACTION = w -> {
		System.out.println("[E] Unknown intent: " + w.singular);
	};
	
	//Each noun belongs to some "sets", and this function checks if this noun is part of that set
	public boolean hasFeature(String feature);
	public void doAction(Word action);
}
