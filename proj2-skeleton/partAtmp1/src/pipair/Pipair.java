package pipair;

import java.io.*;
import java.util.*;

public class Pipair {

	static String path = "";
	static int T_SUPPORT = 3;
	static double T_CONFIDENCE = 0.65;

	// callee to caller map
	static Map<String, Set<String>> cmap = new HashMap<>();

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

	public static void analyze(String fun1, String fun2) {
		Set<String> S1 = cmap.get(fun1);
		Set<String> S2 = cmap.get(fun2);

		// compute the intersection of thisS and otherDS
		Set<String> join = new HashSet<>(S1);
		join.retainAll(S2);

		// check if support satisfies threshold
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

	// debugging method for printing cmap
	public static void printMap() {
		for (String key : cmap.keySet()) {
			System.out.print("function " + key + " : ");
			for (String v : cmap.get(key)) {
				System.out.print(v + ",");
			}
			System.out.println();
		}
	}


	public static void main(String[] args) {
		path = args[0];
		if (args.length == 3) {
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

		// process each line, build cmap
		boolean ignore = false;
		String caller = "";


		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.startsWith("Call graph node <<null function>>")) {
				ignore = true;
			} else if (line.startsWith("Call")) {
				ignore = false;
				caller = line.split("'")[1];
			} else if (line.startsWith("CS") && !ignore) {
				String[] words = line.split("'");
				if (words.length > 1) {
					String func = words[1];
					Set<String> s = cmap.getOrDefault(func, new HashSet<>());
					s.add(caller);
					cmap.put(func, s);
				}
			}
		}
		scanner.close();

		List<String> ls = new ArrayList<String>(cmap.keySet());
		for (int i = 0; i < ls.size(); i++) {
			for (int j = i + 1; j < ls.size(); j++) {
				analyze(ls.get(i), ls.get(j));
			}
		}
	}
}
