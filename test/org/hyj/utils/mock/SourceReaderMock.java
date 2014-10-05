/**
 * 
 */
package org.hyj.utils.mock;

import java.io.IOException;
import java.util.logging.Logger;

import org.hyj.utils.io.SourceReader;

/**
 * @author Yujie Huang
 * 
 */
public class SourceReaderMock implements SourceReader {

	
	private static final Logger LOGGER = Logger
			.getLogger(SourceReaderMock.class.getName());
	
	private char[] source;
	private int index;
	public SourceReaderMock(String source) {
		this.source = source.toCharArray();
		this.index = 0;
		LOGGER.fine("Total input length: " + this.source.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hyj.utils.io.SourceReader#readChar()
	 */
	@Override
	public char readChar() throws IOException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hyj.utils.io.SourceReader#readChar(char[], int)
	 */
	@Override
	public int readChar(char[] input, int len) throws IOException {
		if (index == source.length) {
			return 0;
		}

		int actual = Math.min(Math.min(input.length, len), source.length
				- index);
		System.arraycopy(source, index, input, 0, actual);
		index += actual;
		return actual;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hyj.utils.io.SourceReader#readChar(char[], int, int)
	 */
	@Override
	public int readChar(char[] input, int offset, int len) throws IOException {
		if (index == source.length) {
			return 0;
		}

		int actual = Math.min(Math.min(input.length - offset, len),
				source.length - index);
		System.arraycopy(source, index, input, offset, actual);
		return actual;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hyj.utils.io.SourceReader#readLine()
	 */
	@Override
	public String readLine() throws IOException {
		return null;
	}

}
