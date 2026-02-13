package com.coffee;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "html:build/reports/cucumber/cucumber-report.html")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:build/reports/cucumber/cucumber.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.coffee.steps,com.coffee.config")
public class CucumberTestRunner {
}
