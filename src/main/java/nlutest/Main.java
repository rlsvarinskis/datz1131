package nlutest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import edu.stanford.nlp.simple.Sentence;
import nlutest.instance.KeyboardInstance;
import nlutest.instance.SelfInstance;
import nlutest.instance.SpeakerInstance;
import nlutest.instance.VolumeInstance;

public class Main {
	//This hashmap maps each "noun type" to a list of "noun instances"
	public static HashMap<String, NounCategory> nounTypes = new HashMap<>();
	
	static {
		//No object means the computer must do it to itself (connect [the computer] to the device)
		nounTypes.put(null, new NounCategory() {
			@Override
			public NounInstance[] getObjects(Word w) {
				return new NounInstance[] {SelfInstance.self};
			}
		});
		nounTypes.put("computer", new NounCategory() {
			@Override
			public NounInstance[] getObjects(Word w) {
				return new NounInstance[] {SelfInstance.self};
			}
		});
		
		nounTypes.put("speaker", new NounCategory() {
			@Override
			public NounInstance[] getObjects(Word w) {
				return new NounInstance[] {SpeakerInstance.SP1, SpeakerInstance.SP2, SpeakerInstance.SP3};
			}
		});
		
		nounTypes.put("device", new NounCategory() {
			@Override
			public NounInstance[] getObjects(Word w) {
				return new NounInstance[] {SpeakerInstance.SP1, SpeakerInstance.SP2, SpeakerInstance.SP3, KeyboardInstance.KB1, KeyboardInstance.KB2, KeyboardInstance.M1};
			}
		});
		nounTypes.put("keyboard", new NounCategory() {
			@Override
			public NounInstance[] getObjects(Word w) {
				return new NounInstance[] {KeyboardInstance.KB1, KeyboardInstance.KB2};
			}
		});
		nounTypes.put("mouse", new NounCategory() {
			@Override
			public NounInstance[] getObjects(Word w) {
				return new NounInstance[] {KeyboardInstance.M1};
			}
		});
		nounTypes.put("volume", new NounCategory() {
			@Override
			public NounInstance[] getObjects(Word w) {
				return new NounInstance[] {VolumeInstance.VOLUME};
			}
		});
	}
	
	public static void main(String[] args) {
		Scanner inp = new Scanner(System.in);
		
		//Read every line of input
		while (inp.hasNextLine()) {
			String line = inp.nextLine();
			
			Sentence s = new Sentence(line);
			System.out.println("Input: " + line);
			parse(s);
		}
		
		inp.close();
	}

	//Convert Sentence into a tree data structure
	static Word convertSentence(Sentence s) {
		List<Optional<String>> dep = s.incomingDependencyLabels();
		List<Optional<Integer>> par = s.governors();
		
		Word[] words = new Word[dep.size()];
		
		for (int i = 0; i < dep.size(); i++) {
			words[i] = new Word(s.word(i).toLowerCase());
			words[i].singular = s.lemma(i).toLowerCase();
		}
		int root = -1;
		for (int i = 0; i < dep.size(); i++ ) {
			System.out.print(s.word(i) + " (" + s.lemma(i) + ")" + ": ");
			if (dep.get(i).isPresent() && par.get(i).isPresent()) {
				int parent = par.get(i).get();
				String dependency = dep.get(i).get();
				
				if (parent == -1) {
					root = i;
				} else {
					String simpleDep = dependency.split(":")[0];
					ArrayList<Word> children = words[parent].words.get(simpleDep);
					if (children == null) {
						children = new ArrayList<>();
						words[parent].words.put(simpleDep, children);
					}
					children.add(words[i]);
				}
				words[i].relToPar = dependency;
				
				System.out.print(dependency + " " + parent);
			}
			System.out.println();
		}
		
		System.out.println();
		
		if (root == -1) {
			return null;
		}
		
		return words[root];
	}
	
