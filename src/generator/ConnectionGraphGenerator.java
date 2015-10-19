package generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class ConnectionGraphGenerator {

	public static Graph<String, String> generate(GraphInput input){
		Graph<String, String> graph = input.getGraph();
		Graph<String, String> connectionGraph = new SparseGraph<String, String>();
		
		String x = input.getX();
		String y = input.getY();
		List<String> evidence = input.getEvidence();
		
		for(String vertex: graph.getVertices()){
			connectionGraph.addVertex(vertex);
		}
		
		for(String edge: graph.getEdges()){
			Pair<String> pair = graph.getEndpoints(edge);
			connectionGraph.addEdge(edge, pair.getFirst(), pair.getSecond(), EdgeType.UNDIRECTED);
		}
		
		List<String> paths = getAllPaths(graph, x, y);
		for(String path: paths){
			for(int i = 1, n = path.length() ; i < n-1 ; i++) { 
			    String vertex = path.charAt(i)+"";
			    int index = path.indexOf(vertex);
				String lVertex = "" + path.charAt(index-1);
				String rVertex = "" + path.charAt(index+1);
			    if(isCollider(graph, path, vertex)){
			    	if(!evidence.contains(vertex) && !anyDescendantsInEvidence(graph, evidence, vertex)){
		    			connectionGraph.removeEdge(lVertex+"-"+vertex);
		    			connectionGraph.removeEdge(rVertex+"-"+vertex);
			    	} else {
			    		/*edges.add(lVertex+"-"+vertex);
			    		edges.add(rVertex+"-"+vertex);*/
			    	}
			    } else if(evidence.contains(vertex)){
			    	connectionGraph.removeEdge(lVertex+"-"+vertex);
			    	connectionGraph.removeEdge(rVertex+"-"+vertex);
			    	connectionGraph.removeEdge(vertex+"-"+lVertex);
			    	connectionGraph.removeEdge(vertex+"-"+rVertex);
			    }
			}
		}
		
		return connectionGraph;
	}
	
	private static boolean anyDescendantsInEvidence(Graph<String, String> graph, List<String> evidence, String vertex) {
		boolean result = false;
		for(String condition: graph.getSuccessors(vertex)){
			if(evidence.contains(condition)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static boolean isCollider(Graph<String, String> graph, String path, String vertex){
		int index = path.indexOf(vertex);
		String lVertex = "" + path.charAt(index-1);
		String rVertex = "" + path.charAt(index+1);
		boolean left = false, right = false;
		for(String inEdge: graph.getInEdges(vertex)){
			if(inEdge.startsWith(lVertex)) left = true;
			else if(inEdge.startsWith(rVertex)) right = true;
		}
		return left && right;
	}
	
	public static List<String> getAllPaths(Graph<String, String> graph, String x, String y){
		List<String> paths = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();
		stack.add(x);
		while(!stack.isEmpty()){
			String path = stack.pop();
			if(path.endsWith(y)) paths.add(path);
			else {
				for(String neighbour: graph.getNeighbors(path.substring(path.length() - 1))){
					if(!path.contains(neighbour)) stack.add(path + neighbour);
				}
			}
		}
		return paths;
	}
	
}
