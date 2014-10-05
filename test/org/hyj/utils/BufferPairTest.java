package org.hyj.utils;

import java.io.EOFException;
import java.io.IOException;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.hyj.test.JunitTestBase;
import org.hyj.utils.io.SourceReader;
import org.hyj.utils.mock.SourceReaderMock;
import org.junit.After;
import org.junit.Test;

public class BufferPairTest extends JunitTestBase {

	private static final Logger LOGGER = Logger.getLogger(BufferPairTest.class
			.getName());

	private SourceReader sourceReader;
	private BufferPair buffer;
	private void init(String input, int bufferSize) throws IOException {
		sourceReader = new SourceReaderMock(input);
		buffer = new BufferPair(sourceReader, bufferSize);
	}
	@After
	public void tearDown() {
		buffer = null;
		sourceReader = null;
	}
	@Test
	public void testNext1() throws Exception {
		String input = "abcdefghijkelmn";
		init(input, 10);
		int i = 0;
		try {
			while (true) {
				char actual = buffer.next();
				Assert.assertEquals("Buffer of index: " + i + " is wrong",
						input.charAt(i), actual);
				i++;
			}
		} catch (EOFException e) {
			LOGGER.fine("Finished");
			Assert.assertEquals(input.length(), i);
		}
	}
	@Test
	public void testNext2() throws Exception {
		String input = "abcdefghijkelmn";
		init(input, 7);
		int i = 0;
		try {
			while (true) {
				char actual = buffer.next();
				Assert.assertEquals("Buffer of index: " + i + " is wrong",
						input.charAt(i), actual);
				i++;
			}
		} catch (EOFException e) {
			Assert.fail("Should not come to EOF");
		} catch (OutOfBufferException e) {
			Assert.assertEquals(14, i);

		}
	}

	@Test
	public void testNormal1() throws Exception {
		String input = "abcdefghijkelmn";
		init(input, 7);
		// [a, b, c, d, e, f, g, EOF]
		// [h, i, j, k, e, l, m, EOF]
		// [n, EOF EOF]
		try {
			next(buffer, 3);
			Assert.assertEquals("abc", buffer.extract());
			buffer.retract();
			Assert.assertEquals("ab", buffer.extract());
			buffer.restart();
			next(buffer, 5);
			/**
			 * Buffer[0]: a b c d e f g EOF loaded Buffer[1]: EOF EOF EOF EOF
			 * EOF EOF EOF EOF not loaded begin: 2 forward: 6 currentBuffer: 0
			 */
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("cdefg", buffer.extract());
			next(buffer, 1);
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("cdefgh", buffer.extract());
			buffer.retract();
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("cdefg", buffer.extract());
			next(buffer, 1);
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("cdefgh", buffer.extract());
			buffer.retractAll();
			next(buffer, 6);
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("cdefgh", buffer.extract());
			buffer.restart();
			LOGGER.finest(buffer.debugInfo());
			next(buffer, 7);
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("ijkelmn", buffer.extract());
			try {
				next(buffer, 1);
			} catch (EOFException e) {
				return;
			}
			LOGGER.finest(buffer.debugInfo());
			Assert.fail("Expect EOF");
		} catch (EOFException e) {
			e.printStackTrace();
			Assert.fail("Should not come to EOF");
		} catch (OutOfBufferException e) {
			e.printStackTrace();
			Assert.fail("Should not come to outof buffer");

		}
	}
	private void next(Buffer buffer, int count) throws Exception {
		for (int i = 0; i < count; i++) {
			buffer.next();
		}
	}
	private void retract(Buffer buffer, int count) throws Exception {
		for (int i = 0; i < count; i++) {
			buffer.retract();
		}
	}

