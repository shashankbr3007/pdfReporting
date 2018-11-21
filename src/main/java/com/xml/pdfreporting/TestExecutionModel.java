package com.xml.pdfreporting;

import java.util.List;

public class TestExecutionModel {

    private final String Teststart = "Teststart";
    private final String Testcompletion = "Testcompletion";
    private String testName;
    private String testDescription;
    private String testExpected;
    private List<String> testActual;

    public String getTeststart() {
        return Teststart;
    }

    public String getTestcompletion() {
        return Testcompletion;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public String getTestExpected() {
        return testExpected;
    }

    public void setTestExpected(String testExpected) {
        this.testExpected = testExpected;
    }

    public List<String> getTestActual() {
        return testActual;
    }

    public void setTestActual(List<String> testActual) {
        this.testActual = testActual;
    }

}
