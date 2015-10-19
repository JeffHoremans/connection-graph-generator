package io.parser.file.cgg;
import generator.GraphInput;
import io.parser.file.FileInputParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
/**
 * A class to represent an abstract YAML file input parser strategy.
 * 
 * @author 	Jeff Horemans
 * @version 1.3
 */
public class CggFileInputParser implements FileInputParser {
	
	/**
	 * Variable registering the tokenizer of this YAML file input parser
	 * */
	private StreamTokenizer tokenizer;
	
	/**
	 * Parse the given file and initialize the given manger with the parsed objects.
	 * 
	 * @param 	manager 
	 * 			The given system manager.
	 * @param	fileName
	 * 			The given file name.
	 * 
	 * @throws	IllegalArgumentException
	 * 			When the given manager or file name are not effective.
	 * 			| field == null || fileName == null
	 * @effect	The tokenizer of this YAML file input parser is initialized as an effective stream tokenizer.
	 * 			| new.getTokenizer() != null
	 * @effect	The expected file structure of this YAML file input parser is set to the result of
	 * 			the createExpectedFileStructure method.
	 * 			| new.getExpectedFileStructure() = createExpectedFileStructure()
	 * @effect	The comment handling of this YAML file input parser is set.
	 * 			| setCommentHandling()
	 * @effect	The expected file structure of this YAML file input parser is parsed.
	 * 			| parseFileStructure()
	 * @effect	Return the populated manager.
	 * 			| populateManager(manager)
	 */
	@Override
	public final GraphInput parseFile(String fileName) throws FileNotFoundException{
		if(fileName == null) throw new IllegalArgumentException("The given file name is not effective.");
		setTokenizer(new StreamTokenizer(new FileReader(fileName)));
		setCommentHandling();
		
		Graph<String, String> graph = new SparseGraph<String, String>();
		EdgeType edgeType;
		
		nextToken();
		String type = expectEnumField("type", new String[]{"Bayesian", "Markov"});
		if("Markov".equals(type)) edgeType = EdgeType.UNDIRECTED;
		else edgeType = EdgeType.DIRECTED;
		expectLabel("vertices");
		while (getTokenType() == '-') {
			expectChar('-');
			String name = expectStringField("name");
			graph.addVertex(name);
		}
		expectLabel("edges");
		while (getTokenType() == '-') {
			expectChar('-');
			String v1 = expectStringField("v1");
			String v2 = expectStringField("v2");
			graph.addEdge(v1 + "-" + v2, v1, v2, edgeType);
		}
		String x = expectStringField("x");
		String y = expectStringField("y");
		
		List<String> evidence = new ArrayList<String>();
		expectLabel("evidence");
		while (getTokenType() == '-') {
			expectChar('-');
			String name = expectStringField("name");
			evidence.add(name);
		}
		
		if (getTokenType() != StreamTokenizer.TT_EOF){
			error("End of file or '-' expected");
		}
		return new GraphInput(graph, type, x, y, evidence);
	}
	
	/**
	 * Set the comment handling of this file input parser's tokenizer.
	 * */
	protected void setCommentHandling(){
		getTokenizer().slashSlashComments(false);
		getTokenizer().slashStarComments(false);
		getTokenizer().ordinaryChar('/');
		getTokenizer().commentChar('#');
	}

