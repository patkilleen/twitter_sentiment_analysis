package io;

/**
 * Interface used to allow client objects to access a process' output line by line.
 * @author Patrick Killeen
 *
 */
public interface ProcessStreamReader {
	public void readLine(String line);
}
