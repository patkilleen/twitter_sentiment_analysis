package io;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
	 * Reads a process' output and feeds it line by line to a {@code ProcessStreamReader}
	 * @author Patrick Killeen
	 *
	 */
	public  class StreamGobbler extends Thread
	{
	    InputStream is;
	    ProcessStreamReader reader;
	    public StreamGobbler(InputStream is,ProcessStreamReader reader)
	    {
	        this.is = is;
			this.reader = reader;
	    }
	    
	    public void run()
	    {
	        try
	        {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	                reader.readLine(line);
	            } catch (IOException ioe)
	              {
	                ioe.printStackTrace();  
	              }
	    }
	}