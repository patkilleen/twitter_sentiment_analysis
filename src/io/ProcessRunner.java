package io;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.log.Logger;
import io.log.LoggerFactory;



public class ProcessRunner implements ProcessStreamReader //implements this since it will read the error stream
{
	ProcessStreamReader reader;
	
	String error;
	/**
	* @param reader the object that wants to read a command's output line by line
	*/
	public ProcessRunner( ProcessStreamReader reader){
		this.reader = reader;
	}
	
	/**
	*Runs a command, and sends the std output to reader. The error stream is read by this object
	* the error code of process is returned. Error and standard output is read concurrently
	* @param cmd Shell command to be executed.
	*/
	public int exec(String cmd){
		error="";
	
        try
        {            

        	Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            // any error message?
            StreamGobbler errorGobbler = new 
                StreamGobbler(proc.getErrorStream(),this);            
            
            // any output?
            StreamGobbler outputGobbler = new 
                StreamGobbler(proc.getInputStream(),reader);
                
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
                                    
            // any error???
            int exitVal = proc.waitFor();  
			return exitVal;			
        } catch (Throwable t)
          {
            t.printStackTrace();
            return -1;
          }
        
     
    }
	
	public String getErrorMessage(){
		return error;
	}
	
	@Override
	public void readLine(String line) {
		// TODO Auto-generated method stub
		error+=line;
	//	Logger log = LoggerFactory.getInstance();
		//log.log_debug("error:" +line);
	}
	
	

}