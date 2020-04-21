package io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import classify.TweetParser;
import common.exception.ConfigurationException;
import io.log.Logger;
import io.log.LoggerFactory;
import io.log.ProgressLogger;

public class FileHandler {

	private final static LinkOption [] EXISTS_NO_FOLLOW_ARG = new LinkOption[]{ LinkOption.NOFOLLOW_LINKS};

	public FileHandler() {
	}

	/**
	 * Copies a file to another location. Does not overwrite files.
	 * @param srcFilePath File path of source file to copy.
	 * @param destFilePath File path of the destination of source file to be copied to.
	 * @throws IOException thrown when the source file doesn't exist, the destination file arleady exists, or something went wrong copying the file.
	 * @return the path to the target file 
	 */
	public static Path copy(Path srcFilePath, Path destFilePath) throws IOException{
		//null ptr?
		if(srcFilePath == null || destFilePath == null){
			throw new ConfigurationException("cannot copy file due to null path.");
		}

		//make sure the source file exists
		if(!Files.exists(srcFilePath,EXISTS_NO_FOLLOW_ARG)){
			throw new FileNotFoundException("cannot copy file, since the source file "+srcFilePath.toString()+" doesn't exist.");
		}

		//make sure destination file dosn't already exist to avoid overwritting an existing file
		//by design the shouldn't be a file where another file is being copied to
		//in this simulation, so this is a precaution measure.
		if(!Files.exists(destFilePath,EXISTS_NO_FOLLOW_ARG)){
			return Files.copy(srcFilePath, destFilePath);
		}else{
			throw new IOException("cannot copy file to "+destFilePath.toString()+", since destination already exists");
		}
	}

	/**
	 * Copies a file to another location. Does not overwrite files.
	 * @param srcPath File path of source file to copy.
	 * @param destPath File path of the destination of source file to be copied to.
	 * @throws IOException thrown when the source file doesn't exist, the destination file arleady exists, or something went wrong copying the file.
	 * @return the path to the target file
	 */
	public static Path copy(String srcPath, String destPath) throws IOException{
		//null ptr?
		if(srcPath == null || destPath == null){
			throw new ConfigurationException("cannot copy file due to null path.");
		}

		Path srcFilePath = Paths.get(srcPath);
		Path destFilePath = Paths.get(destPath);;
		return copy(srcFilePath,destFilePath);
	}

	/**
	 * Creates a file at a specific path. 
	 * @param filePath The path to create a new file
	 * @throws IOException Thrown when the file already exists or otherwise fails.
	 * @return the file
	 */
	public static Path createFile(Path filePath) throws IOException{
		if(filePath == null){
			throw new ConfigurationException("cannot create file due to null path.");
		}

		//make sure the file doesn't already exist before creating it
		if(!Files.exists(filePath,EXISTS_NO_FOLLOW_ARG)){
			Path newFile = Files.createFile(filePath);

			//double check to make sure it was crated
			if(!Files.exists(newFile,EXISTS_NO_FOLLOW_ARG)){
				throw new IOException("failed to create file "+filePath);
			}

			return newFile;
		}else{
			throw new IOException("cannot create file "+filePath+", it already exist.");
		}
	}

	/**
	 * Creates a file at a specific path. 
	 * @param path The path to create a new file
	 * @throws IOException Thrown when the file already exists or otherwise fails.
	 * @return the file
	 */
	public static Path createFile(String path) throws IOException{
		Logger log = LoggerFactory.getInstance();

		if(path == null){
			throw new ConfigurationException("cannot create file due to null path.");
		}
		log.log_debug("creating file: "+path.toString());
		Path filePath = Paths.get(path);
		return createFile(filePath);
	}

	/**
	 * Appends bytes to an already existing file.
	 * @param filePath The file path to append to
	 * @param bytes Bytes to append to file.
	 * @throws IOException thrown when the source file doesn't exists or otherwise failed.
	 * @return the path
	 */
	public static Path append(Path filePath, byte [] bytes) throws IOException{
		if(filePath == null || bytes == null){
			throw new ConfigurationException("cannot append to file due to null path or null bytes.");
		}

		//make sure the file already exist before appending to it
		if(Files.exists(filePath,EXISTS_NO_FOLLOW_ARG)){
			return Files.write(filePath, bytes, StandardOpenOption.APPEND);
		}else{
			throw new IOException("cannot append to file "+filePath+", it doesn't exist.");
		}
	}


