import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Stack;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.*;

public class CFG {
    Set<Node> nodes = new HashSet<Node>();
    Map<Node, Set<Node>> edges = new HashMap<Node, Set<Node>>();

    static class Node {
		int position;
		MethodNode method;
		ClassNode clazz;
	
		Node(int p, MethodNode m, ClassNode c) {
		    position = p; method = m; clazz = c;
		}
	
		public boolean equals(Object o) {
		    if (!(o instanceof Node)) return false;
		    Node n = (Node)o;
		    return (position == n.position) &&
			method.equals(n.method) && clazz.equals(n.clazz);
		}
	
		public int hashCode() {
		    return position + method.hashCode() + clazz.hashCode();
		}
	
		public String toString() {
		    return clazz.name + "." +
			method.name + method.signature + ": " + position;
		}
    }
    
    private Node getNode(int p, MethodNode m, ClassNode c) {
    	Node ref = new Node(p, m, c);
    	for (Node n : nodes) {
    		if (n.equals(ref)) {
    			return n;
    		}
    	}
    	return null;
    }

    public void addNode(int p, MethodNode m, ClassNode c) {
    	if (getNode(p, m, c) != null) {
    		return;
    	}
    	Node node = new Node(p, m, c);
    	nodes.add(node);
    	edges.put(node, new HashSet<>());
    }

    public void addEdge(int p1, MethodNode m1, ClassNode c1,
			int p2, MethodNode m2, ClassNode c2) {
    	
    	addNode(p1, m1, c1);
    	addNode(p2, m2, c2);
    	
    	Node n1 = getNode(p1, m1, c1);
    	Node n2 = getNode(p2, m2, c2);
    	
    	Set<Node> n1_neighbors = edges.get(n1);
    	n1_neighbors.add(n2);
    	edges.put(n1, n1_neighbors);
    	
    }
	
	public void deleteNode(int p, MethodNode m, ClassNode c) {
		Node toDelete = getNode(p, m, c);
		if (toDelete == null) {
			return;
		}
		for (Node n : edges.keySet()) {
			edges.get(n).remove(toDelete);
		}
		edges.remove(toDelete);
		nodes.remove(toDelete);
    }
	
    public void deleteEdge(int p1, MethodNode m1, ClassNode c1,
						int p2, MethodNode m2, ClassNode c2) {

    	Node n1 = getNode(p1, m1, c1);
    	Node n2 = getNode(p2, m2, c2);
    	if (n1 == null || n2 == null) {
    		return;
    	}
    	edges.get(n1).remove(n2);
    }
	

    public boolean isReachable(int p1, MethodNode m1, ClassNode c1,
			       int p2, MethodNode m2, ClassNode c2) {
    	Node start = getNode(p1, m1, c1);
    	Node end = getNode(p2, m2, c2);
    	if (start == null || end == null) {
    		return false;
    	}
    	
    	Set<Node> visited = new HashSet<>();
    	Stack<Node> st = new Stack<>();
    	st.push(start);
    	
    	while (!st.isEmpty()) {
    		Node curr = st.pop();
    		if (curr.equals(end)) {
    			return true;
    		}
    		visited.add(curr);
    		for (Node n : edges.get(curr)) {
    			if (!visited.contains(n)) {
    				st.push(n);
    			}
    		}
    	}
    	
    	return false;
    }
}