package nlutest.instance;

import java.util.ArrayList;
import java.util.HashMap;

import nlutest.Main;
import nlutest.NounInstance;
import nlutest.Verb;
import nlutest.Word;

public class SpeakerInstance implements NounInstance {
	
	public static SpeakerInstance SP1 = new SpeakerInstance() {
		//This is the first speaker, the default speaker, and an audio device
		@Override
		public boolean hasFeature(String feature) {
			switch (feature) {
			case "first":
			case "audio":
			case "default":
				return true;
			default:
				return false;
			}
		}
		@Override
		public String toString() {
			return "Speaker 1";
		}
	};
	public static SpeakerInstance SP2 = new SpeakerInstance() {
		//This is the second speaker, a muted speaker, and an audio device
		@Override
		public boolean hasFeature(String feature) {
			switch (feature) {
			case "second":
			case "audio":
			case "muted":
				return true;
			default:
				return false;
			}
		}
		@Override
		public String toString() {
			return "Speaker 2 [muted]";
		}
	};
	public static SpeakerInstance SP3 = new SpeakerInstance() {
		//This is an audio device, a muted speaker, and a bluetooth speaker/device
		@Override
		public boolean hasFeature(String feature) {
			switch (feature) {
			case "audio":
			case "muted":
			case "bluetooth":
				return true;
			default:
				return false;
			}
		}
		@Override
		public String toString() {
			return "Bluetooth Speaker [muted]";
		}
	};
	
	private HashMap<String, Verb> actions = new HashMap<>();
	
	//Intents:
	// Mute <speaker>
	// Unmute <speaker>
	// Turn up <speaker> by [amount]
	// Turn down <speaker> by [amount]
	// List <speakers>
	public SpeakerInstance() {
		//The mute/unmute intent selects the objects attached to the verb
		actions.put("mute", w -> {
			ArrayList<Word> obl = w.words.get("obj");
			if (obl != null && obl.size() > 0) {
				NounInstance[] speakers = Main.findNoun(obl.get(0));
				
				if (speakers == null) {
					System.out.println("[E] Ambiguous target specified");
				} else {
					System.out.println("[ ] Intent: Mute:");
					for (int i = 0; i < speakers.length; i++) {
						System.out.println("\t" + speakers[i].toString());
					}
				}
			} else {
				System.out.println("[E] Not specified what to mute");
			}
		});
		actions.put("silence", actions.get("mute"));
		
		actions.put("unmute", w -> {
			ArrayList<Word> obl = w.words.get("obj");
			if (obl != null && obl.size() > 0) {
				NounInstance[] speakers = Main.findNoun(obl.get(0));
				
				if (speakers == null) {
					System.out.println("[E] Ambiguous target specified");
				} else {
					System.out.println("[ ] Intent: Unmute:");
					for (int i = 0; i < speakers.length; i++) {
						System.out.println("\t" + speakers[i].toString());
					}
				}
			} else {
				System.out.println("[E] Not specified what to unkute");
			}
		});
		
		//The turn up/down intent selects the compound particle for "up" or "down"
		//and selects the object of the verb as the target
		actions.put("turn", w -> {
			ArrayList<Word> compound = w.words.get("compound");
			ArrayList<Word> obl = w.words.get("obj");
			
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
			
			if (compound != null && compound.size() > 0) {
				switch (compound.get(0).word) {
				case "down":
					System.out.println("[ ] Intent: Turn the speaker <" + this.toString() + "> down by <" + amount + ">");
					return;
				case "up":
					System.out.println("[ ] Intent: Turn the speaker <" + this.toString() + "> up by <" + amount + ">");
					return;
				}
			} else {
				System.out.println("[E] Unknown intent for the speaker <" + this.toString() + ">");
			}
		});
		
		//Show, list, and give are all assumed to be synonyms.
		//They take the target object of the verb and find all nouns that match them.
		actions.put("show", w -> {
			ArrayList<Word> obj = w.words.get("obj");
			if (obj != null && obj.size() > 0) {
				NounInstance[] dev = Main.findNoun(obj.get(0));
				
				if (dev == null) {
					System.out.println("[E] Ambiguous target specified");
				} else {
					System.out.println("[ ] Intent: List objects:");
					for (int i = 0; i < dev.length; i++) {
						System.out.println("\t" + dev[i].toString());
					}
				}
			} else {
				System.out.println("[E] Unknown intent for the computer");
			}
		});
		actions.put("list", actions.get("show"));
		actions.put("give", actions.get("show"));
	}
	
	@Override
	public boolean hasFeature(String feature) {
		switch (feature) {
		case "audio":
			return true;
		default:
			return false;
		}
	}

	@Override
	public void doAction(Word name) {
		actions.getOrDefault(name.singular, NounInstance.DEFAULT_ACTION).run(name);
	}
}