	/**
	 * Appends bytes to an already existing file.
	 * @param path The file path to append to
	 * @param bytes Bytes to append to file.
	 * @throws IOException thrown when the source file doesn't exists or otherwise failed.
	 * @return the path
	 */
	public static Path append(String path, byte [] bytes) throws IOException{
		if(path == null){
			throw new ConfigurationException("cannot append to file due to null path.");
		}

		Path filePath = Paths.get(path);
		return append(filePath,bytes);
	}

	/**
	 * Moves a file to another location. Does not overwrite files.
	 * @param srcFilePath File path of source file to move.
	 * @param destFilePath File path of the destination of source file to be moved to.
	 * @throws IOException thrown when the source file doesn't exist, the destination file already exists, or otherwise failed.
	 * @return the path to the target file
	 */
	public static Path move(Path srcFilePath, Path destFilePath) throws IOException{
		//null ptr?
		if(srcFilePath == null || destFilePath == null){
			throw new ConfigurationException("cannot move file due to null path.");
		}

		//make sure the source file exists
		if(!Files.exists(srcFilePath,EXISTS_NO_FOLLOW_ARG)){
			throw new FileNotFoundException("cannot move file, since the source file "+srcFilePath.toString()+" doesn't exist.");
		}

		//since the StandardCopyOption.REPLACE_EXISTING arg is ommited, it won't overwrite files
		return Files.move(srcFilePath, destFilePath);
	}

	/**
	 * Moves a file to another location. Does not overwrite files.
	 * @param srcPath File path of source file to move.
	 * @param destPath File path of the destination of source file to be moved to.
	 * @throws IOException thrown when the source file doesn't exist, the destination file already exists, or otherwise failed.
	 * @return the path to the target file
	 */
	public static Path move(String srcPath, String destPath) throws IOException{
		//null ptr?
		if(srcPath == null || destPath == null){
			throw new ConfigurationException("cannot move file due to null path.");
		}

		Path srcFilePath = Paths.get(srcPath);
		Path destFilePath = Paths.get(destPath);
		return move(srcFilePath,destFilePath);
	}

	/**
	 * Creates a new directory.
	 * @param filePath The path of new directory.
	 * @throws IOException Thrown when the directory already exists or creating the directory otherwise failed.
	 * @return the directory
	 */
	public static Path mkdir(Path filePath ) throws IOException{
		if(filePath == null){
			throw new ConfigurationException("cannot move file due to null path.");
		}


		//make sure directory dosn't already exist before creating it
		if(!Files.exists(filePath,EXISTS_NO_FOLLOW_ARG)){
			return Files.createDirectory(filePath);
		}else{
			throw new IOException("cannot create directory "+filePath+", it already exists");
		}
	}

	/**
	 * Creates a new directory.
	 * @param path The path of new directory.
	 * @throws IOException Thrown when the directory already exists or creating the directory otherwise failed.
	 * @return the directory
	 */
	public static Path mkdir(String path) throws IOException{
		if(path == null){
			throw new ConfigurationException("cannot move file due to null path.");
		}

		Path filePath = Paths.get(path);
		return mkdir(filePath);
	}

