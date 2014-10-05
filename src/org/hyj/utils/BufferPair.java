/**
 * 
 */
package org.hyj.utils;

import java.io.EOFException;
import java.io.IOException;
import java.util.logging.Logger;

import org.hyj.utils.io.SourceReader;

/**
 * Paired buffer.
 * 
 * @author Yujie Huang
 * 
 */
public class BufferPair implements Buffer {

	private static final Logger LOGGER = Logger.getLogger(BufferPair.class
			.getName());

	private static final int DEFAULT_BUFFER_LENGTH = 4096;

	private static final char EOF = 0;

	private final int bufferArraySize;

	private final int bufferCount;

	private final int bufferSize;

	private final SourceReader reader;

	private final char[][] buffer;

	// whether the specific buffer is loaded with data.
	private final boolean[] isLoaded;

	private int begin = 0;

	private int forward = -1;

	// The buffer just load the data
	private int currentBuffer = 0;

	public BufferPair(SourceReader reader) throws IOException {
		this(reader, DEFAULT_BUFFER_LENGTH);
	}

	public BufferPair(SourceReader reader, int bufferSize) throws IOException {
		this.reader = reader;
		this.bufferSize = bufferSize;
		bufferArraySize = bufferSize + 1;
		bufferCount = 2;
		this.buffer = new char[bufferCount][bufferArraySize];
		for (char[] singleBuffer : buffer) {
			singleBuffer = new char[bufferArraySize];
			singleBuffer[this.bufferSize] = EOF;
		}
		this.isLoaded = new boolean[bufferCount];
		reloadBuffer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hyj.utils.Buffer#next()
	 */
	@Override
	public char next() throws OutOfBufferException, IOException {
		++forward;
		char currentChar = getCurrentChar();
		if (currentChar == EOF) {
			if (hasReachEndOfBuffer()) {
				// Run out of one buffer
				// Switch buffer to another one.
				try {
					switchBuffer();
				} catch (EOFException e) {
					forward--;// Rollback forward in case client wants to
								// retract
					throw e;
				}
				forward = (++forward) % (bufferArraySize * bufferCount);
				return getCurrentChar();
			}
			// Get end of file in the middle of buffer
			throw new EOFException();
		}
		return currentChar;
	}

	// Get the current reading char from the buffer
	private char getCurrentChar() {
		return buffer[getX(forward)][getY(forward)];
	}

	private boolean hasReachEndOfBuffer() {
		return forward % bufferArraySize == bufferSize;
	}

	private void switchBuffer() throws OutOfBufferException, IOException {
		if (getX(begin) != getX(forward)) {
			// begin is not in current buffer;
			// two buffers are running out.
			throw new OutOfBufferException();
		}
		// Only someone retract so that forward can be not in current buffer.
		if (getX(forward) != currentBuffer) {
			LOGGER.fine("Buffer " + currentBuffer
					+ " already loaded, no need to load");
			return;
		}
		currentBuffer = (currentBuffer + 1) % bufferCount;
		reloadBuffer();
	}

	// Reload one buffer from the source
	private void reloadBuffer() throws IOException {
		// if (isLoaded[currentBuffer]) {
		// LOGGER.fine("Buffer " + currentBuffer
		// + " already loaded, no need to load");
		// return;
		// }
		LOGGER.fine("Buffer reloaded when begin = " + begin + ", forward= "
				+ forward);
		int actualCharCount = reader
				.readChar(buffer[currentBuffer], bufferSize);
		if (actualCharCount == 0) {
			throw new EOFException();
		}
		if (actualCharCount != bufferSize) {
			buffer[currentBuffer][actualCharCount] = EOF;
		}
		isLoaded[currentBuffer] = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hyj.utils.Buffer#retract()
	 */
	@Override
	public void retract() throws NoCharToRetractException {
		if (begin - 1 == forward) {
			throw new NoCharToRetractException();
		}
		// foward goes backward
		--forward;
		if (forward > 0 && buffer[getX(forward)][getY(forward)] == EOF) {
			--forward;
		}
		if (forward == -1 && begin != 0) {
			forward = bufferArraySize * 2 - 2;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hyj.utils.Buffer#retractAll()
	 */
	@Override
	public void retractAll() {
		forward = begin - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hyj.utils.Buffer#extract()
	 */
	@Override
	public String extract() {
		if (begin - 1 == forward) {
			return "";
		}
		StringBuilder token = new StringBuilder(bufferSize * bufferCount);
		for (int i = begin;; i = (i + 1) % (bufferArraySize * bufferCount)) {
			char current = buffer[getX(i)][getY(i)];
			if (current != EOF) {
				token.append(current);
			}
			if (i == forward) {
				return token.toString();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hyj.utils.Buffer#restart()
	 */
	@Override
	public void restart() {
		int beginBufferIndex = getX(begin);
		int forwardBufferIndex = getX(forward);
		if (beginBufferIndex != forwardBufferIndex) {
			isLoaded[beginBufferIndex] = false;
		}
		// Moves begin to next of forward
		begin = (forward + 1) % (bufferArraySize * bufferCount);
	}

	private int getX(int index) {
		return index / (bufferArraySize);
	}
	private int getY(int index) {
		return index % (bufferArraySize);
	}

	public String debugInfo() {
		StringBuilder debugInformation = new StringBuilder();
		debugInformation.append("Buffer[0]: ");
		for (char c : buffer[0]) {
			if (c == EOF) {
				debugInformation.append("EOF").append(' ');
			} else {
				debugInformation.append(c).append(' ');
			}
		}
		if (isLoaded[0]) {
			debugInformation.append("loaded");
		} else {
			debugInformation.append("not loaded");
		}
		debugInformation.append('\n');
		debugInformation.append("Buffer[1]: ");
		for (char c : buffer[1]) {
			if (c == EOF) {
				debugInformation.append("EOF").append(' ');
			} else {
				debugInformation.append(c).append(' ');
			}
		}
		if (isLoaded[1]) {
			debugInformation.append("loaded");
		} else {
			debugInformation.append("not loaded");
		}
		debugInformation.append('\n');
		debugInformation.append("begin: ").append(begin).append('\n');
		debugInformation.append("forward: ").append(forward).append('\n');
		debugInformation.append("currentBuffer: ").append(currentBuffer)
				.append('\n');

		return debugInformation.toString();
	}

}
