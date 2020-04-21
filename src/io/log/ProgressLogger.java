package io.log;

public  class ProgressLogger{
	private int loggingFrequency;
	private int totalLoggingIterations;
	private int progressTicks;
	
	private Logger log;
	
	private long lastTimeTick;
	
	private long sumProgressTimeTicks;
	
	/**
	 * constructor
	 * @param loggingFrequency the number of times <code>logProgress</code> must be called to log a message 
	 * @param totalLoggingIterations the total number of times the <code>logProgress</code> function is expected to be called before a job is complete
	 */
	public ProgressLogger(int loggingFrequency, int totalLoggingIterations){
		
		this.totalLoggingIterations=totalLoggingIterations;
		progressTicks=0;
		log = LoggerFactory.getInstance();
		
		//make sure logging intervale at least 1
		if(loggingFrequency<1){
			this.loggingFrequency = 1;
		}else{
			this.loggingFrequency=loggingFrequency;
		}
	}
	
	/**
	 * constructor
	 * @param loggingFrequency the fraction of times (0,1] <code>logProgress</code> must be called to log a message 
	 * @param totalLoggingIterations the total number of times the <code>logProgress</code> function is expected to be called before a job is complete
	 */
	public ProgressLogger(double loggingFrequency, int totalLoggingIterations){	
		this((int)Math.floor(loggingFrequency * (double)totalLoggingIterations),totalLoggingIterations);	
	}
	/**
	 * will log a message with a progress-style format, but this function will only log the
	 * message if it has been called <code>loggingFrequency</code> times.
	 * The idea is to prevent logging a message at every iteration of a job,
	 * but to log messages periodically (at 10% completion intervals, for example).
	 * @param msg message to print
	 */
	public void logProgress(String msg){
		
		if(progressTicks == 0){
			lastTimeTick = System.currentTimeMillis();
		}
		progressTicks++;
		
		if((progressTicks % loggingFrequency) == 0){

			int percentage = (int)(((double)progressTicks/ (double)totalLoggingIterations)*100);
			
			long currentTime =System.currentTimeMillis();
			//time since last progress tick
			long progressTimeTick =  currentTime - lastTimeTick;
			sumProgressTimeTicks += progressTimeTick;
			
			//avgProgressTimeTicks = avgProgressTimeTicks / (double)intervalTicks;
			lastTimeTick = currentTime;
			
			double timePerPercent = (double)sumProgressTimeTicks / (double)percentage;
			long percentRemaining = (100 - percentage ); 
			
			//time remaining
			long msRemaining =  (long)(percentRemaining * timePerPercent);
			long secondsRemaining = Math.floorDiv(msRemaining,1000);//conver to seconds
			long minutesRemaining = Math.floorDiv(secondsRemaining,60);//conver to minutes
			
			String remainingTimeStr =  minutesRemaining+"m:"+(secondsRemaining%60)+"s";
			
			long msEllapses = sumProgressTimeTicks;
			long secondsEllapsed = Math.floorDiv(msEllapses,1000);//conver to seconds
			long minutesEllapsed = Math.floorDiv(secondsEllapsed,60);//conver to minutes
			
			String ellapsedTimeStr =  minutesEllapsed+"m:"+(secondsEllapsed%60)+"s";
			//time remaininng
			log.log_info(msg+". ["+percentage+"% complete], time ellapsed: ("+ellapsedTimeStr+"), estimated time remaining: ("+remainingTimeStr+")");
		}
	}
}