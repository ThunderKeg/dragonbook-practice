/**
 * 
 */
package org.hyj.test;

import java.util.logging.LogManager;

import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Yujie Huang
 * 
 */
public abstract class JunitTestBase {

	@BeforeClass
	public static void beforeClass() throws Exception {
		System.setProperty("java.util.logging.config.file",
				"logging.properties");
		LogManager.getLogManager().readConfiguration();
	}

	@Before
	public void setUp() throws Exception {
		initTestCase();

	}
	protected void initTestCase() throws Exception {

	}

}
