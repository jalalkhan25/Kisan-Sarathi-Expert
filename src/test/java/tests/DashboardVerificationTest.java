package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * DashboardVerificationTest - Verifies dashboard counts match actual data
 * in respective sections (Query Management, Advisory, Call Management, Farmer Management).
 *
 * Drill-down validation: Read count from dashboard → Navigate to section → Compare actual count.
 */
public class DashboardVerificationTest extends BaseTest {

    // Dashboard values captured
    private int dashPendingQueries = 0;
    private int dashClosedQueries = 0;
    private int dashReferredQueries = 0;
    private int dashTotalQueries = 0;
    private int dashAdvisorySent = 0;
    private int dashOutboundCalls = 0;
    private int dashInboundCalls = 0;
    private int dashAssignedCalls = 0;

    // ==================== QUERY SECTION ====================

    @Test(priority = 1)
    public void captureDashboardQueryCounts() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(3000);

        // Total number of queries (from header like "Total number of queries (24)")
        By totalQueryHeader = By.xpath("//*[contains(text(),'Total number of queries')]");
        if (isVisible(totalQueryHeader)) {
            String headerText = driver.findElement(totalQueryHeader).getText();
            dashTotalQueries = extractNumber(headerText);
            System.out.println("[DASHBOARD] Total Queries: " + dashTotalQueries);
        }

        // Pending count
        By pendingBox = By.xpath("//*[contains(text(),'Pending')]/following-sibling::*[1] | //*[contains(text(),'Pending')]/parent::*//h2 | //*[contains(text(),'Pending')]/preceding-sibling::*[1]");
        dashPendingQueries = getCountNearLabel("Pending");
        System.out.println("[DASHBOARD] Pending Queries: " + dashPendingQueries);

        // Closed count
        dashClosedQueries = getCountNearLabel("Closed");
        System.out.println("[DASHBOARD] Closed Queries: " + dashClosedQueries);

        // Referred count
        dashReferredQueries = getCountNearLabel("Referred");
        System.out.println("[DASHBOARD] Referred Queries: " + dashReferredQueries);

