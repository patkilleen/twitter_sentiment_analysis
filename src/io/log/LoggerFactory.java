package io.log;

public class LoggerFactory {
	
	private static Logger SINGLE_LOG_OBJECT = null;
	
	
	public static synchronized Logger getInstance(){
		if(SINGLE_LOG_OBJECT == null){
			SINGLE_LOG_OBJECT = new Logger();
		}
		return SINGLE_LOG_OBJECT;
	}
	
}
