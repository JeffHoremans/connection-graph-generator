package generator;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;

public class GraphInput {

	Graph<String, String> graph;
	GraphType graphType;
	String x;
	String y;
	List<String> evidence;
	
	public GraphInput(Graph<String, String> graph, String graphType, String x, String y, List<String> evidence){
		this.graph = graph;
		this.graphType = GraphType.fromString(graphType);
		this.x = x;
		this.y = y;
		this.evidence = evidence;
	}
	
	public Graph<String, String> getGraph(){
		return graph;
	}

	public Object getGraphType() {
		return graphType;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	public List<String> getEvidence() {
		return evidence;
	}
}
