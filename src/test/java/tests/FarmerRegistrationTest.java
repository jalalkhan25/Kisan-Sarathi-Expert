package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * FarmerRegistrationTest - Complete Farmer Management E2E flow.
 * 1. Navigate to Farmer Management → Farmer List
 * 2. Click "+ Add Farmer" → Registration page opens
 * 3. Fill mandatory fields, submit, verify success popup
 * 4. Search registered farmer by mobile
 * 5. Apply Active status filter, verify count
 * 6. Search farmer 8081826002, click message, send text
 */
public class FarmerRegistrationTest extends BaseTest {

    private static final String FARMER_NAME = "Automation Farmer";
    private static final String FARMER_PHONE = generateIncrementalPhone();
    private static final String MESSAGE_TARGET_PHONE = "8081826002";

    @Test(priority = 1)
    public void verifyFarmerManagementPageOpens() throws InterruptedException {
        // Click Farmer Management in sidebar
        driver.get(BASE_URL + "/farmer-management");
        Thread.sleep(3000);

        // Verify "Farmer Management" heading and "Farmers List"
        assertVisible(By.xpath("//*[contains(text(),'Farmer Management') or contains(text(),'Farmers List')]"),
                "Farmer Management page");
        System.out.println("[PASS] Farmer Management page opened with Farmers List");
    }

    @Test(priority = 2, dependsOnMethods = {"verifyFarmerManagementPageOpens"})
    public void verifyAddFarmerButtonOpensRegistration() throws InterruptedException {
        driver.get(BASE_URL + "/farmer-management");
        Thread.sleep(2000);

        // Click "+ Add Farmer" button (top-right)
        By addFarmerBtn = By.xpath("//button[contains(text(),'Add Farmer')] | //a[contains(text(),'Add Farmer')] | //button[contains(text(),'+ Add')]");
        WebElement btn = assertVisible(addFarmerBtn, "+ Add Farmer button");
        btn.click();
        Thread.sleep(3000);

        // Verify redirected to registration page
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/registration"), "Should redirect to registration. URL: " + url);
        assertVisible(By.xpath("//*[contains(text(),'Farmer Registration')]"), "Farmer Registration heading");
        System.out.println("[PASS] + Add Farmer redirected to registration page");
    }

    @Test(priority = 3, dependsOnMethods = {"verifyAddFarmerButtonOpensRegistration"})
    public void registerNewFarmer() throws InterruptedException {
        driver.get(BASE_URL + "/farmer-management/registration");
        Thread.sleep(3000);

        // Fill Full Name (mandatory)
        By nameInput = By.xpath("//input[@placeholder='Enter your name']");
        assertVisible(nameInput, "Full Name input");
        driver.findElement(nameInput).clear();
        driver.findElement(nameInput).sendKeys(FARMER_NAME);
        Thread.sleep(500);

        // Fill Mobile Number (mandatory)
        By mobileInput = By.xpath("//input[@placeholder='Enter your phone number']");
        assertVisible(mobileInput, "Mobile Number input");
        driver.findElement(mobileInput).clear();
        driver.findElement(mobileInput).sendKeys(FARMER_PHONE);
        Thread.sleep(500);

        // Fill Age
        By ageInput = By.xpath("//input[@placeholder='Enter your age']");
        if (isVisible(ageInput)) {
            driver.findElement(ageInput).clear();
            driver.findElement(ageInput).sendKeys("35");
        }
        Thread.sleep(500);

        // Select Gender (mandatory)
        By genderDropdown = By.xpath("//select[contains(@class,'Gender') or @name='gender'] | //select[.//option[contains(text(),'Select Gender')]]");
        if (isVisible(genderDropdown)) {
            Select gender = new Select(driver.findElement(genderDropdown));
            gender.selectByVisibleText("Male");
        }
        Thread.sleep(500);

        // Select Preferred Language
        By langDropdown = By.xpath("//select[.//option[contains(text(),'Select Language')]]");
        if (isVisible(langDropdown)) {
            Select lang = new Select(driver.findElement(langDropdown));
            if (lang.getOptions().size() > 1) lang.selectByIndex(1);
        }
        Thread.sleep(500);

        // State, District, KVK are pre-populated for Expert (Uttar Pradesh, Azamgarh, Dic Test Kvk)
        // Select Block (mandatory)
        By blockDropdown = By.xpath("//select[.//option[contains(text(),'Select Block')]]");
        if (isVisible(blockDropdown)) {
            Thread.sleep(1000);
            Select block = new Select(driver.findElement(blockDropdown));
            if (block.getOptions().size() > 1) block.selectByIndex(1);
        }
        Thread.sleep(1000);

        // Select Village
        By villageDropdown = By.xpath("//select[.//option[contains(text(),'Select Village')]]");
        if (isVisible(villageDropdown)) {
            Thread.sleep(1000);
            Select village = new Select(driver.findElement(villageDropdown));
            if (village.getOptions().size() > 1) village.selectByIndex(1);
        }
        Thread.sleep(500);

        // Total Land Size
        By landInput = By.xpath("//input[@placeholder='Enter your total land size' or contains(@placeholder,'land')]");
        if (isVisible(landInput)) {
            driver.findElement(landInput).clear();
            driver.findElement(landInput).sendKeys("5");
        }

        // Scroll down and click Submit
        scrollDown();
        Thread.sleep(1000);
        By submitBtn = By.xpath("//button[text()='Submit']");
        WebElement submit = assertVisible(submitBtn, "Submit button");
        submit.click();
        Thread.sleep(4000);

        // Verify success popup or redirect
        boolean success = isVisible(By.xpath("//*[contains(text(),'success') or contains(text(),'Success') or contains(text(),'registered')]"))
                || !driver.getCurrentUrl().contains("/registration");
        Assert.assertTrue(success, "Farmer registration failed. URL: " + driver.getCurrentUrl());
        System.out.println("[PASS] Farmer registered: " + FARMER_NAME + " | Phone: " + FARMER_PHONE);
    }

