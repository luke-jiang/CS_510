import java.util.*;
import java.io.*;
import java.io.FileNotFoundException;


public class bugFinder {
	// TODO: incorporate into args of main
	public static double T_CONFIDENCE = 0.65;
	public static int T_SUPPORT = 3;

	// a map representation of the call graph, key is scope name, value is the list of functions called
	public static Map<String, HashSet<String>> scopeMap;
	// a map counting the number of appearance of each function
	public static Map<String, Integer> funcCount;
	// a map counting the number of appearance of each pair of functions
	public static Map<String, Integer> funcPairCount;
	// a list of likely invariants
	public static Map<String, HashSet<String>> likelyInvariants;

	// emit one line of error message
	public static void emit(String fun, String otherfun, String scope, int support, double confidence) {
		System.out.print("bug: " + fun + " in " + scope + ", ");
		if (fun.compareTo(otherfun) < 0) {
			System.out.print("pair: (" + fun + ", " + otherfun + "), ");
		} else {
			System.out.print("pair: (" + otherfun + ", " + fun + "), ");
		}
		System.out.print("support: " + support + ", ");
		System.out.print("confidence: ");
		System.out.printf("%.2f", confidence * 100); 
		System.out.println("%");
	}

	public static void parseGraph(String path) {
		// first, we construct the scope map
		scopeMap = new HashMap<>();

		// read form file
		File openFile = new File(path);
		Scanner scanner;
		try {
			scanner = new Scanner(openFile);
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			return;
		}

		boolean newScope = true;
		String scope = "";
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			// if current is an empty line, the next line will be a new scope
			if (line.length() == 0) {
				newScope = true;
				continue;
			}

			String[] tokens = line.split("\'");
			// if null node, token should be length 1
			if (tokens.length != 1) {
				if (newScope) {
					scope = tokens[1];
					if (!scopeMap.containsKey(scope))
						scopeMap.put(scope, new HashSet<String>());
					newScope = false;

				}else {
					// this must be the functions of the scope
					HashSet<String> funcSet = scopeMap.get(scope);
					funcSet.add(tokens[1]);
					scopeMap.put(scope, funcSet);

				}
			}
		}
		scanner.close();

		// now let's count the appearance of functions
		funcCount = new HashMap<>();

		for (String s: scopeMap.keySet()) {
			for (String func: scopeMap.get(s)) {
				if (funcCount.containsKey(func)) {
					int currentCount = funcCount.get(func);
					funcCount.put(func,++currentCount);
				}else{
					funcCount.put(func, 1);
				}
			}
		}

		// now let's count the appearance of each pair of functions
		List<String> functions = new ArrayList<>(funcCount.keySet());
		funcPairCount = new HashMap<>();

		for (int i = 0; i < functions.size();i++) {
			for (int j = i+1;j < functions.size();j++) {
				String f1 = functions.get(i);
				String f2 = functions.get(j);


				String pair = makePair(f1,f2);
				if (funcPairCount.containsKey(pair)) {
					int currentCount = funcCount.get(pair);
					funcPairCount.put(pair,++currentCount);
				}else{
					funcPairCount.put(pair,1);
				}

			}
		}
	}
	private static String makePair(String f1, String f2) {
		// we represent a pair of functions using string
		// the two functions are separated by a space
		String pair = "";
		// we compare f1 and f2, we always put the smaller one at the beginning
		if (f1.compareTo(f2) < 0){
			pair = f1 + " "+ f2;
		}else{
			pair = f2 + " "+ f1;
		}
		return pair;
	}
	public static void findLikelyInVariants() {
		for (String pair: funcPairCount.keySet()) {
			// only pairs that appears more than the threshold can be invariants
			int pairCount = funcPairCount.get(pair);
			if ( pairCount >= T_SUPPORT) {
				// let's now find the confidence and find which function caused the bug
				String[] functions = pair.split("\\s+");
				if (functions.length != 2) {
					System.out.println("Something is wrong");
					return;
				}

				String f1 = functions[0];
				String f2 = functions[1];

				double c1 = pairCount * 1.0 / funcCount.get(f1);
				double c2 = pairCount * 1.0 / funcCount.get(f2);

				if (c1 >= T_CONFIDENCE) {
					HashSet<String> invs;
					if (!likelyInvariants.containsKey(f1)) {
						invs = new HashSet<>();
					}else{
						invs = likelyInvariants.get(f1);
					}
					invs.add(f2);
					likelyInvariants.put(f1, invs);
				}

				if (c2 >= T_CONFIDENCE) {
					HashSet<String> invs;
					if (!likelyInvariants.containsKey(f2)) {
						invs = new HashSet<>();
					}else{
						invs = likelyInvariants.get(f2);
					}
					invs.add(f1);
					likelyInvariants.put(f2, invs);
				}
			}
		}
	}

	public static void findBugs() {
		for (String scope: scopeMap.keySet()) {
			for (String bugKey: likelyInvariants.keySet()) {
				HashSet<String> functions = scopeMap.get(scope);
				if (functions.contains(bugKey)) {
					for (String inv: likelyInvariants.get(bugKey)) {
						if (!functions.contains(inv)) {
							// bugkey present but not inv, a potential bug
							String pair = makePair(bugKey, inv);
							int support = funcPairCount.get(pair);
							double confidence = support * 1.0 / funcCount.get(bugKey);
							emit(bugKey, inv, scope, support, confidence);
						}
					}
				}
			}
		}
	}
	public static void main(String[] args) {


		if (args.length == 3) {
			T_SUPPORT = Integer.parseInt(args[2]);
		}else if (args.length == 4){
			T_SUPPORT = Integer.parseInt(args[2]);
			T_CONFIDENCE = Integer.parseInt(args[3]);
		}

		// First, we parse the graph
		parseGraph(args[1]+".callgraph");

		// Then, we find likely invariants
		findLikelyInVariants();

		// Lastly, we find the bugs
		findBugs();
		return;
	}
	
	
}
