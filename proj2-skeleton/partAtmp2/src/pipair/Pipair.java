package pipair;

import java.io.*;
import java.util.*;

public class Pipair {

	static String path = "";
	static int T_SUPPORT = 3;
	static double T_CONFIDENCE = 0.65;
	static boolean EXPAND = false;

	// callee to caller map
	static Map<String, Set<String>> cmap = new HashMap<>();

	// caller to callee map
	static Map<String, Set<String>> cmapR = new HashMap<>();

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
		double confidence = support * 1.0 / S1.size();
		if (confidence >= T_CONFIDENCE) {
			Set<String> scopes = new HashSet<>(S1);
			scopes.removeAll(join);
			for (String scope : scopes) {
				emit(fun1, fun2, scope, support, confidence);
			}
		}

		double confidence1 = support * 1.0 / S2.size();
		if (confidence1 >= T_CONFIDENCE) {
			Set<String> scopes = new HashSet<>(S2);
			scopes.removeAll(join);
			for (String scope : scopes) {
				emit(fun2, fun1, scope, support, confidence1);
			}
		}
	}

	public static void expand() {
		Set<String> common = new HashSet<>(cmap.keySet());
		common.retainAll(cmapR.keySet());

		for (String func : common) {
			Set<String> scopes = cmap.get(func);
			Set<String> callers = cmapR.get(func);
			for (String caller : callers) {
				Set<String> S = cmap.get(caller);
				S.addAll(scopes);
			}
		}

		for (String func : common) {
			cmap.remove(func);
		}
	}

	public static void expand1() {
		Set<String> common = new HashSet<>(cmap.keySet());
		common.retainAll(cmapR.keySet());

		Map<String, Set<String>> M = new HashMap<>();
		for (String func : common) {
			M.put(func, cmap.get(func));
			cmap.remove(func);
		}

		for (String func : common) {
			Set<String> scopes = M.get(func);
			Set<String> callers = cmapR.get(func);
			for (String caller : callers) {
				Set<String> S = cmap.getOrDefault(caller, new HashSet<>());
				S.addAll(scopes);
				cmap.put(caller, S);
			}
		}

		cmapR.clear();
		// Map<String, Set<String>> cmapR1 = new HashMap<>();
		for (String callee : cmap.keySet()) {
			for (String caller : cmap.get(callee)) {
				Set<String> S = cmapR.getOrDefault(caller, new HashSet<>());
				S.add(callee);
				cmapR.put(caller, S);
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
		if (args.length == 4) {
			EXPAND = Boolean.valueOf(args[3]);
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
					String callee = words[1];
					Set<String> s = cmap.getOrDefault(callee, new HashSet<>());
					s.add(caller);
					cmap.put(callee, s);
					if (EXPAND) {
						Set<String> s1 = cmapR.getOrDefault(caller, new HashSet<>());
						s1.add(callee);
						cmapR.put(caller, s1);
					}
				}
			}
		}


		scanner.close();
		if (EXPAND) expand1();

		List<String> ls = new ArrayList<String>(cmap.keySet());
		for (int i = 0; i < ls.size(); i++) {
			for (int j = i + 1; j < ls.size(); j++) {
				analyze(ls.get(i), ls.get(j));
			}
		}
	}
}