    @Test(priority = 4, dependsOnMethods = {"registerNewFarmer"})
    public void searchRegisteredFarmerByMobile() throws InterruptedException {
        driver.get(BASE_URL + "/farmer-management");
        Thread.sleep(2000);

        // Search by mobile number
        By searchInput = By.xpath("//input[@placeholder='Search by Name or Mobile Number']");
        WebElement search = assertVisible(searchInput, "Search input");
        search.clear();
        search.sendKeys(FARMER_PHONE);
        search.sendKeys(Keys.ENTER);
        Thread.sleep(3000);

        // Verify farmer appears in list
        By tableRow = By.cssSelector("table tbody tr");
        Assert.assertTrue(isVisible(tableRow), "Registered farmer not found for: " + FARMER_PHONE);
        String rowText = driver.findElement(tableRow).getText();
        System.out.println("[PASS] Farmer found in search: " + rowText.substring(0, Math.min(60, rowText.length())));
    }

    @Test(priority = 5)
    public void filterByActiveStatusAndVerifyCount() throws InterruptedException {
        driver.get(BASE_URL + "/farmer-management");
        Thread.sleep(2000);

        // Click "Filters" button
        By filtersBtn = By.xpath("//button[contains(text(),'Filters') or contains(text(),'Filter')]");
        WebElement filterBtn = assertVisible(filtersBtn, "Filters button");
        filterBtn.click();
        Thread.sleep(1500);

        // Click "Status" filter option in sidebar
        By statusOption = By.xpath("//*[contains(text(),'Status')]");
        if (isVisible(statusOption)) {
            driver.findElement(statusOption).click();
            Thread.sleep(1000);
        }

        // Select "Active" radio button
        By activeRadio = By.xpath("//input[@type='radio' and @value='Active'] | //label[contains(text(),'Active')]/input | //label[contains(text(),'Active')]");
        if (isVisible(activeRadio)) {
            driver.findElement(activeRadio).click();
            Thread.sleep(500);
        }

        // Click "Apply" button
        By applyBtn = By.xpath("//button[text()='Apply']");
        if (isVisible(applyBtn)) {
            driver.findElement(applyBtn).click();
            Thread.sleep(3000);
        }

        // Count filtered farmers
        By rows = By.cssSelector("table tbody tr");
        int activeCount = 0;
        if (isVisible(rows)) {
            activeCount = driver.findElements(rows).size();
        }
        System.out.println("[RESULT] Active Farmers count: " + activeCount);
        Assert.assertTrue(activeCount > 0, "No active farmers found after filter");
        System.out.println("[PASS] Active status filter applied, found " + activeCount + " farmers");
    }

    @Test(priority = 6)
    public void searchFarmerAndSendMessage() throws InterruptedException {
        driver.get(BASE_URL + "/farmer-management");
        Thread.sleep(2000);

        // Search for farmer 8081826002
        By searchInput = By.xpath("//input[@placeholder='Search by Name or Mobile Number']");
        WebElement search = assertVisible(searchInput, "Search input");
        search.clear();
        search.sendKeys(MESSAGE_TARGET_PHONE);
        search.sendKeys(Keys.ENTER);
        Thread.sleep(3000);

        // Verify farmer appears
        By tableRow = By.cssSelector("table tbody tr");
        Assert.assertTrue(isVisible(tableRow), "Farmer " + MESSAGE_TARGET_PHONE + " not found");

        // Click message icon/button in Contact column
        By messageBtn = By.xpath("//table//tr[1]//button[contains(@class,'message') or @title='Message'] | //table//tr[1]//img[contains(@alt,'message')] | //table//tr[1]//*[contains(@class,'chat') or contains(@class,'sms')]");
        if (!isVisible(messageBtn)) {
            // Try clicking the message icon in the Action/Contact column
            messageBtn = By.xpath("//table//tr[1]//td[last()]//button[1] | //table//tr[1]//td[last()-1]//button");
        }
        WebElement msgIcon = assertVisible(messageBtn, "Message button");
        msgIcon.click();
        Thread.sleep(2000);

        // Verify "Send Message" popup opens
        assertVisible(By.xpath("//*[contains(text(),'Send Message')]"), "Send Message popup");

        // Enter message text
        By messageArea = By.xpath("//textarea[@placeholder='Type your message upto 150 characters' or contains(@placeholder,'message')]");
        WebElement msgInput = assertVisible(messageArea, "Message textarea");
        msgInput.clear();
        msgInput.sendKeys("Testing purpose");
        Thread.sleep(500);

        // Click Submit
        By submitBtn = By.xpath("//button[text()='Submit']");
        WebElement submit = assertVisible(submitBtn, "Submit button in popup");
        submit.click();
        Thread.sleep(3000);

        System.out.println("[PASS] Message 'Testing purpose' sent to farmer " + MESSAGE_TARGET_PHONE);
    }

    /**
     * Incremental phone: 6000000199 → 6000000299 → 6000000399...
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
        // Format: 600000XX99 — 8th digit increments
        String base = "600000";
        String mid = String.format("%02d", counter % 100);
        String suffix = "99";
        return base + mid + suffix;
    }
}
