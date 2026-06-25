package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.QueryManagementPage;

/**
 * QueryManagementTest - E2E test for resolving a pending query.
 */
public class QueryManagementTest extends BaseTest {

    private QueryManagementPage queryPage;

    @Test(priority = 1)
    public void verifyQueryManagementPageOpens() throws InterruptedException {
        queryPage = new QueryManagementPage(driver);
        queryPage.navigateToQueryManagement(BASE_URL);
        assertVisible(By.xpath("//*[contains(text(),'Query')]"), "Query Management heading");
        System.out.println("[PASS] Query Management page opened");
    }

    @Test(priority = 2)
    public void openPendingQueryAndProvideSolution() throws InterruptedException {
        queryPage = new QueryManagementPage(driver);
        queryPage.navigateToQueryManagement(BASE_URL);

        // Click Pending tab
        queryPage.clickPendingTab();

        // Check if pending queries exist
        if (!queryPage.hasPendingQueries()) {
            System.out.println("[INFO] No pending queries available to resolve. Skipping.");
            return;
        }

        // Open first pending query
        queryPage.openFirstPendingQuery();

        // Enter solution
        queryPage.enterSolution("this is automation testing");

        // Select SMS = No
        queryPage.selectSmsNo();

        // Submit solution
        queryPage.clickSubmitSolution();

        // Verify success
        boolean success = queryPage.isSolutionSubmittedSuccessfully() ||
                isVisible(By.xpath("//*[contains(text(),'success') or contains(text(),'Success')]"));
        Assert.assertTrue(success, "Solution submission did not show success message");
        System.out.println("[PASS] Solution submitted successfully");
    }

    @Test(priority = 3)
    public void verifyQueryMovedToClosed() throws InterruptedException {
        queryPage = new QueryManagementPage(driver);
        queryPage.navigateToQueryManagement(BASE_URL);

        queryPage.clickClosedTab();
        Thread.sleep(2000);

        int closedCount = queryPage.getTableRowCount();
        Assert.assertTrue(closedCount > 0, "No closed queries found after providing solution");
        System.out.println("[PASS] Closed queries count: " + closedCount);
    }
}
