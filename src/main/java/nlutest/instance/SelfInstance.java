package nlutest.instance;

import java.util.ArrayList;
import java.util.HashMap;

import nlutest.Main;
import nlutest.NounInstance;
import nlutest.Verb;
import nlutest.Word;

//This class represents the computer itself
//"Connect to a device" is equivalent to "Connect [the computer] to a device"
public class SelfInstance implements NounInstance {
	public static SelfInstance self = new SelfInstance();
	
	private static HashMap<String, Verb> actions = new HashMap<>();
	
	//Intents:
	// Connect <device>
	// Disconnect <device>
	// Turn on the computer
	// Turn off the computer
	static {
		//The connect/disconnect intent selects all oblique nominal nouns attached to the verb
		//This is not perfect, since it assumes all oblique nominals have the case "to"
		actions.put("connect", w -> {
			ArrayList<Word> obl = w.words.get("obl");
			if (obl != null && obl.size() > 0) {
				NounInstance[] connections = Main.findNoun(obl.get(0));
				
				if (connections == null) {
					System.out.println("[E] Ambiguous target specified");
				} else {
					System.out.println("[ ] Intent: Connect to:");
					for (int i = 0; i < connections.length; i++) {
						System.out.println("\t" + connections[i].toString());
					}
				}
			} else {
				System.out.println("[E] Not specified what to connect to");
			}
		});
		actions.put("disconnect", w -> {
			ArrayList<Word> obl = w.words.get("obl");
			if (obl != null && obl.size() > 0) {
				NounInstance[] connections = Main.findNoun(obl.get(0));
				
				if (connections == null) {
					System.out.println("[E] Ambiguous target specified");
				} else {
					System.out.println("[ ] Intent: Disconnect from:");
					for (int i = 0; i < connections.length; i++) {
						System.out.println("\t" + connections[i].toString());
					}
				}
			} else {
				System.out.println("[E] Not specified what to disconnect from");
			}
		});
		
		//Turn itself is an unknown intent, but it can be part of a compound word (turn on/turn off)
		actions.put("turn", w -> {
			ArrayList<Word> compound = w.words.get("compound");
			if (compound != null && compound.size() > 0) {
				switch (compound.get(0).word) {
				case "off":
					//Turn off the computer intent
					System.out.println("[ ] Intent: Turn off the computer");
					return;
				case "on":
					//Turn on the computer intent
					System.out.println("[ ] Intent: Turn on the computer");
					return;
				}
			} else {
				System.out.println("[E] Unknown intent for the computer");
			}
		});
	}
	
	//There aren't any features needed for this, since it shouldn't be referred to
	@Override
	public boolean hasFeature(String feature) {
		return false;
	}

	@Override
	public void doAction(Word action) {
		actions.getOrDefault(action.singular, NounInstance.DEFAULT_ACTION).run(action);
	}
}
