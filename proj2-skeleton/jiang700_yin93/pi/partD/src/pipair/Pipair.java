// CS 510
// Project 2 part i.d
// Luke Jiang, Sihao Yin

package pipair;

import java.io.*;
import java.util.*;
import pipair.util.Pair;

public class Pipair {

	static String path = "";
	static double T_CONFIDENCE = 0.65;
	static int T_SUPPORT = 3;


	// callee to caller map
	static Map<String, Set<String>> cmap = new HashMap<>();

	// map of a (caller, caller) pair to callee's order in caller
	static Map<Pair<String, String>, Integer> cmapOrder = new HashMap<>();


	// emit one line of error message
	public static void emit(String fun, String otherfun, String scope, int support, double confidence) {
		System.out.print("bug: " + fun + " in " + scope + ", ");
		System.out.print("pair: (" + otherfun + ", " + fun + "), ");
		System.out.print("support: " + support + ", ");
		System.out.print("confidence: ");
		System.out.printf("%.2f", confidence * 100);
		System.out.println("%");
	}


	public static void analyze(String fun1, String fun2, Set<String> join, Set<String> S1, Set<String> S2) {
		int support = join.size();
		if (support < T_SUPPORT) return;

		// check if confidence satisfies threshold
		double confidence1 = support * 1.0 / S1.size();
		if (confidence1 >= T_CONFIDENCE) {
			Set<String> scopes = new HashSet<>(S1);
			scopes.removeAll(join);
			for (String scope : scopes) {
				emit(fun1, fun2, scope, support, confidence1);
			}
		}

		double confidence2 = support * 1.0 / S2.size();
		if (confidence2 >= T_CONFIDENCE) {
			Set<String> scopes = new HashSet<>(S2);
			scopes.removeAll(join);
			for (String scope : scopes) {
				emit(fun2, fun1, scope, support, confidence2);
			}
		}
	}

	public static void analyzeOrder(String fun1, String fun2) {
		Set<String> S1 = cmap.get(fun1);
		Set<String> S2 = cmap.get(fun2);

		// compute the intersection of thisS and otherDS
		Set<String> join = new HashSet<>(S1);
		join.retainAll(S2);

		Set<String> join12 = new HashSet<>();
		Set<String> join21 = new HashSet<>();
		for (String caller : join) {
			int order1 = cmapOrder.get(new Pair<>(fun1, caller));
			int order2 = cmapOrder.get(new Pair<>(fun2, caller));
			if (order1 < order2) {
				join12.add(caller);
			} else {
				join21.add(caller);
			}
		}

		analyze(fun1, fun2, join12, S1, S2);
		analyze(fun2, fun1, join21, S2, S1);
	}


	// debugging method for printing cmap
	public static void printMap(Map<String, Set<String>> map) {
		for (String key : map.keySet()) {
			System.out.print("function " + key + " : ");
			for (String v : map.get(key)) {
				System.out.print(v + ",");
			}
			System.out.println();
		}
	}

	// debugging method for printing cmapOrder
	public static void printMapOrder(Map<Pair<String, String>, Integer> map) {
		for (Pair<String, String> key : map.keySet()) {
			System.out.print("function pair " + key + " : ");
			System.out.println(map.get(key));
		}
	}

	public static void main(String[] args) {
		path = args[0];
		if (args.length >= 3) {
			T_SUPPORT = Integer.valueOf(args[1]);
			T_CONFIDENCE = (double) (Integer.valueOf(args[2]) * 1.0 / 100);
		}

		// read form file
		File openFile = new File(path);
		Scanner scanner;
		try {
			 scanner = new Scanner(openFile);
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			return;
		}

		// process each line, build cmap and cmapOrder
		boolean ignore = false;
		String caller = "";
		int order = 0;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.startsWith("Call graph node <<null function>>")) {
				ignore = true;
				order = 0;
			} else if (line.startsWith("Call")) {
				ignore = false;
				caller = line.split("'")[1];
				order = 0;
			} else if (line.startsWith("CS") && !ignore) {
				String[] words = line.split("'");
				if (words.length > 1) {
					String callee = words[1];
					Set<String> s = cmap.getOrDefault(callee, new HashSet<>());
					s.add(caller);
					cmap.put(callee, s);
					cmapOrder.put(new Pair<String, String>(callee, caller), order);
					order++;
				}
			}
		}
		scanner.close();

		// analyze each function in cmap's key set
		List<String> ls = new ArrayList<String>(cmap.keySet());
		for (int i = 0; i < ls.size(); i++) {
			for (int j = i + 1; j < ls.size(); j++) {
				analyzeOrder(ls.get(i), ls.get(j));
			}
		}
	}
}
