package tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/** JUnit Platform suite entry point for the Karate execution test. */
@Suite
@SuiteDisplayName("Karate E2E suite")
@SelectClasses(KarateExecutionSpec.class)
public class KarateRunnerTest {}
