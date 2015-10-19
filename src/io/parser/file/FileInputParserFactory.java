package io.parser.file;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;


/**
 * A class representing a Factory to create File Input Parsers on extension basis.
 * 
 * @author Jeff Horemans
 * @version 1.3
 */
public class FileInputParserFactory {
	
	/**
	 * Class variable that references a map of parsers that this factory can create.
	 * */
	private static Map<String,FileInputParser> parsers;
	
	/**
	 * Empty constructor that is not visible
	 * */
	private FileInputParserFactory(){}
	
	/**
	 * Static initialization block that initializes the parsers map and registers every known file input parser 
	 * service provider declared into  the file META-INF/services/domain.io.parser.file.FileInputParser
	 * */
	static {
		parsers = new HashMap<String, FileInputParser>();
		for (FileInputParser parser : ServiceLoader.load(FileInputParser.class)){
			register(parser);
		}
	}
	
	/**
	 * Returns the parser corresponding to the given file extension.
	 * 
	 * @param	extension
	 * 			The given file extension
	 * 
	 * @throws	IllegalArgumentException
	 * 			When the given file extension is not effective
	 * 			| extension == null
	 * @throws	IllegalArgumentException
	 * 			When the given file extension is not supported, i.e. when it is not
	 * 			found as a key of the parsers map.
	 * 			| parsers.get(extension.toUpperCase()) == null
	 * @return	A valid file input parser, not null.
	 * 			| result != null
	 */
	public static FileInputParser getParser(String extension) throws IllegalArgumentException {
		if(extension == null) throw new IllegalArgumentException("The given extension is not effective.");
		FileInputParser parser = parsers.get(extension.toUpperCase());
		if (parser == null) throw new IllegalArgumentException("The given file has an unsupported file extension");
		return parser;
	}

	/**
	 * Register a given file input parser.
	 * 
	 * @param	parser
	 * 			The given file input parser
	 * 
	 * @throws	IllegalArgumentException
	 * 			When the given parser is not effective
	 * 			| parser == null
	 * @post	The given parser is added to the map of parsers this factory can create, 
	 * 			using the parser's supported file extension as the key.
	 * 			| parsers.put(parser.getSupportedFileExtension().toUpperCase(), parser);
	 * */
	public static void register(FileInputParser parser) {
		if(parser == null) throw new IllegalArgumentException("The given parser is not effective.");
		parsers.put(parser.getSupportedFileExtension().toUpperCase(), parser);
	}
}