	/**
	 * Copies a folder to a destination
	 * @param src the source folder
	 * @param dest the destination folder
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest) throws IOException {

		if(src.isDirectory()){

			//if directory not exists, create it
			if(!dest.exists()){
				dest.mkdir();

				//System.out.println("Directory copied from " 
				//         + src + "  to " + dest);
			}

			//list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				//construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				//recursive copy
				copyFolder(srcFile,destFile);
			}

		}else{
			//if file, then copy it

			//try to copy it
			try{
				//Use bytes stream to support all file types
				InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(dest); 

				byte[] buffer = new byte[1024];

				int length;
				//copy the file content in bytes 
				while ((length = in.read(buffer)) > 0){
					out.write(buffer, 0, length);
				}

				in.close();
				out.close();
				//System.out.println("File copied from " + src + " to " + dest);

			}catch(IOException e){
				//ignore it, we just faied to copy 1 file
				Logger log = LoggerFactory.getInstance();
				log.log_warning("failed to copy input file ("+src.getAbsolutePath()+") to ("+dest.getAbsolutePath()+"), due to: "+e.getMessage());
			}
		}
	}

	/**
	 * Counts the number of lines in a file.
	 * @param filename file to count lines in
	 * @return number of lines in file
	 * @throws IOException
	 */
	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	/**
	 * Reads a source file and samples it with a given probability and 
	 * creates an output file.
	 * For example, 20% of a file's lines could be sampled into a file (dest), and 
	 * the remaining 80% that were excluded from the sampling will be left in another file (delta_src).
	 * The original file (src) remains untouched.
	 *   
	 * @param src source file path to sample from
	 * @param delta_src the file path to the file that contains all un-sampled lines from src
	 * @param dest destination file path of samples from src
	 * @param sampleProb probability [0,1] of sampling a line from src and outputing to dest
	 * @throws IOException 
	 */
	public static void createSampleFile(String src, String delta_src, String dest, double sampleProb) throws IOException{

		Logger log = LoggerFactory.getInstance();
		log.log_debug("sampling, with probability: "+sampleProb+" from :"+src);
		log.log_debug("sampled file: "+dest);
		log.log_debug("delta file: "+delta_src);


		int numLines=FileHandler.countLines(src);
		//log 10% intervals of completion
		ProgressLogger progress = new ProgressLogger(0.1,numLines);

		if(sampleProb < 0){
			sampleProb=0;
		}else if(sampleProb>1){
			sampleProb=1;
		}

		Random rand = new Random();


		/*
		 * create the ouput files if they don't exist
		 */
		File delta_srcFile = new File(delta_src);
		if(!delta_srcFile.exists()){
			delta_srcFile.createNewFile();
		}

		File destFile = new File(dest);
		if(!destFile.exists()){
			destFile.createNewFile();
		}


		FileAppender destOut = new FileAppender(dest);
		FileAppender deltaSrcOut = new FileAppender(delta_src);
		BufferedReader reader = new BufferedReader(new FileReader(src));

		destOut.open();
		deltaSrcOut.open();
		String line = reader.readLine();

		//iterate all lines of source file
		while (line != null) {
			progress.logProgress("file "+src+" sampling progress...");	
			//random double between 0 and 1
			double p = rand.nextDouble();

			//do we sample the line?
			if(p < sampleProb){
				destOut.append(line+TweetParser.NEW_LINE);
			}else{//dont smaple it
				deltaSrcOut.append(line+TweetParser.NEW_LINE);
			}


			// read next line
			line = reader.readLine();
		}

		destOut.flush();
		deltaSrcOut.flush();

		reader.close();
		destOut.close();
		deltaSrcOut.close();

	}


