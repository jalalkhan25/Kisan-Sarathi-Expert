package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.FarmerRegistrationPage;

/**
 * FarmerRegistrationTest - E2E test for registering a new farmer.
 * Phone number auto-increments on each run to ensure uniqueness.
 */
public class FarmerRegistrationTest extends BaseTest {

    private FarmerRegistrationPage farmerPage;
    private static final String FARMER_NAME = "Automation Farmer";
    private static final String FARMER_PHONE = generateIncrementalPhone();

    /**
     * Generates phone with 8th digit incrementing per run.
     * Base: 6000000999 → 6000001999 → 6000002999 ...
     * Counter stored in phone_counter.txt
     */
    private static String generateIncrementalPhone() {
        int counter = 0;
        java.io.File counterFile = new java.io.File("phone_counter.txt");
        try {
            if (counterFile.exists()) {
                String val = new String(java.nio.file.Files.readAllBytes(counterFile.toPath())).trim();
                counter = Integer.parseInt(val);
            }
            counter++;
            java.nio.file.Files.write(counterFile.toPath(), String.valueOf(counter).getBytes());
        } catch (Exception e) {
            counter = (int) (System.currentTimeMillis() % 100);
        }
        // Format: 600000X999 where X = 8th digit increments
        // 6000000999 → 6000001999 → 6000002999 → ... → 6000009999 → 6000010999
        String base = "600000";
        String mid = String.format("%02d", counter % 100);
        String suffix = "99";
        return base + mid + suffix;
    }

    @Test(priority = 1)
    public void verifyFarmerRegistrationPageOpens() throws InterruptedException {
        farmerPage = new FarmerRegistrationPage(driver);
        farmerPage.navigateDirectly(BASE_URL);
        assertVisible(By.xpath("//*[contains(text(),'Farmer') and (contains(text(),'Registration') or contains(text(),'Register'))]"),
                "Farmer Registration heading");
        System.out.println("[PASS] Farmer Registration page opened");
    }

    @Test(priority = 2, dependsOnMethods = {"verifyFarmerRegistrationPageOpens"})
    public void registerNewFarmer() throws InterruptedException {
        farmerPage = new FarmerRegistrationPage(driver);
        farmerPage.navigateDirectly(BASE_URL);
        Thread.sleep(2000);

        farmerPage.fillName(FARMER_NAME);
        farmerPage.fillMobile(FARMER_PHONE);
        farmerPage.selectGender("Male");
        farmerPage.fillAge("35");

        // Location fields (pre-populated for KVK Expert, select block if needed)
        farmerPage.selectBlock();
        farmerPage.fillLandSize("5");

        farmerPage.clickSubmit();

        // Verify success - either redirect or success toast
        Thread.sleep(2000);
        String url = driver.getCurrentUrl();
        boolean success = !url.contains("/registration") ||
                isVisible(By.xpath("//*[contains(text(),'success') or contains(text(),'Success') or contains(text(),'registered')]"));
        Assert.assertTrue(success, "Farmer registration did not succeed. URL: " + url);
        System.out.println("[PASS] Farmer registered: " + FARMER_NAME + " | Phone: " + FARMER_PHONE);
    }

    @Test(priority = 3, dependsOnMethods = {"registerNewFarmer"})
    public void verifyRegisteredFarmerInList() throws InterruptedException {
        farmerPage = new FarmerRegistrationPage(driver);
        driver.get(BASE_URL + "/farmer-management");
        Thread.sleep(2000);

        farmerPage.searchFarmer(FARMER_PHONE);
        Assert.assertTrue(farmerPage.isResultVisible(),
                "Registered farmer not found in list for phone: " + FARMER_PHONE);
        System.out.println("[PASS] Farmer found in list: " + FARMER_PHONE);
    }
}
