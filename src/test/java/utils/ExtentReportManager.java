package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * ExtentReportManager - Manages HTML test report generation.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final String REPORT_PATH = "reports/TestReport.html";

    public static ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("KS Expert Automation Report");
            spark.config().setReportName("Kisan Sarathi 2.0 - KVK Expert Test Results");
            extent = new ExtentReports();
            extent.setSystemInfo("Project", "Kisan Sarathi 2.0");
            extent.setSystemInfo("Module", "KVK Expert");
            extent.setSystemInfo("QA Engineer", "Jalal Khan");
            extent.setSystemInfo("Environment", "Dev");
            extent.attachReporter(spark);
        }
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        return getInstance().createTest(testName);
    }

    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}