	public static void parse(Sentence s) {
		//Dependency types:
		// nsubj, csubj - nominal/clausal subject (who is doing it)
		// obj, iobj - main object and indirect object (who is it being done upon)
		// ccomp, xcomp - object which is a clause, either a full sentence, or a clause without a subject
		
		// obl - adverbial noun (modifies a verb)
		// advmod - adverb (modifies a verb or other modifier)
		// advcl - temporal clause, consequence, condition, purpose, etc.
		
		// amod - adjectival modifier
		// nmod, acl - modifies a noun
		// appos - explains a noun immediately after it (He, the guy, ...)
		// nummod - modifies noun with quantity
		
		// conj - list
		// cc - connectors of list items
		
		// fixed - multiple word expression with fixed order (as well as)
		// flat - multiple word expression without hierarchy (President Barack Obama)
		// compound - multiple word expressions with hierarchy (ice cream van)
		
		// list - not a real sentence
		// parataxis -
		
		// orphan - equivalent to root, but attached to the single sentence root
		// goeswith - typos where a word is separated (with out)
		// reparandum - verbal speech repair (go past the secon- the third block)
		
		// det - determiner
		// clf - measure words for uncountable noun (three pieces of candy)
		// case - words that change the case of a word (to him, out of it, etc)
		
		// aux - 
		// cop - 
		// mark - combines clauses
		
		// punct - punctuation
		// root - root
		// dep - words whose dependency is not clear
		
		// vocative - calling someone with their name
		// expl - existential there, or it (there is, it is)
		// dislocated - spoken language dislocation of (for example) an object
		// discourse - interjections or stuff like emojis
		
		Word root = convertSentence(s);
		if (root == null) {
			System.out.println("[E] I didn't understand that sentence");
			return;
		}
		
		//Assume the root is a verb. Find the object.
		ArrayList<Word> w1 = root.words.get("obj");
		
		NounInstance[] objects;
		
		if (w1 != null && w1.size() > 0) {
			Word obj = w1.get(0);
			objects = findNoun(obj);
		} else {
			//If there is no object, assume it is the computer itself
			objects = nounTypes.get("computer").getObjects(null);
		}
		
		if (objects == null || objects.length == 0) {
			//If no object matches
			System.out.println("[E] Target not found");
		} else {
			//Execute the action
			//However, it wasn't clear here whether the action should be done on each object itself or whether "the computer" should do it on each object
			//If I write "List all devices", it seems like "the computer" itself should implement the intent, and each device doesn't matter
			//If I write "Switch off all devices", each device itself should implement the intent, since "switch off" could mean different things for different devices
			objects[0].doAction(root);
		}
	}
	
	//Convert a noun with determiners and modifiers into a list of instances that match the description
	public static NounInstance[] findNoun(Word noun) {
		NounCategory category = nounTypes.get(noun.singular);
		if (category == null) {
			System.out.println("Noun category " + noun.singular + " not found");
			return null;
		}
		NounInstance[] categories = category.getObjects(noun);
		
		ArrayList<NounInstance> result = new ArrayList<>();
		for (int i = 0; i < categories.length; i++) {
			result.add(categories[i]);
		}
		
		//Filter all objects that do not match the given modifiers
		ArrayList<Word> modifiers = noun.words.get("amod");
		if (modifiers != null) {
			for (int i = 0; i < modifiers.size(); i++) {
				Word modifier = modifiers.get(i);
				System.out.println(modifier.singular);
				result.removeIf(filter -> !filter.hasFeature(modifier.singular));
			}
		}
		
		//The determiner further filters the objects
		ArrayList<Word> determiners = noun.words.get("det");
		if (determiners != null && determiners.size() > 0) {
			Word det = determiners.get(0);
			
			switch (det.singular) {
			case "each":
			case "every":
			case "all":
				break;
			case "a":
				while (result.size() > 1) {
					result.remove(result.size() - 1);
				}
				break;
			case "some":
				while (result.size() > 3) {
					result.remove(result.size() - 1);
				}
				break;
			case "the":
				//A bad way of checking if the noun is singular
				//If it is, then "the" must refer to only one
				if (noun.singular.equalsIgnoreCase(noun.word)) {
					if (result.size() > 1) {
						result.clear();
					}
				}
				break;
			}
		}
		
		if (result.isEmpty()) {
			return null;
		}
		
		NounInstance[] res = new NounInstance[result.size()];
		for (int i = 0; i < result.size(); i++) {
			res[i] = result.get(i);
		}
		
		return res;
	}
}