	@Test
	public void testNormal2() throws Exception {
		String input = "abcdefghijkelm";
		init(input, 7);
		boolean exception = false;
		// [a, b, c, d, e, f, g, EOF]
		// [h, i, j, k, e, l, m, EOF]
		// [n, EOF EOF]
		try {
			next(buffer, 3);
			Assert.assertEquals("abc", buffer.extract());
			buffer.retract();
			Assert.assertEquals("ab", buffer.extract());
			buffer.restart();
			next(buffer, 5);
			/**
			 * Buffer[0]: a b c d e f g EOF loaded Buffer[1]: EOF EOF EOF EOF
			 * EOF EOF EOF EOF not loaded begin: 2 forward: 6 currentBuffer: 0
			 */
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("cdefg", buffer.extract());
			next(buffer, 1);
			Assert.assertEquals("cdefgh", buffer.extract());
			LOGGER.finest(buffer.debugInfo());
			buffer.retract();
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("cdefg", buffer.extract());
			next(buffer, 1);
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("cdefgh", buffer.extract());
			buffer.retractAll();
			exception = false;
			try {
				buffer.retract();
			} catch (NoCharToRetractException e) {
				exception = true;
			}
			Assert.assertTrue("Should get a no char to retract", exception);
			next(buffer, 6);
			Assert.assertEquals("cdefgh", buffer.extract());
			buffer.restart();
			LOGGER.finest(buffer.debugInfo());
			next(buffer, 6);
			Assert.assertEquals("ijkelm", buffer.extract());
			LOGGER.finest(buffer.debugInfo());
			exception = false;
			try {
				System.out.println(buffer.next());
			} catch (EOFException e) {
				exception = true;
			}
			Assert.assertTrue("Should get eof", exception);
			Assert.assertEquals("ijkelm", buffer.extract());
			buffer.retract();
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("ijkel", buffer.extract());
			retract(buffer, 5);
			LOGGER.finest(buffer.debugInfo());
			Assert.assertEquals("", buffer.extract());
			exception = false;
			try {
				buffer.retract();
			} catch (NoCharToRetractException e) {
				exception = true;
			}
			LOGGER.finest(buffer.debugInfo());
			Assert.assertTrue("Should get a no char to retract", exception);
		} catch (EOFException e) {
			e.printStackTrace();
			Assert.fail("Should not come to EOF");
		} catch (OutOfBufferException e) {
			e.printStackTrace();
			Assert.fail("Should not come to outof buffer");

		}
	}
	@Test
	public void testRetractInFirstBuffer() throws Exception {
		// [a, b, c, d, e, f, g, EOF] [n, o, p, q, r, s,EOF,EOF]
		// [h, i, j, k, e, l, m, EOF]
		String input = "abcdefghijkelmnopqrs";
		init(input, 7);
		next(buffer, 12);
		LOGGER.finest(buffer.debugInfo());
		Assert.assertEquals("abcdefghijke", buffer.extract());
		buffer.restart();
		next(buffer, 5);
		LOGGER.finest(buffer.debugInfo());
		Assert.assertEquals("lmnop", buffer.extract());
		retract(buffer, 3);
		LOGGER.finest(buffer.debugInfo());
		Assert.assertEquals("lm", buffer.extract());
		retract(buffer, 2);
		LOGGER.finest(buffer.debugInfo());
		Assert.assertEquals("", buffer.extract());
		// next(buffer, 3);
		// Assert.assertEquals("lmnop", buffer.extract());
		LOGGER.finest(buffer.debugInfo());
		buffer.restart();
		next(buffer, 3);
		LOGGER.finest(buffer.debugInfo());
		Assert.assertEquals("lmn", buffer.extract());
		next(buffer, 2);
		Assert.assertEquals("lmnop", buffer.extract());
		next(buffer, 2);
		Assert.assertEquals("lmnopqr", buffer.extract());
		retract(buffer, 2);
		Assert.assertEquals("lmnop", buffer.extract());

	}

	@Test
	public void testDefaultConstructor() throws Exception {
		StringBuilder sb = new StringBuilder(4096 * 2);
		for (int i = 0; i < 4096; i++) {
			sb.append('a');
		}
		for (int i = 0; i < 4096; i++) {
			sb.append('b');
		}
		sourceReader = new SourceReaderMock(sb.toString());
		buffer = new BufferPair(sourceReader);
		next(buffer, 4096 * 2);
		boolean exception = false;
		try {
			buffer.next();
		} catch (OutOfBufferException e) {
			exception = true;
		}
		LOGGER.finest(buffer.debugInfo());
		Assert.assertTrue(exception);
	}
	@Test
	public void testRetractTheFirstChar() throws Exception {
		init("aab", 4);
		next(buffer, 1);
		Assert.assertEquals("a", buffer.extract());
		buffer.retract();
		Assert.assertEquals("", buffer.extract());
	}

}