	/**
	 * Reads a file and returns its content
	 * @param path path to file to read
	 * @return string representing content of file
	 * @throws IOException
	 */
	public static String readFile(String path) throws IOException{
		String res = "";
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(path), Charset.forName("UTF-8")));
		String line = reader.readLine();
		while (line != null) {

			res+=line+TweetParser.NEW_LINE;

			// read next line
			line = reader.readLine();
		}
		reader.close();
		return res;
	}
	
	
	/**
	 * Prepares files for k-fold cross validation.
	 * Seperates the files into k-folds and gives access to these folds
	 * to consummers
	 * @param datasetFile file to partition
	 * @param k number of folds
	 * @throws IOException 
	 * @para consummer the consummer that will use the files partitioned
	 */
	public static void performKFoldCrossValidation(KFoldCrossValidationConsummer consummer, Path datasetFile, int k) throws IOException {
		
		if(k <=0){
			throw new IllegalArgumentException("cannot perform k-fold cross validation for k = "+k+". k must be positive");
		}
		
		int numLines = FileHandler.countLines(datasetFile.toString());
		
		//find the size of partitions
		int partitionSize = Math.floorDiv(numLines, k);
		
		//create all the folds
		for(int foldIx = 0;foldIx < k;foldIx++){
			File trainingData = File.createTempFile("kFoldTrainingData", null);
			File testingData = File.createTempFile("kFoldTestingData", null);
			trainingData.deleteOnExit();
			testingData.deleteOnExit();
			
			
			int testFoldStartIx = foldIx * partitionSize;
			
			int testFoldEndIx = testFoldStartIx+partitionSize;
			
			//last case? deal with the extra elements in last fold
			if(foldIx >= (k-1)){
				//add remaiing elements to index
				testFoldEndIx += partitionSize%k;
			}
			FileHandler.subFile(datasetFile, trainingData.toPath(), testingData.toPath(), testFoldStartIx,testFoldEndIx);
			
			FoldInfo info = new FoldInfo(datasetFile, trainingData.toPath(), testingData.toPath(), testFoldStartIx,testFoldEndIx,numLines);
			consummer.consumFold(trainingData.toPath(), testingData.toPath(),info);
			
		}
	}
	
	
	/**
	 * Partitions an input file into a sub file that contains the select set
	 * of lines removed. The input file's content is unchanged.
	 * 
	 * @param inputFile input file to partition
	 * @param subFile path to partition file that will have the original file's removed lines (desired file subset)
	 * @param fromLineIx the line index to start removing from until the end of the file
	 * @throws IOException
	 */
	public static void subFile(Path inputFile,Path subFile, int fromLineIx) throws IOException{
		int numLines = FileHandler.countLines(inputFile.toString());
		subFile(inputFile,subFile,fromLineIx,numLines);
	}
	
	/**
	 * Partitions an input file into a sub file that contains the select set
	 * of lines removed. The input file's content is unchanged.
	 * 
	 * @param inputFile input file to partition
	 * @param subFile path to partition file that will have the original file's removed lines (desired file subset)
	 * @param fromLineIx the line index to start removing from until toLineIx (excluding this lie)
	 * @param toLineIx the line index to stop the partion at
	 * @throws IOException
	 */
	public static void subFile(Path inputFile,Path subFile, int fromLineIx, int toLineIx) throws IOException{
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(inputFile.toFile()), Charset.forName("UTF-8")));
		int i = 0;
		String line = reader.readLine();
		
		FileAppender out = new FileAppender(subFile);
		
		out.open();
		while (line != null) {

			//in sub file line index interval?
			if((i >= fromLineIx) && (i <toLineIx)){
				
				//append to sub file
				out.append(line+TweetParser.NEW_LINE);
			}
			
			// read next line
			line = reader.readLine();
			
			i++;
		}
		
		out.flush();
		
		reader.close();
		out.close();
	}
	
	/**
	 * Partitions an input file into two partitions: a) a new file with the original file's content
	 * but with a select set of lines removed and b) a new file that contains the select set
	 * of lines removed. The input file's content is unchanged.
	 * 
	 * @param inputFile input file to partition
	 * @param p1 path to partition file that will have the original file's content, but with the selected lines removed
	 * @param p2 path to partition file that will have the original file's removed lines
	 * @param fromLineIx the line index to start removing from until the end of the file
	 * @throws IOException
	 */
	public static void subFile(Path inputFile,Path p1,Path p2, int fromLineIx) throws IOException{
		int numLines = FileHandler.countLines(inputFile.toString());
		subFile(inputFile,p1,p2,fromLineIx,numLines);
	}
	
	/**
	 * Partitions an input file into two partitions: a) a new file with the original file's content
	 * but with a select set of lines removed and b) a new file that contains the select set
	 * of lines removed. The input file's content is unchanged
	 * 
	 * @param inputFile input file to partition
	 * @param p1 path to partition file that will have the original file's content, but with the selected lines removed
	 * @param p2 path to partition file that will have the original file's removed lines
	 * @param fromLineIx the line index to start removing from
	 * @param toLineIx the line index to stop removing at (does not include line at this index in p2)
	 * @throws IOException
	 */
	public static void subFile(Path inputFile,Path p1,Path p2, int fromLineIx, int toLineIx) throws IOException{
		
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(inputFile.toFile()), Charset.forName("UTF-8")));
		int i = 0;
		String line = reader.readLine();
		
		FileAppender p1Out = new FileAppender(p1);
		FileAppender p2Out = new FileAppender(p2);
		
		p1Out.open();
		p2Out.open();
		while (line != null) {

			//in sub file line index interval?
			if((i >= fromLineIx) && (i <toLineIx)){
				
				//append to sub file
				p2Out.append(line+TweetParser.NEW_LINE);
			}else{//we are in the normal file's interval
				
				//append to the new 'original-file'
				p1Out.append(line+TweetParser.NEW_LINE);
			}
			
			// read next line
			line = reader.readLine();
			
			i++;
		}
		
		p1Out.flush();
		p2Out.flush();
		reader.close();
		p1Out.close();
		p2Out.close();
		
	}

	public static void randomPermutation(Path src, Path dst) throws IOException {
		int numLines = FileHandler.countLines(src.toString());
		
		//list to hold the lines in files
		List<String> lines = new ArrayList<String>(numLines);
		
		
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(new  FileInputStream(src.toFile()), Charset.forName("UTF-8")));
		String line = reader.readLine();
		while (line != null) {

			lines.add(line);

			// read next line
			line = reader.readLine();
		}
		reader.close();
		
		//shuffle the file's lines
		Collections.shuffle(lines);
		
		//write the lines back to file
		FileAppender out = new FileAppender(dst);
		out.open();
		
		for(String l : lines){
			out.append(l+TweetParser.NEW_LINE);
		}
		out.flush();
		out.close();
		
	}
}
