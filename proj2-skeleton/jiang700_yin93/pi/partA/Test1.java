package jiang700_yin93.pi.partA;

import java.util.*;
import java.io.*;
import java.io.FileNotFoundException;


public class Test1 {
	
	// TODO: incorporate into args of main
	static String path = "/Users/lukejiang/eclipse-workspace/proj2/src/proj2/httpd.callgraph";
	static double T_CONFIDENCE = 0.65;
	static int T_SUPPORT = 3;
	
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
	
	public static void analyze(String fun1) {
		Set<String> S1 = cmap.get(fun1);

		for (String fun2 : cmap.keySet()) {
			if (fun2.equals(fun1)) continue;
			Set<String> S2 = cmap.get(fun2);

			// compute the intersection of thisS and otherDS
			Set<String> join = new HashSet<>(S1);
			join.retainAll(S2);
			
			// check if support satisfies threshold
			int support = join.size();
			if (support < T_SUPPORT) continue;
			
			// check if confidence satisfies threshold
			double confidence = support * 1.0 / S1.size();
			if (confidence < T_CONFIDENCE) continue;
			
			Set<String> scopes = new HashSet<>(S1);
			scopes.removeAll(join);
			for (String scope : scopes) {
				emit(fun1, fun2, scope, support, confidence);
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
		return;
	}
	
	
	public static void main(String[] args) {
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
		
		// analyze each function in cmap's key set
		for (String f : cmap.keySet()) {
			analyze(f);
		}
		return;
	}
	
	
}
