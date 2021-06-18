package nlutest.instance;

import java.util.ArrayList;
import java.util.HashMap;

import nlutest.Main;
import nlutest.NounInstance;
import nlutest.Verb;
import nlutest.Word;

public class VolumeInstance implements NounInstance {
	public static VolumeInstance VOLUME = new VolumeInstance() {
		@Override
		public String toString() {
			return "Volume Property (20%)";
		}
	};
	
	private HashMap<String, Verb> actions = new HashMap<>();
	
	//Intents:
	// Increase the volume [by amount]
	// Decrease the volume [by amount]
	public VolumeInstance() {
		actions.put("increase", w -> {
			ArrayList<Word> obl = w.words.get("obl");
			
			String amount = "default amount";
			
			if (obl != null && obl.size() > 0) {
				Word percent = obl.get(0);
				if (percent.singular.equalsIgnoreCase("%") || percent.singular.equalsIgnoreCase("percent")) {
					ArrayList<Word> nummod = percent.words.get("nummod");
					if (nummod != null && nummod.size() > 0) {
						Word num = nummod.get(0);
						amount = num.singular + " %";
					}
				} else {
					System.out.println("[E] Unknown unit: " + percent.singular);
					return;
				}
			}
			System.out.println("[ ] Intent: Increase the volume by <" + amount + ">");
		});
		actions.put("decrease", w -> {
			ArrayList<Word> obl = w.words.get("obl");
			
			String amount = "default amount";
			
			if (obl != null && obl.size() > 0) {
				Word percent = obl.get(0);
				if (percent.singular.equalsIgnoreCase("%") || percent.singular.equalsIgnoreCase("percent")) {
					ArrayList<Word> nummod = percent.words.get("nummod");
					if (nummod != null && nummod.size() > 0) {
						Word num = nummod.get(0);
						amount = num.singular + " %";
					}
				} else {
					System.out.println("[E] Unknown unit: " + percent.singular);
					return;
				}
			}
			System.out.println("[ ] Intent: Decrease the volume by <" + amount + ">");
		});
		
		actions.put("turn", w -> {
			ArrayList<Word> compound = w.words.get("compound");
			ArrayList<Word> obl = w.words.get("obl");
			
			String amount = "default amount";
			
			if (obl != null && obl.size() > 0) {
				Word percent = obl.get(0);
				if (percent.singular.equalsIgnoreCase("%") || percent.singular.equalsIgnoreCase("percent")) {
					ArrayList<Word> nummod = percent.words.get("nummod");
					if (nummod != null && nummod.size() > 0) {
						Word num = nummod.get(0);
						amount = num.singular + " %";
					}
				} else {
					System.out.println("[E] Unknown unit: " + percent.singular);
				}
			}
			
			if (compound != null && compound.size() > 0) {
				switch (compound.get(0).word) {
				case "down":
					System.out.println("[ ] Intent: Turn the volume down by <" + amount + ">");
					return;
				case "up":
					System.out.println("[ ] Intent: Turn the volume up by <" + amount + ">");
					return;
				}
			} else {
				System.out.println("[E] Turn where? Left?");
			}
		});
	}
	
	@Override
	public boolean hasFeature(String feature) {
		return false;
	}

	@Override
	public void doAction(Word name) {
		actions.getOrDefault(name.singular, NounInstance.DEFAULT_ACTION).run(name);
	}
}
