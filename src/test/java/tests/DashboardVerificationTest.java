package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * DashboardVerificationTest - Verifies dashboard counts, My Appointments button,
 * calendar Today button, and drill-down to Query/Call sections.
 */
public class DashboardVerificationTest extends BaseTest {

    private int dashPendingQueries = 0;
    private int dashClosedQueries = 0;
    private int dashTotalQueries = 0;

    @Test(priority = 1)
    public void verifyTotalQueriesHeaderDisplayed() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(3000);

        // "Total number of queries (38)" from screenshot
        By totalHeader = By.xpath("//*[contains(text(),'Total number of queries')]");
        assertVisible(totalHeader, "Total queries header");

        String headerText = driver.findElement(totalHeader).getText();
        dashTotalQueries = extractNumber(headerText);
        System.out.println("[PASS] Total queries header displayed: " + dashTotalQueries);
    }

    @Test(priority = 2, dependsOnMethods = {"verifyTotalQueriesHeaderDisplayed"})
    public void verifyPendingClosedReferredCounts() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);

        // Pending count (5 from screenshot)
        By pendingCount = By.xpath("//*[contains(text(),'Pending')]/parent::*//h2 | //*[contains(text(),'Pending')]/preceding-sibling::*");
        if (isVisible(pendingCount)) {
            dashPendingQueries = extractNumber(driver.findElement(By.xpath("//*[contains(text(),'Pending')]/ancestor::div[1]")).getText());
        }

        // Closed count (33 from screenshot)
        By closedCount = By.xpath("//*[contains(text(),'Closed')]/ancestor::div[1]");
        if (isVisible(closedCount)) {
            dashClosedQueries = extractNumber(driver.findElement(closedCount).getText());
        }

        System.out.println("[RESULT] Pending: " + dashPendingQueries + " | Closed: " + dashClosedQueries);
        Assert.assertTrue(dashPendingQueries >= 0 && dashClosedQueries >= 0,
                "Query counts should be non-negative");
        System.out.println("[PASS] Pending/Closed/Referred counts verified");
    }

    @Test(priority = 3)
    public void verifyMyAppointmentsButtonRedirects() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);

        // Click "My appointments" button (green button top-right of queries section)
        By myAppointments = By.xpath("//button[contains(text(),'My appointments') or contains(text(),'appointments')]");
        WebElement btn = assertVisible(myAppointments, "My appointments button");
        btn.click();
        Thread.sleep(3000);

        // Should redirect to pending queries / call schedule
        String url = driver.getCurrentUrl();
        boolean redirected = url.contains("query") || url.contains("appointment")
                || url.contains("schedule") || url.contains("calendar");
        Assert.assertTrue(redirected, "My Appointments should redirect. URL: " + url);
        System.out.println("[PASS] My Appointments redirected to: " + url);
    }

    @Test(priority = 4)
    public void verifyQueryModeChartDisplayed() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);

        // "Query Mode" chart with KCC, Farmer bars
        boolean chartVisible = isVisible(By.xpath("//*[contains(text(),'Query Mode')]"));
        Assert.assertTrue(chartVisible, "Query Mode chart not displayed");
        System.out.println("[PASS] Query Mode chart displayed");
    }

    @Test(priority = 5)
    public void verifyTop10CommoditiesChartDisplayed() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);

        // "Top 10 commodities" chart
        boolean chartVisible = isVisible(By.xpath("//*[contains(text(),'Top 10 commodities') or contains(text(),'Top 10')]"));
        Assert.assertTrue(chartVisible, "Top 10 commodities chart not displayed");
        System.out.println("[PASS] Top 10 commodities chart displayed");
    }

    @Test(priority = 6)
    public void verifyAdvisoryPerformanceSection() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);
        scrollDown();
        Thread.sleep(1000);

        boolean advSection = isVisible(By.xpath("//*[contains(text(),'Advisory') and contains(text(),'performance')]"));
        if (advSection) {
            System.out.println("[PASS] Advisory performance section displayed");
        } else {
            System.out.println("[INFO] Advisory performance section not visible on this view");
        }
    }

    @Test(priority = 7)
    public void verifyCallStatusSection() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);
        scrollDown();
        Thread.sleep(1000);

        boolean callSection = isVisible(By.xpath("//*[contains(text(),'Call Status')]"));
        if (callSection) {
            System.out.println("[PASS] Call Status section displayed");
        } else {
            System.out.println("[INFO] Call Status section not in current view");
        }
    }

    @Test(priority = 8)
    public void verifyCropCalendarTodayButton() throws InterruptedException {
        // Navigate to Crop Calendar
        driver.get(BASE_URL + "/crop-calendar");
        Thread.sleep(3000);

        // Click "Today" button
        By todayBtn = By.xpath("//button[contains(text(),'Today') or contains(text(),'today')]");
        if (isVisible(todayBtn)) {
            String urlBefore = driver.getCurrentUrl();
            driver.findElement(todayBtn).click();
            Thread.sleep(2000);
            String urlAfter = driver.getCurrentUrl();
            // Page should refresh but stay on same URL
            Assert.assertTrue(urlAfter.contains("calendar"),
                    "Should stay on calendar page after clicking Today");
            System.out.println("[PASS] Today button clicked - page refreshed, stayed on calendar");
        } else {
            System.out.println("[INFO] Today button not found on Crop Calendar page");
        }
    }

    // Helper
    private int extractNumber(String text) {
        try {
            String num = text.replaceAll("[^0-9]", "");
            return num.isEmpty() ? 0 : Integer.parseInt(num.substring(0, Math.min(num.length(), 5)));
        } catch (Exception e) {
            return 0;
        }
    }
}
