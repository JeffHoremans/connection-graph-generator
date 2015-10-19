package main;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import generator.ConnectionGraphGenerator;
import generator.GraphInput;
import io.FileInputHandler;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main {

	
	public static void main(String[] args){
		GraphInput input = null;
		if (args.length < 1) System.out.println("Error");
		else input = new FileInputHandler(args[0]).constructGraph();
		System.out.println(input.getGraph().toString());
		
		Graph<String, String> connectionGraph = ConnectionGraphGenerator.generate(input);
		System.out.println(connectionGraph.toString());
		
		// The Layout<V, E> is parameterized by the vertex and edge types
        Layout<String, String> graphLayout = new CircleLayout<String, String>(input.getGraph());
        graphLayout.setSize(new Dimension(300,300)); // sets the initial size of the layout space
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
        BasicVisualizationServer<String,String> vv = new BasicVisualizationServer<String,String>(graphLayout);
        vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size 
        vv.getRenderContext().setVertexFillPaintTransformer(new VertexTransformer(input));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
        
        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<String, String> connectionGraphLayout = new CircleLayout<String, String>(connectionGraph);
        connectionGraphLayout.setSize(new Dimension(300,300)); // sets the initial size of the layout space
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
        BasicVisualizationServer<String,String> connectionVv = new BasicVisualizationServer<String,String>(connectionGraphLayout);
        connectionVv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
        connectionVv.getRenderContext().setVertexFillPaintTransformer(new VertexTransformer(input));
        connectionVv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
        connectionVv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
        
        JLabel label1 = new JLabel();
        label1.setText("<HTML>"+input.getX()+" <U>||</U> " + input.getY() + " | " + input.getEvidence().toString().replace("[", "").replace("]", "")
                .replace(", ", ",") + " ?" + "</HTML>");
        JLabel label2 = new JLabel(ConnectionGraphGenerator.getAllPaths(connectionGraph, input.getX(), input.getY()).size() > 0 ? "No": "Yes");
        JFrame frame = new JFrame("Connection Graph Generator");
        frame.setSize(350, 500);
        JPanel lPanel = new JPanel();
        lPanel.setSize(new Dimension(350, 500));
        lPanel.add(vv, BorderLayout.NORTH);
        lPanel.add(label1, BorderLayout.SOUTH);
        lPanel.add(label2, BorderLayout.SOUTH);
        JPanel rPanel = new JPanel();
        rPanel.setSize(new Dimension(350, 500));
        rPanel.add(connectionVv);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(lPanel, BorderLayout.WEST); 
        frame.getContentPane().add(rPanel, BorderLayout.EAST); 
        frame.pack();
        frame.setVisible(true);
	}
}
