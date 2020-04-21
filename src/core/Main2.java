package core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import classify.TweetParser;
import io.FileHandler;

public class Main2 {

	public Main2() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String [] args) throws IOException{
	
	/*	Path res = Paths.get(args[0],"res.csv");
		FileHandler.createFile(res);
		for(int i = 0; i<10;i++){
			
			Path p = Paths.get(args[0],i+".txt");
			parseResultFile(p.toString(),res);
		}
		*/
		
		System.out.println("looking for results in "+args[0]);
		if(args[0] == null){
			return;
		}
		
		String[] experimentDirs = dirs(args[0]);
		
		Path dir = new File(args[0]).toPath().getFileName();
		String dirName = dir.toString();
		
		String output = "";
		//iterate all experiment directories
		for(String p : experimentDirs){
		
			System.out.println("exp loop: "+p);
			String experimentId = dirName +"/"+(new File(p).toPath().getFileName().toString());
			
			
			Path expPath = Paths.get(args[0],p);
			//iterate sub experiments (per dataset)
			String [] datasetExpDirs = dirs(expPath.toString());
			
			for(String datasetDir : datasetExpDirs){
				System.out.println("dataset loop: "+datasetDir);
				String dataset = null;
				if(datasetDir.equals("air-line")){
					dataset="AIRLINE";
				}else if(datasetDir.equals("dataset3")){
					dataset="DATASET3";
				}else if(datasetDir.equals("stanford")){
					dataset="STANFORD";
				}else{
					System.out.println("unkjnown dataset dir: "+datasetDir);
					continue;
				}
				
				Path datasetPath = Paths.get(expPath.toString(),datasetDir);
				//now iterate the results of classifiers
				String [] classifierDirs = dirs(datasetPath.toString());
				for(String classifierDir : classifierDirs){
					System.out.println("exploring results in dir "+classifierDir);
					
					String classifier = null;
					if(classifierDir.startsWith("max")){
						classifier="MaxEnt";
					}else if(classifierDir.equals("nb")){
						classifier="NB";
					}else if(classifierDir.equals("svm")){
						classifier="SVM";
					}else{
						System.out.println("unkjnown classifier dir");
						continue;
					}
					
					
					//now read the result
					Path path = Paths.get(Paths.get(datasetPath.toString(),classifierDir).toString(), "res-10f-cv.txt");
					if(path.toFile().exists()){
						String acc = parseAccuracy(path);
						output+= experimentId+","+dataset+",-1,10,"+classifier+",train,test,"+acc+TweetParser.NEW_LINE;
					}else{
						System.out.println("not found: "+path.toString());
					}
					
					//now read the result
					path = Paths.get(Paths.get(datasetPath.toString(),classifierDir).toString(), "res-test-stanford-data.txt");
					if(path.toFile().exists()){
						String acc = parseAccuracy(path);
						output+= experimentId+","+dataset+",-1,-1,"+classifier+",train,test,"+acc+TweetParser.NEW_LINE;
					}else{
						System.out.println("not found: "+path.toString());
					}
					
					
				}
				
				//now search for fusion result
		/*		Path fusionPath = Paths.get(datasetPath.toString(), "fusion.txt");
				if(fusionPath.toFile().exists()){
					String acc = parseAccuracy(fusionPath);
					output+= experimentId+","+dataset+",-1,-1,Fusion,train+dev,test,"+acc+TweetParser.NEW_LINE;
					
				}
*/				
				//now search for fusion result
			/*	Path fusionPath = Paths.get(datasetDir, "nlp.txt");
				if(fusionPath.toFile().exists()){
					
				}*/
				
			}
		}
		
		System.out.println(output);
		
	}
	
	public static String parseAccuracy(Path path) throws IOException{
		//read file and parse accuracy
		String input = FileHandler.readFile(path.toString());
		String [] lines = input.split(TweetParser.NEW_LINE);
		String accuracy = null;
		for(String line : lines){
			if(line.contains("Accuracy/micro-averaged")){
				String [] words=  line.split(TweetParser.WHITE_SPACE_REGEX);
				accuracy =words[words.length-1]; 
				
			}
		}
	
	return accuracy;
	}
	public static String [] dirs(String path){
		File file = new File(path);
		return file.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
			});
	}
	public static void parseResultFile(String filePath,Path res) throws IOException{
	
	//String filePath = args[0];
	String rawResults = FileHandler.readFile(filePath);
	Path outPath = Paths.get(filePath);
	String [] lines = rawResults.split(TweetParser.NEW_LINE);
	
	HashMap<String,String> map = new HashMap<String,String>();
	String classifier = null;
	String accuracy = null;
	Path p = Paths.get(filePath);
	String experimentId=p.getParent().getFileName()+"/"+outPath.getFileName().toString();
	//String output = "experiment id,classifier,training data,testing data,accuracy"+TweetParser.NEW_LINE;
	String output = "";
	String trainingData = "train";
	String testingData = "test";
	for(String line : lines){
		
		
		if(line.startsWith("Name")){
			String [] words = line.split(TweetParser.WHITE_SPACE_REGEX);
			classifier = words[words.length-1];
			if(map.containsKey(classifier)){
				 trainingData = "train";
				 testingData = "dev";
			}else{
				map.put(classifier,classifier);
			}
		}else if(line.startsWith("macro recall")){
			String [] words = line.split(TweetParser.WHITE_SPACE_REGEX);
			accuracy = words[words.length-1];
			output +=experimentId+","+classifier+",5,10,"+trainingData+","+testingData+","+accuracy+TweetParser.NEW_LINE;
		}
	}
	
	
	FileHandler.append(res, output.getBytes());

	System.out.println("parsed results: "+outPath.toString());
}

}
