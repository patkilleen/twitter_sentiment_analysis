package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class FileAppender {

	/**
	 * size of string buffer in number of characters
	 */
	public static final int DEFAULT_CAPACITY = 512;
	
	private Path outputFile;
	
	private StringBuilder buffer;
	private PrintWriter writer;
	private int capacity;
	
	/**
	 * Constructor
	 * @param outputFile path file to append to
	 * @param capacity the size of string buffer (number of characters stored in buffer before a flush)
	 */
	public FileAppender(Path outputFile,int capacity) {
		this.outputFile=outputFile;
		buffer = new StringBuilder();
		this.capacity=capacity;
	}

	/**
	 * Constructor, with default capacity of 512
	 * @param outputFile path file to append to
	 */
	public FileAppender(Path outputFile) {
		this(outputFile,DEFAULT_CAPACITY);
	}
	
	/**
	 * Constructor, with default capacity of 512
	 * @param outputFile string representing file path to append to
	 */
	public FileAppender(String outputFile) {
		this(new File(outputFile).toPath());
	}

	/**
	 * Constructor
	 * @param outputFile string representing file path to append to
	 * @param capacity the size of string buffer (number of characters stored in buffer before a flush)
	 */
	public FileAppender(String outputFile,int capacity) {
		this(new File(outputFile).toPath(),capacity);
	}
	
	
	/**
	 * opens the file to append to
	 * @throws FileNotFoundException
	 */
	public void open() throws FileNotFoundException{
		writer = new PrintWriter(outputFile.toFile());
	}
	
	/**
	 * Buffers the string to append to the file and flushes all appended 
	 * strings when the capacity of buffer is exceeded
	 * @param s string to append to file
	 */
	public void append(String s){
		
		if(writer ==null){
			throw new IllegalStateException("FileAppender must be opened first before appending.");
		}
		buffer.append(s);
		if(buffer.length() > capacity){
			flush();
		}
	}
	
	/**
	 * closes the file
	 */
	public void close(){
		if(writer ==null){
			throw new IllegalStateException("FileAppender must be opened first before closing.");
		}
		writer.close();
		writer = null;
	}
	
	/**
	 * forces a buffer flush; that is, appends buffered strings to file
	 */
	public void flush(){
		if(writer ==null){
			throw new IllegalStateException("FileAppender must be opened first before flushing.");
		}
		writer.print(buffer.toString());
		buffer.delete(0, buffer.length());
	}
}
