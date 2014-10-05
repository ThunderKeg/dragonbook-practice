/**
 * 
 */
package org.hyj.utils;

import java.io.IOException;

/**
 * @author Yujie Huang
 * The buffer holding the characters from source files
 *
 */
public interface Buffer {
	
	/**
	 * get next char from the buffer
	 * @return the next char of the buffer
	 * @exception EOFException Already coming to the end of buffer
	 * @exception OutOfBufferException The buffer is full, no more character can get from the buffer
	 * @exception IOException i/o error, which can be an unrecoverable error, future operation should terminate
	 */
	char next() throws OutOfBufferException, IOException;
	
	/**
	 * retract one char to the buffer
	 * @exception NoCharToRetractException The buffer is already retracted to the starting point
	 */
	void retract() throws NoCharToRetractException;

	/**
	 * retract all char to the buffer
	 */
	void retractAll();
	
	/**
	 * Extract the token from the buffer
	 */
	String extract();
	
	/**
	 * Start next
	 */
	void restart();
}