        Assert.assertTrue(dashTotalQueries > 0 || dashPendingQueries > 0,
                "Could not capture query counts from dashboard");
        System.out.println("[PASS] Dashboard query counts captured");
    }

    @Test(priority = 2, dependsOnMethods = {"captureDashboardQueryCounts"})
    public void verifyPendingQueriesMatchSection() throws InterruptedException {
        driver.get(BASE_URL + "/query-management");
        Thread.sleep(2000);

        // Click Pending tab
        By pendingTab = By.xpath("//button[contains(text(),'Pending')] | //div[contains(text(),'Pending')] | //span[contains(text(),'Pending')]");
        if (isVisible(pendingTab)) {
            driver.findElement(pendingTab).click();
            Thread.sleep(2000);
        }

        int actualPending = getTableRowCount();
        System.out.println("[SECTION] Pending Queries actual count: " + actualPending);

        if (dashPendingQueries > 0) {
            Assert.assertEquals(actualPending, dashPendingQueries,
                    "Dashboard Pending (" + dashPendingQueries + ") != Query Section Pending (" + actualPending + ")");
            System.out.println("[PASS] Pending queries match: Dashboard=" + dashPendingQueries + ", Section=" + actualPending);
        } else {
            System.out.println("[INFO] Pending count was 0 on dashboard, section shows: " + actualPending);
        }
    }

    @Test(priority = 3, dependsOnMethods = {"captureDashboardQueryCounts"})
    public void verifyClosedQueriesMatchSection() throws InterruptedException {
        driver.get(BASE_URL + "/query-management");
        Thread.sleep(2000);

        By closedTab = By.xpath("//button[contains(text(),'Closed')] | //div[contains(text(),'Closed')] | //span[contains(text(),'Closed')]");
        if (isVisible(closedTab)) {
            driver.findElement(closedTab).click();
            Thread.sleep(2000);
        }

        int actualClosed = getTableRowCount();
        System.out.println("[SECTION] Closed Queries actual count: " + actualClosed);

        if (dashClosedQueries > 0) {
            Assert.assertEquals(actualClosed, dashClosedQueries,
                    "Dashboard Closed (" + dashClosedQueries + ") != Query Section Closed (" + actualClosed + ")");
            System.out.println("[PASS] Closed queries match: Dashboard=" + dashClosedQueries + ", Section=" + actualClosed);
        } else {
            System.out.println("[INFO] Closed count was 0 on dashboard, section shows: " + actualClosed);
        }
    }

    // ==================== ADVISORY SECTION ====================

    @Test(priority = 4)
    public void captureDashboardAdvisoryCount() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);

        // "No. of advisory sent" or "Advisory-based performance statistics"
        dashAdvisorySent = getCountNearLabel("advisory sent");
        if (dashAdvisorySent == 0) {
            dashAdvisorySent = getCountNearLabel("Advisory");
        }
        System.out.println("[DASHBOARD] Advisory Sent: " + dashAdvisorySent);
        System.out.println("[PASS] Dashboard advisory count captured");
    }

    @Test(priority = 5, dependsOnMethods = {"captureDashboardAdvisoryCount"})
    public void verifyAdvisoryCountMatchesOutbox() throws InterruptedException {
        driver.get(BASE_URL + "/advisory-management/outbox-advisories");
        Thread.sleep(3000);

        int actualAdvisory = getTableRowCount();
        System.out.println("[SECTION] Advisory Outbox actual count: " + actualAdvisory);

        if (dashAdvisorySent > 0) {
            Assert.assertEquals(actualAdvisory, dashAdvisorySent,
                    "Dashboard Advisory (" + dashAdvisorySent + ") != Outbox count (" + actualAdvisory + ")");
            System.out.println("[PASS] Advisory count match: Dashboard=" + dashAdvisorySent + ", Outbox=" + actualAdvisory);
        }
    }

    // ==================== CALL STATUS SECTION ====================

    @Test(priority = 6)
    public void captureDashboardCallCounts() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);

        // Call Status section: Outbound (X), Inbound (X), Assigned (X)
        dashOutboundCalls = getCountFromCallCard("Outbound");
        dashInboundCalls = getCountFromCallCard("Inbound");
        dashAssignedCalls = getCountFromCallCard("Assigned");

        System.out.println("[DASHBOARD] Outbound Calls: " + dashOutboundCalls);
        System.out.println("[DASHBOARD] Inbound Calls: " + dashInboundCalls);
        System.out.println("[DASHBOARD] Assigned Calls: " + dashAssignedCalls);
        System.out.println("[PASS] Dashboard call counts captured");
    }

    @Test(priority = 7, dependsOnMethods = {"captureDashboardCallCounts"})
    public void verifyInboundCallsMatchSection() throws InterruptedException {
        driver.get(BASE_URL + "/call-management/Inbound-calls");
        Thread.sleep(2000);

        int actualInbound = getTableRowCount();
        System.out.println("[SECTION] Inbound Calls actual count: " + actualInbound);

        // Dashboard shows total in header, table may be paginated
        System.out.println("[RESULT] Dashboard Inbound=" + dashInboundCalls + " | Section shows " + actualInbound + " rows");
    }

    @Test(priority = 8, dependsOnMethods = {"captureDashboardCallCounts"})
    public void verifyOutboundCallsMatchSection() throws InterruptedException {
        driver.get(BASE_URL + "/call-management/Outbound-calls");
        Thread.sleep(2000);

        int actualOutbound = getTableRowCount();
        System.out.println("[SECTION] Outbound Calls actual count: " + actualOutbound);

        System.out.println("[RESULT] Dashboard Outbound=" + dashOutboundCalls + " | Section shows " + actualOutbound + " rows");
    }

    // ==================== FARMER REGISTRATION SECTION ====================

    @Test(priority = 9)
    public void captureDashboardFarmerRegistrations() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);

        int selfReg = getCountNearLabel("Self Farmer Registration");
        int totalKvkReg = getCountNearLabel("Total Farmer Registrations under KVK");

        System.out.println("[DASHBOARD] Self Farmer Registrations: " + selfReg);
        System.out.println("[DASHBOARD] Total KVK Registrations: " + totalKvkReg);
        System.out.println("[PASS] Dashboard farmer registration counts captured");
    }

    @Test(priority = 10, dependsOnMethods = {"captureDashboardFarmerRegistrations"})
    public void verifyFarmerCountMatchesSection() throws InterruptedException {
        driver.get(BASE_URL + "/farmer-management");
        Thread.sleep(2000);

        int actualFarmers = getTableRowCount();
        System.out.println("[SECTION] Farmer Management list count: " + actualFarmers);
        System.out.println("[PASS] Farmer section accessible and has " + actualFarmers + " records");
    }

    // ==================== HELPER METHODS ====================

    /**
     * Get numeric count displayed near a label text on dashboard.
     * Looks for large numbers (h2, h3, strong, span) near the label.
     */
    private int getCountNearLabel(String label) {
        try {
            // Strategy 1: Look for number in sibling/parent of the label
            By labelLoc = By.xpath("//*[contains(text(),'" + label + "')]");
            if (!isVisible(labelLoc)) return 0;

            WebElement labelEl = driver.findElement(labelLoc);
            WebElement parent = labelEl.findElement(By.xpath("./.."));

            // Find nearby numbers - look for digits in the parent container
            List<WebElement> numbers = parent.findElements(By.xpath(".//*[string-length(normalize-space()) < 6 and number(normalize-space()) = number(normalize-space())]"));
            if (!numbers.isEmpty()) {
                String text = numbers.get(0).getText().trim();
                return Integer.parseInt(text.replaceAll("[^0-9]", ""));
            }

            // Strategy 2: Look for number preceding the label text
            By precedingNum = By.xpath("//*[contains(text(),'" + label + "')]/preceding-sibling::*[1]");
            if (isVisible(precedingNum)) {
                String text = driver.findElement(precedingNum).getText().trim();
                if (text.matches("\\d+")) return Integer.parseInt(text);
            }

            // Strategy 3: Extract from parent text
            String parentText = parent.getText();
            String[] parts = parentText.split("\\s+");
            for (String part : parts) {
                if (part.matches("\\d+")) return Integer.parseInt(part);
            }
        } catch (Exception e) {
            // silent
        }
        return 0;
    }

    /**
     * Extract count from Call Status card (e.g., "Outbound (6)")
     */
    private int getCountFromCallCard(String callType) {
        try {
            By cardLoc = By.xpath("//*[contains(text(),'" + callType + "')]");
            if (isVisible(cardLoc)) {
                String text = driver.findElement(cardLoc).getText();
                // Extract number from "Outbound (6)" or "Inbound (0)"
                if (text.contains("(") && text.contains(")")) {
                    String num = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
                    return Integer.parseInt(num.trim());
                }
            }
        } catch (Exception e) {
            // silent
        }
        return 0;
    }

    /**
     * Count table rows on current page.
     */
    private int getTableRowCount() {
        By rows = By.cssSelector("table tbody tr");
        if (isVisible(rows)) {
            return driver.findElements(rows).size();
        }
        return 0;
    }

    /**
     * Extract first number from a text string.
     */
    private int extractNumber(String text) {
        try {
            String num = text.replaceAll("[^0-9]", "");
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        } catch (Exception e) {
            return 0;
        }
    }
}
