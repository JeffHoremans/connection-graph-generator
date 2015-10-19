package io.parser.file;

import generator.GraphInput;

import java.io.FileNotFoundException;

/**
 * An interface for file input parsers.
 * 
 * @author 	Jeff Horemans
 * @version 1.4
 */
public interface FileInputParser {
	
	/**
	 * Parse the given file and initialize the given manger with the parsed objects.
	 * 
	 * @param 	manager 
	 * 			The given system manager.
	 * @param	fileName
	 * 			The given file name.
	 * 
	 * @return 	the manager initialized with the parsed projects
	 * 			| manager.getProjects().size() >= 0
	 * @throws 	IllegalArgumentException the manager parameter is not effective.
	 * 			| manager == null
	 * @throws	FileNotFoundExcepton
	 * 			When the given file is not found
	 */
	GraphInput parseFile(String fileName) throws FileNotFoundException;
	
	/**
	 * Returns the file extension this file input parser supports.
	 * 
	 * @return	An effective string, not empty
	 * 			| result != null && result.length > 0
	 * */
	String getSupportedFileExtension();
}
