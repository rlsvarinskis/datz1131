package nlutest.instance;

import java.util.ArrayList;
import java.util.HashMap;

import nlutest.Main;
import nlutest.NounInstance;
import nlutest.Verb;
import nlutest.Word;

public class KeyboardInstance implements NounInstance {
	public static KeyboardInstance KB1 = new KeyboardInstance() {
		@Override
		public boolean hasFeature(String feature) {
			switch (feature) {
			case "input":
			case "keyboard":
			case "bluetooth":
			case "default":
			case "latin":
				return true;
			default:
				return false;
			}
		}
		@Override
		public String toString() {
			return "Latin Bluetooth Keyboard";
		}
	};
	public static KeyboardInstance KB2 = new KeyboardInstance() {
		@Override
		public boolean hasFeature(String feature) {
			switch (feature) {
			case "input":
			case "keyboard":
			case "bluetooth":
			case "cyrillic":
				return true;
			default:
				return false;
			}
		}
		@Override
		public String toString() {
			return "Cyrillic Keyboard";
		}
	};
	public static KeyboardInstance M1 = new KeyboardInstance() {
		@Override
		public boolean hasFeature(String feature) {
			switch (feature) {
			case "mouse":
			case "bluetooth":
				return true;
			default:
				return false;
			}
		}
		@Override
		public String toString() {
			return "Bluetooth Mouse";
		}
	};
	
	private HashMap<String, Verb> actions = new HashMap<>();
	
	//Intents:
	// Switch on
	// Switch off
	// List <devices>
	public KeyboardInstance() {
		actions.put("switch", w -> {
			ArrayList<Word> compound = w.words.get("compound");
			
			if (compound != null && compound.size() > 0) {
				switch (compound.get(0).word) {
				case "off":
					System.out.println("[ ] Intent: Switch off the device <" + this.toString() + ">");
					return;
				case "on":
					System.out.println("[ ] Intent: Switch on the device <" + this.toString() + ">");
					return;
				}
			} else {
				System.out.println("[E] Unknown intent for the device <" + this.toString() + ">");
			}
		});
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
		return false;
	}

	@Override
	public void doAction(Word name) {
		actions.getOrDefault(name.singular, NounInstance.DEFAULT_ACTION).run(name);
	}
}
