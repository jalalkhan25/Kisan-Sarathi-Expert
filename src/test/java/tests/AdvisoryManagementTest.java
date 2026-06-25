package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AdvisoryManagementPage;
import utils.ConfigReader;

/**
 * AdvisoryManagementTest - E2E test for creating and sending advisory.
 */
public class AdvisoryManagementTest extends BaseTest {

    private AdvisoryManagementPage advisoryPage;
    private static final String RECIPIENT = ConfigReader.getProperty("advisoryRecipient");

    @Test(priority = 1)
    public void verifyAdvisoryPageOpens() throws InterruptedException {
        advisoryPage = new AdvisoryManagementPage(driver);
        driver.get(BASE_URL + "/advisory-management");
        Thread.sleep(2000);
        assertVisible(By.xpath("//*[contains(text(),'Advisory')]"), "Advisory Management heading");
        System.out.println("[PASS] Advisory Management page opened");
    }

    @Test(priority = 2)
    public void createAndSendAdvisory() throws InterruptedException {
        advisoryPage = new AdvisoryManagementPage(driver);
        advisoryPage.navigateToCreateAdvisory(BASE_URL);

        // Enter recipient mobile
        advisoryPage.enterRecipientMobile(RECIPIENT);

        // Select dropdowns
        advisoryPage.selectDomain("Agriculture");
        Thread.sleep(1000);
        advisoryPage.selectSubDomain("Cereal");
        Thread.sleep(1000);
        advisoryPage.selectCommodity("Adley");
        Thread.sleep(1000);
        advisoryPage.selectProblemType("Field");
        Thread.sleep(1000);

        // Click Next to compose
        advisoryPage.clickNext();

        // Enter advisory content
        advisoryPage.enterAdvisoryDescription("This is automation testing advisory - please ignore");
        advisoryPage.enterAdvisoryTitle("Automation Test Advisory");

        // Submit
        advisoryPage.clickSubmit();

        System.out.println("[PASS] Advisory composed and submitted to: " + RECIPIENT);
    }

    @Test(priority = 3)
    public void verifyAdvisoryInOutbox() throws InterruptedException {
        advisoryPage = new AdvisoryManagementPage(driver);
        advisoryPage.navigateToOutbox(BASE_URL);

        Assert.assertTrue(advisoryPage.isAdvisoryInOutbox(), "No advisory found in outbox");
        System.out.println("[PASS] Advisory verified in outbox");
    }
}
