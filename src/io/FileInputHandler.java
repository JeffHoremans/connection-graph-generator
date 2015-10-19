package io;

import generator.GraphInput;
import io.parser.file.FileInputParser;
import io.parser.file.FileInputParserFactory;

import java.io.FileNotFoundException;

import com.google.common.io.Files;

/**
 * A file input handler implementation that handles input files.
 * 
 * @author Jeff Horemans
 * @version 1.1
 * 
 * @invar	The fileName of this file input handler is a valid file name for all file input handlers.
 * 			| isValidFileName(getFileName())
 * @invar	The parser of this file input handler is a valid parser for all file input handlers.
 * 			| isValidParser(getParser())
 */
public class FileInputHandler {
	
	/**
	 * Instance variable referencing the name of the file of this file input handler
	 * */
	private String fileName;
	/**
	 * Instance variable referencing the input parser of the file 
	 * */
	private FileInputParser parser;
	
	/**
	 * Initializes this new file input handler with the given file name and file input parser
	 * 
	 * @param	fileName
	 * 			The given file name
	 * @param	parser
	 * 			The given file input parser
	 * 
	 * @throws	IllegalArgumentException
	 * 			When the given file name is not valid.
	 * 			| !isValidFileName(fileName)
	 * @throws	IllegalArgumentException
	 * 			When the given file parser is not valid.
	 * 			| !isValidParser(parser)
	 * @post	The file name of this file input handler is set to the given file name.
	 * 			| new.getFileName() == fileName
	 * @post	The file input parser of this file input handler is set to the given parser
	 * 			| new.getParser() == parser
	 * */
	public FileInputHandler(String fileName, FileInputParser parser){
		setFileName(fileName);
		setParser(parser);
	}
	
	/**
	 * Initializes this new file input handler with the given file name
	 * 
	 * @param	fileName
	 * 			The given file name
	 * 
	 * @effect	This file input handler is further initialized with the given file name
	 * 			and a file input parser, dynamically created by the file input parser
	 * 			factory based on the extension of the given file name.
	 *			| let parse = FileInputParserFactory.getParser(getFileExtension(fileName))
	 *			| 	in this(fileName, parser)
	 * */
	public FileInputHandler(String fileName){
		this(fileName, FileInputParserFactory.getParser(getFileExtension(fileName)));
	}
	
	/**
	 * Extract the file extension from a given file name.
	 * 
	 * @param	fileName
	 * 			The given file name.
	 * 
	 * @throws	IllegalArgumentException
	 * 			When the given file name is not valid.
	 * 			| !isValidFileName(fileName)
	 * @effect	Return the file extension of the given fileName using the Guava com.google.common.io.Files library
	 * 			| Files.getFileExtension(fileName)
	 * */
	public static String getFileExtension(String fileName){
		if(!isValidFileName(fileName)) throw new IllegalArgumentException("The given file name is not valid.");
		return Files.getFileExtension(fileName);
	}
	
	/**
	 * Process the given system manager, initializing it from an input file.
	 * 
	 * @param 	graph 
	 * 			the graph to .
	 * 
	 * {@inheritDoc}
	 * 
	 * @throws 	IllegalArgumentException 
	 * 			When given file is not found.
	 * 			| when FileNotFoundException is thrown
	 */
	public GraphInput constructGraph() {
		try {
			return getParser().parseFile(getFileName());
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("The given file was not found.");
		}
	}

	/**
	 * Returns the file name of this file input handler
	 * */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Set the file name of this file input handler with the given file name
	 * 
	 * @param	fileName
	 * 			The given file name
	 * */
	private void setFileName(String fileName) {
		if(!isValidFileName(fileName)) throw new IllegalArgumentException("The given file name is not valid.");
		this.fileName = fileName;
	}
	
	/**
	 * Return whether the given file name is a valid file name for all file input handlers.
	 * @param 	fileName
	 * @return	True if, and only if, the given file name is effective.
	 * 			| result == ( fileName != null )
	 */
	public static boolean isValidFileName(String fileName){
		return fileName != null;
	}
	
	/**
	 * Returns the file input parser of this file input handler
	 * */
	public FileInputParser getParser() {
		return parser;
	}

	/**
	 * Set the file input parser of this file input handler with the given parser
	 * 
	 * @param	parser
	 * */
	private void setParser(FileInputParser parser) {
		if(!isValidParser(parser)) throw new IllegalArgumentException("The given file parser is not valid.");
		this.parser = parser;
	}
	
	/**
	 * Return whether the given parser is a valid parser for all file input handlers.
	 * 
	 * @param 	parser
	 * @return	True if, and only if, the given parser is effective.
	 * 			| result == ( parser != null )
	 */
	public static boolean isValidParser(FileInputParser parser){
		return parser != null;
	}
}
