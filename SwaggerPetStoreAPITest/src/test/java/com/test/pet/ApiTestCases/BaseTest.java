package com.test.pet.ApiTestCases;


import com.test.pet.reports.ExtentReport;
import com.test.pet.reports.LogStatus;
import com.test.pet.utils.Constants;
import io.restassured.RestAssured;
import org.apache.commons.io.output.WriterOutputStream;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import utils.Properties;
//import utils.Properties;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;

public class BaseTest {

    protected StringWriter writer;
    protected PrintStream captor;

    @BeforeSuite
    public void setUpSuite() {
        ExtentReport.initialize();
    }


    @AfterSuite
    public void afterSuite() throws IOException {
        ExtentReport.report.flush();
        File htmlFile = new File(Constants.EXTENTREPORTPATH);
        Desktop.getDesktop().browse(htmlFile.toURI());

    }
    @BeforeMethod
    public void setUp() {
        RestAssured.baseURI = Properties.baseUri;
        writer = new StringWriter();
        captor = new PrintStream(new WriterOutputStream(writer), true);
    }


    protected void formatAPIAndLogInReport(String content) {

        String prettyPrint = content.replace("\n", "<br>");
        LogStatus.info("<pre>" + prettyPrint + "</pre>");

    }

    public void
    writeRequestAndResponseInReport(String request, String response) {

        LogStatus.info("---- Request ---");
        formatAPIAndLogInReport(request);
        LogStatus.info("---- Response ---");
        formatAPIAndLogInReport(response);
    }

    public static void writeAssertionDetailsToReport(String assertionDescription, boolean isAssertionPassed) {

        if (isAssertionPassed) {
            LogStatus.pass("Assertion passed: " + assertionDescription);
        } else {
           LogStatus.fail("Assertion failed: " + assertionDescription);
        }
    }

}
