package io.log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Logger {

	private static final int DEBUG=0;
	private static final int INFO=1;
	private static final int WARNING=2;
	private static final int ERROR=3;
	private static final int FATAL=4;
	
	private static final String [] LOG_LEVEL_LABLES = {"DEBUG","INFO","WARNING","ERROR","FATAL"};
	
	private final static String COLON = ":";
	private final static String DOUBLE_COLON = "::";
	private final static String DOT = ".";
	private final static String WHITESPACE = " ";
	private final static String CLOSING_SQUARE_BRACKET = "]";
	private final static String OPENING_SQUARE_BRACKET = "[";
	private final static String NEW_LINE = System.lineSeparator();
		
	/**
	 * Uses to filter logging levels.
	 */
	public static int GLOBAL_LOG_LEVEL = DEBUG;
	
	private List<OutputStream> outputStreams;
	
	private boolean enabled;
	public Logger(){
		outputStreams = new ArrayList<OutputStream>();
		enabled = true;

	}
	
	
	public static void setLogLevel(String lvl){
		for(int i = 0;i<LOG_LEVEL_LABLES.length;i++){
			
			String candidate = LOG_LEVEL_LABLES[i];
			//found the log level?
			if(candidate.equals(lvl)){
				GLOBAL_LOG_LEVEL=i;//to log level integer
				return;
			}
		}
	}
	public void addOutputStream(OutputStream out){
		synchronized(outputStreams){
			outputStreams.add(out);
		}
	}
	

	public void clearOutputStreams(){
		synchronized(outputStreams){
			outputStreams.clear();
		}
	}
	
	public void enable(){
		this.enabled = true;
	}
	
	public void disable(){
		this.enabled = false;
	}
	
	protected void log(int level, String msg){
		
		//only log when enabled
		if(!this.enabled){
			return;
		}
		
		Thread thisThread = Thread.currentThread(); 
		//get the 2nd trace elemt (skip the functio ncall of log_xxxx)
		StackTraceElement trace = thisThread.getStackTrace()[3];

		String output = OPENING_SQUARE_BRACKET + LOG_LEVEL_LABLES[level] + CLOSING_SQUARE_BRACKET + //[LOG LEVEL]
				COLON + WHITESPACE + OPENING_SQUARE_BRACKET + thisThread.getId() + CLOSING_SQUARE_BRACKET + //: [THREAD_ID]::
				DOUBLE_COLON + trace.getClassName()+ DOT + trace.getMethodName() + DOUBLE_COLON + trace.getLineNumber() + COLON + msg + NEW_LINE; //::class.method::line num : <msg>
		
		//lock the output stream
		synchronized(outputStreams){
						
			byte[] bytes = output.getBytes();
			//write the message to all streams
			for(OutputStream outStream : outputStreams){
				
				//don't want to have all caller implement the try catch, so this will just print errors
				try {
					outStream.write(bytes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		
			
		}	//end sync otut streams	
		
	}
	
	
	public  void log_debug(String msg){
		if(DEBUG >= GLOBAL_LOG_LEVEL){
			log(DEBUG,msg);
		}
	}
	
	public  void log_info(String msg){
		if(INFO >= GLOBAL_LOG_LEVEL){
			log(INFO,msg);
		}
	}
	
	public  void log_warning(String msg){
		if(WARNING >= GLOBAL_LOG_LEVEL){
			log(WARNING,msg);
		}
	}
	
	public  void log_error(String msg){
		if(ERROR >= GLOBAL_LOG_LEVEL){
			log(ERROR,msg);
		}
	}
	
	public  void log_fatal(String msg){
		if(FATAL >= GLOBAL_LOG_LEVEL){
			log(FATAL,msg);
		}
	}


}
