package main;

import generator.GraphInput;

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

public class VertexTransformer implements Transformer<String,Paint> {
	
	private GraphInput input;
	
	public VertexTransformer(GraphInput input){
		this.input = input;
	}
	
    public Paint transform(String vertex) {
        if(vertex.equals(input.getX())) return Color.RED;
        else if(vertex.equals(input.getY())) return Color.GREEN;
        else if(input.getEvidence().contains(vertex)) return Color.YELLOW;
        else return Color.WHITE;
    }
};