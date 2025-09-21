package com.automation.testing.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.automation.testing.utils.AllureReportHelper;
import com.automation.testing.utils.ConfigManager;

import io.qameta.allure.Allure;

public abstract class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected ConfigManager configManager;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("=== Starting Test Suite ===");
        configManager = ConfigManager.getInstance();

        String environment = System.getProperty("test.environment", "dev");
        AllureReportHelper.addEnvironmentInfo(environment, configManager.getBaseUrl());

        logger.info("Test environment: {}", environment);
        logger.info("Base URL: {}", configManager.getBaseUrl());
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());
        setupTestClass();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        logger.info("Starting test method: {}", getCurrentMethodName());
        setupTestMethod();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        String methodName = getCurrentMethodName();
        logger.info("Finished test method: {} - Status: {}", methodName, getTestStatus(result));

        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test failed: {}", result.getThrowable().getMessage());
            AllureReportHelper.addStep("Test Failed",
                    io.qameta.allure.model.Status.FAILED,
                    result.getThrowable().getMessage());
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.warn("Test skipped: {}", result.getSkipCausedBy());
            AllureReportHelper.addStep("Test Skipped",
                    io.qameta.allure.model.Status.SKIPPED,
                    "Test was skipped");
        }

        teardownTestMethod();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        logger.info("Tearing down test class: {}", this.getClass().getSimpleName());
        teardownTestClass();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        logger.info("=== Test Suite Completed ===");
        teardownTestSuite();
    }

    protected void setupTestClass() {
    }

    protected void setupTestMethod() {
    }


    protected void teardownTestMethod() {
    }


    protected void teardownTestClass() {
    }

    protected void teardownTestSuite() {
    }

    protected String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    private String getTestStatus(ITestResult result) {
        return switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            default -> "UNKNOWN";
        };
    }

    protected void addTestStep(String stepName, String description) {
        Allure.step(stepName, () -> {
            logger.info("Test Step: {} - {}", stepName, description);
            if (description != null && !description.isEmpty()) {
                AllureReportHelper.attachTestData("Step Details", description);
            }
        });
    }


    protected void addTestStep(String stepName) {
        addTestStep(stepName, null);
    }

    protected void validateAndLog(boolean condition, String message) {
        if (condition) {
            logger.info("✓ Validation passed: {}", message);
            AllureReportHelper.addValidationStep("Validation", "Pass", "Pass", true);
        } else {
            logger.error("✗ Validation failed: {}", message);
            AllureReportHelper.addValidationStep("Validation", "Pass", "Fail", false);
            throw new AssertionError("Validation failed: " + message);
        }
    }

    protected boolean softValidateAndLog(boolean condition, String message) {
        if (condition) {
            logger.info("✓ Soft validation passed: {}", message);
            AllureReportHelper.addValidationStep("Soft Validation", "Pass", "Pass", true);
            return true;
        } else {
            logger.warn("✗ Soft validation failed: {}", message);
            AllureReportHelper.addValidationStep("Soft Validation", "Pass", "Fail", false);
            return false;
        }
    }

    protected void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted: {}", e.getMessage());
        }
    }

    protected ConfigManager getConfigManager() {
        return configManager;
    }
}
