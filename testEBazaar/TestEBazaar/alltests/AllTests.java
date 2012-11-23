package alltests;

import homayoon.OSValidator;

import java.io.File;
import java.util.logging.Logger;

import business.externalinterfaces.RulesConfigProperties;

import middleware.DbConfigProperties;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {
	static Logger log = Logger.getLogger(AllTests.class.getName());
	private static final String LOC_DB_PROPS = "/TestEBazaar/resources/dbconfig.properties";
	private static final String LOC_RULES_PROPS = "/TestEBazaar/resources/rulesconfig.properties";
	private static final String WIN_LOC_DB_PROPS = "\\TestEBazaar\\resources\\dbconfig.properties";
	private static final String WIN_LOC_RULES_PROPS = "\\TestEBazaar\\resources\\rulesconfig.properties";
	private static final String context = computeDir();
	static {
		initializeProperties();
	}

	private static String computeDir() {
		File f = new File(System.getProperty("user.dir"));
		if (f.exists() && f.isDirectory()) {
			System.out.println("******" + f.getParent());
			return f.getParent();
		}
		return null;

	}

	@SuppressWarnings("unused")
	private static boolean initialized = false;

	public static void initializeProperties() {
		// Need to specify full path to dbconfig.properties
		// when accessing from outside the project.
		if (!initialized) {
			if (OSValidator.isWindows()) {

				DbConfigProperties.readProps(context + WIN_LOC_DB_PROPS);
				RulesConfigProperties.readProps(context + WIN_LOC_RULES_PROPS);
			} else if (OSValidator.isUnix()) {
				DbConfigProperties.readProps(context + LOC_DB_PROPS);
				RulesConfigProperties.readProps(context + LOC_RULES_PROPS);
			}
			initialized = true;
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		// $JUnit-BEGIN$ -- put fully qualified classnames of all tests here
		suite.addTest(new TestSuite(integrationtests.BrowseAndSelectTest.class));
		suite.addTest(new TestSuite(
				performancetests.RulesPerformanceTests.class));
		suite.addTest(new TestSuite(unittests.business.StringParseTest.class));
		suite.addTest(new TestSuite(
				unittests.middleware.dataaccess.SimpleConnectionPoolTest.class));

		// $JUnit-END$
		return suite;
	}

}