	/**
	 * Return the next token of the tokenizer of this YAML input file parser.
	 * 
	 * @effect	Returns the next token of the tokenizer
	 * 			| tokenizer.nextToken()
	 * @throws	RuntimeException
	 * 			When an IOException is thrown
	 * */
	public int nextToken() {
		try {
			return tokenizer.nextToken();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Throw a new Runtime exception with the given message
	 * 
	 * @throws	RuntimeException
	 * 			Always
	 * 			| true
	 * */
	protected void error(String message) {
		throw new RuntimeException("Line " + getLineNo() + ": " + message);
	}
	
	/**
	 * Return the current line number of the tokenizer of this YAML file input parser.
	 * 
	 * @effect	Returns the current line number of the tokenizer
	 * 			| tokenizer.lineno()
	 * */
	public int getLineNo(){
		return tokenizer.lineno();
	}
	
	/**
	 * Returns whether the given string is a word.
	 * 
	 * @param	word
	 * 			The given string
	 * 
	 * @effect	True if and only if the current token type is a word token
	 * 			and if so, the word token is equal to the given word
	 * 			| getTokenType() == StreamTokenizer.TT_WORD && getCurrentStringValue().equals(word)
	 * */
	protected boolean isWord(String word) {
		return getTokenType() == StreamTokenizer.TT_WORD && getCurrentStringValue().equals(word);
	}
	
	/**
	 * Return the sval field of the tokenizer of this YAML file input parser
	 * 
	 * @effect	The current sval field of the tokenizer
	 * 			| getTokenizer().sval
	 * */
	public String getCurrentStringValue(){
		return tokenizer.sval;
	}
	
	/**
	 * Return the nval field of the tokenizer of this YAML file input parser
	 * 
	 * @effect	The current nval field of the tokenizer
	 * 			| getTokenizer().nval
	 * */
	public double getCurrentNumberValue(){
		return tokenizer.nval;
	}
	
	/**
	 * Returns the token type of the tokenizer of this YAML file input parser
	 * 
	 * @effect	Returns the token type of the tokenizer
	 * 			| getTokenizer().ttype
	 * */
	public int getTokenType(){
		return tokenizer.ttype;
	}
	
	/**
	 * Expect the given char as the next token.
	 * 
	 * @param	c
	 * 			The given char
	 * 
	 * @effect	If the current token type is not equal to the given char an error is thrown.
	 * 			| if (getTokenType() != c)
				| 	then error (...)
	 * @effect	Move to the next token of the tokenizer
	 * 			| nextToken()
	 * */
	protected void expectChar(char c) {
		if (getTokenType() != c)
			error ("'" + c + "' expected");
		nextToken();
	}
	
	/**
	 * Expect an enum field with the given label and one of the given options as it's value.
	 * 
	 * @param	label
	 * 			The given string label
	 * 
	 * @throws	IllegalArgumentException
	 * 			When the given list of options is not effective or empty.
	 * 			| options == null || options.length == 0
	 * @effect	Expect the given label
	 * 			| expectLabel(label)
	 * @effect	For every option of the given options, check if it is a word and if so, move to the next token.
	 * 			| for each option in options
	 * 			| 	if isWord(option)
	 * 			|		then nextToken()
	 * */
	protected String expectEnumField(String label, String[] options) {
		if(options == null) throw new IllegalArgumentException("The given list of options is not effective");
		if(options.length == 0) throw new IllegalArgumentException("The given list of options is empty.");
		expectLabel(label);
		String result = null;
		for(String option: options){
			if(isWord(option)){
				nextToken();
				result = option;
			}
		}
		return result;
	}
	
	/**
	 * Expect a string label with the given name.
	 * 
	 * @param	name
	 * 			The given name.
	 * 
	 * @throws	IllegalArgumentException
	 * 			When the given name is not effective or empty.
	 * 			| name == null || name.length() == 0
	 * @effect	If the given name is not a word, throw an error.
	 * 			| if (!isWord(name))
	 * 			|	then error(...)
	 * @effect	Move to the next token of the tokenizer
	 * 			| nextToken()
	 * @effect	Expect a colon char.
	 * 			| expectChar(':')
	 * */
	protected void expectLabel(String name) {
		if(name == null) throw new IllegalArgumentException("The given name is not effective.");
		if(name.length() == 0) throw new IllegalArgumentException("The given name is empty.");
		if (!isWord(name))
			error("Keyword '" + name + "' expected");
		else {
			nextToken();
			expectChar(':');
		}
	}
	
	/**
	 * Expect a string field with the given label.
	 * 
	 * @param	label
	 * 			The given label.
	 * 
	 * @effect	Expect the given label
	 * 			expectLabel(label)
	 * @effect	If the current token type is not equal to the quotation mark char, throw an error.
	 * 			| if (getTokenType() != '"')
	 * 			|	then error(...)
	 * @effect	Move to the next token of the tokenizer
	 * 			| nextToken()
	 * @effect	Expect a colon char.
	 * 			| expectChar(':')
	 * @return	Return the word token of the tokenizer
	 * 			| result = old.tokenizer.sval
	 * */
	protected String expectStringField(String label) {
		expectLabel(label);
		if (getTokenType() != '"')
			error("String expected");
		String value = getCurrentStringValue();
		nextToken();
		return value;
	}
	
	/**
	 * Expect an int.
	 * 
	 * @effect	If the current token type is not a number token or if so and it is not a valid integer, throw an error.
	 * 			| if (getTokenType() != StreamTokenizer.TT_NUMBER || getCurrentNumberValue() != (double)(int)getCurrentNumberValue())
	 * 			|	then error(...)
	 * @effect	Move to the next token of the tokenizer
	 * 			| nextToken()
	 * @return	Return the number token of the tokenizer
	 * 			| result == getCurrentNumberValue()
	 * */
	protected int expectInt() {
		int value=0;
		if (getTokenType() != StreamTokenizer.TT_NUMBER || getCurrentNumberValue() != (double)(int)getCurrentNumberValue())
			error("Integer expected");
		else {
			value = (int)getCurrentNumberValue();
			nextToken();
		}
		
		return value;
	}
	
	/**
	 * Expect a int field with the given label.
	 * 
	 * @param	label
	 * 			The given label.
	 * 
	 * @effect	Expect the given label
	 * 			expectLabel(label)
	 * @effect	Return an expected int
	 * 			| expectInt()
	 * */
	protected int expectIntField(String label) {
		expectLabel(label);
		return expectInt();
	}
	
	/**
	 * Expect an Integer field with the given label.
	 * 
	 * @param	label
	 * 			The given label.
	 * 
	 * @effect	Expect the given label
	 * 			expectLabel(label)
	 * @effect	If the current token type is a number token, return an expected int.
	 * 			| if (getTokenType() == StreamTokenizer.TT_NUMBER)
	 * 			|	then expectInt();
	 * */
	protected Integer expectIntegerField(String label) {
		Integer integer = null;
		expectLabel(label);
		if (getTokenType() == StreamTokenizer.TT_NUMBER)
			integer = expectInt();
		return integer;
	}
	
	/**
	 * Expect a string field with the given label.
	 * 
	 * @param	label
	 * 			The given label.
	 * 
	 * @effect	Expect the given label
	 * 			expectLabel(label)
	 * @effect	If the current token type is equal to left open square bracket char, return an expected int list.
	 * 			| if (getTokenType() == '[')
	 * 			| 	then list = expectIntList()
	 * */
	protected List<Integer> expectIntListField(String label) {
		List<Integer> list = new ArrayList<>();
		expectLabel(label);
		if (getTokenType() == '[')
			list = expectIntList();
		return list;
	}
	
	/**
	 * Expect an int list.
	 * 
	 * @effect	Expect a opened square bracket char.
	 * 			| expectChar('[')
	 * @effect	Expect a closed square bracket char.
	 * 			| expectChar(']')
	 * @effect	Return a list of Integers, populated by adding an expected int if the next token is a number token
	 * 			followed by an expected comma char or a close square bracket, throw an error otherwise.
	 * 			| let list = new List<Integer>
	 * 			| 	in while (getTokenType() == StreamTokenizer.TT_NUMBER)
	 * 			|		then list.add(expectInt())
	 * 			| 	     	if (getTokenType() == ',')
	 * 			|				then expectChar(',')
	 * 			|				else 
	 * 			|					if (getTokenType() != ']')
	 * 			|						then error(...);
		}
	 * */
	protected List<Integer> expectIntList() {
		ArrayList<Integer> list = new ArrayList<>();
		expectChar('[');
		while (getTokenType() == StreamTokenizer.TT_NUMBER){
			list.add(expectInt());
			if (getTokenType() == ',')
				expectChar(',');
			else if (getTokenType() != ']')
				error("']' (end of list) or ',' (new list item) expected");
		}
		expectChar(']');
		return list;
	}
	
	/**
	 * Sets the tokenizer of this YAML file input parser to the given tokenizer.
	 * 
	 * @param	tokenizer
	 * 			The given tokenizer
	 * 
	 * @throws	IllegalArgumentException
	 * 			When the given tokenizer is not effective.
	 * @post	The tokenizer of this YAML file input parser is set to the given tokenizer
	 * 			| new.getTokenizer() == tokenizer
	 * 
	 * */
	protected void setTokenizer(StreamTokenizer tokenizer){
		if(tokenizer == null) throw new IllegalArgumentException("The given tokenizer is not effective.");
		this.tokenizer = tokenizer;
	}
	
	/**
	 * Returns the tokenizer of this YAML parser
	 * */
	protected StreamTokenizer getTokenizer(){
		return tokenizer;
	}

	@Override
	public String getSupportedFileExtension() {
		return "CGG";
	}
}
