/**
 * 
 */
package org.hyj.utils.io;

import java.io.IOException;

/**
 * @author Yujie Huang
 * Unified definition used to read characters from source code
 */
public interface SourceReader {
	
	/**
	 * read one char from source code
	 * @return the char read
	 * @throws IOException I/O error, should be an unrecoverable error, future operation should terminate
	 */
	char readChar() throws IOException;
	
	/**
	 * read a sequence of char from source code
	 * @param input where to store the characters
	 * @param len how many characters to read
	 * @return the actual count that read. If nothing read or reach the end of file, zero will be returned.
	 * @throws IOException I/O error, should be an unrecoverable error, future operation should terminate
	 */
	int readChar(char[] input,int len) throws IOException;
	
	/**
	 * read a sequence of char from source code
	 * the char would fill in the input from input[offset] to input[offset + actual - 1]
	 * Actual is the actual read count which must be less or equal than <code>len</code>
	 * @param input where to store the characters
	 * @param len how many characters to read
	 * @return the actual count that read. If nothing read or reach the end of file, zero will be returned.
	 * @throws IOException I/O error, should be an unrecoverable error, future operation should terminate
	 */
	int readChar(char[] input,int offset, int len) throws IOException;
	
	/**
	 * Read a line from source code
	 * @return the line read, if nothing read null is returned
	 * @throws IOException I/O error, should be an unrecoverable error, future operation should terminate
	 */
	String readLine() throws IOException;
}
