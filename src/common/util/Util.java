package common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import io.IConfig;

public class Util {

	public Util() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Verifies that a path is not empty. Will throw a IllegalArgumentException if the
	 * provided path is null or empty
	 * 
	 *  @param path Path to verify non-emptiness
	 * 
	 */
	public static void verifyPath(Path path){
		if (path == null || path.toString().equals("")){
			//throw new IllegalArgumentException("Cannot create classifier builder with empty StandforNLP Library JAR path or empty properties file");
			throw new IllegalArgumentException("path validation check failed for path: "+path);
		}
	}
	
	
	/**
	 * Verifies if a file path is valid.
	 * @param path path to file
	 * @return true when the file exists, false otherwise.
	 */
	public static boolean isValidFilePath(String path){
		if (path == null || path.toString().equals("")){
			return false;
		}
		
		try{
			File f = new File(path);
		
			return f.exists();
		}catch(Exception e){
			return false;
		}
	}
	/**
	 * Returns true when the provided list has nothing in it.
	 * @param target list to check
	 * @return true when list is null or empty, and false when it has elements in it.
	 */
	@SuppressWarnings({"rawtypes" })
	public static boolean isNullOrEmpty(List target){
		if((target == null) || target.isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Fetches a relative path from a configuration file by using a key.
	 * The relative path will be retreived and  parsed into an absolute path
	 * from the root path (the root path is found in the configuration file). 
	 * @param config configuration object with the desired path 
	 * 
	 * @throws IllegalArgumentException thrown if parsing the path fails.
	 * 
	 * @param pathKey key to relative path
	 * @return Path object 
	 */
	public static Path parseAndVerifyPath(IConfig config, String pathKey){
		if(config == null){
			throw new IllegalArgumentException("path validation check failed for path key "+pathKey+" due to null config"); 
		}
		Path path = Paths.get(config.getProperty(IConfig.PROPERTY_ROOT_PATH),config.getProperty(pathKey));
		Util.verifyPath(path);
		return path;
	}
	
	/**
	 * Returns a list of the unique elements found in a list.
	 * @param <E> type of elements in list. The element must implement the equals method
	 * @param list list of elements to extract the unique elements from
	 * @return list of unique elements found in the list or null if the provided list is null. 
	 */
	public static <E> List<E> unique(List<E> list){
		
		if(list == null){
			return null;
		}
		
		if(list.size() == 0){
			return new ArrayList<E>(0);
		}
		
		 List<E> uniqueRes = new ArrayList<E>(list.size());

		 //iterate the elements and add element to result if it hasn't been added yet
		 for(E e: list){
		     if(!uniqueRes.contains(e)){
		      	uniqueRes.add(e);
		     }
		 }
		return uniqueRes;
	}
		
	/**
	 * Returns true when the list contain no duplicate elements and false if there are duplicates
	 * @param <E> type of elements in list. The element must implement the equals method
	 * @param list list to verify the uniqueness of elements contained
	 * @return true when all elements are unique or when the list is null, and false otherwise
	 */
	public static <E> boolean allElementsUnique(List<E> list){
		if(list == null || list.size() <= 1){
			return true;
		}
		
		return Util.unique(list).size() == list.size();
	}

	public static boolean verifyPath(String devDatasetPathStr) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Tries to resolve a path from a configurtion object given a key
	 * Although, if the path fails to be parsed as an absolute path
	 * then its attempts to be parsed as relative path (relative to
	 * the root of this project) 
	 * @param config confirguration
	 * @param pathKey key to file path in config
	 * @return path to file
	 */
	public static Path tryParsePath(IConfig config, String pathKey){
		Path res = null;
		String potentialPathStr = config.getProperty(pathKey);
		
		//check for validity as an absolute path
		if(!Util.isValidFilePath(potentialPathStr)){
			//last option: try relative path
			res = Util.parseAndVerifyPath(config,pathKey);
		}else{
			res = new File(potentialPathStr).toPath();
		}
		
		return res;
	}
}
