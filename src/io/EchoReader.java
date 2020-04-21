package io;

import io.log.Logger;
import io.log.LoggerFactory;

public class EchoReader implements ProcessStreamReader{
	private Logger log;
	public EchoReader() {
		log = LoggerFactory.getInstance();
	}

	@Override
	public void readLine(String line) {
		log.log_info(line);
		
	}

}
