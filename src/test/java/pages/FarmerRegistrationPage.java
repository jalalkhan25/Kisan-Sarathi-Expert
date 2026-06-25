package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.WaitUtils;

import java.time.Duration;

/**
 * FarmerRegistrationPage - Page Object for Farmer Registration module.
 */
public class FarmerRegistrationPage {

    private WebDriver driver;

    // Locators
    private By farmerMgmtMenu = By.xpath("//span[contains(text(),'Farmer')] | //a[contains(@href,'farmer')]");
    private By registrationLink = By.xpath("//a[contains(@href,'registration')] | //span[contains(text(),'Registration')]");
    private By nameInput = By.cssSelector("input[placeholder='Enter Name'], input[name='name'], input[placeholder='Enter Full Name']");
    private By mobileInput = By.cssSelector("input[placeholder='Enter Mobile Number'], input[name='mobile'], input[placeholder='Enter mobile number']");
    private By genderDropdown = By.cssSelector("select[name='gender']");
    private By ageInput = By.cssSelector("input[placeholder='Enter Age'], input[name='age']");
    private By stateDropdown = By.cssSelector("select[name='state']");
    private By districtDropdown = By.cssSelector("select[name='district']");
    private By blockDropdown = By.cssSelector("select[name='block']");
    private By villageInput = By.cssSelector("input[name='village'], input[placeholder*='Village']");
    private By landSizeInput = By.cssSelector("input[placeholder*='Land'], input[name='landSize']");
    private By submitButton = By.xpath("//button[contains(text(),'Submit') or contains(text(),'Register')]");
    private By searchInput = By.cssSelector("input[placeholder*='Search'], input[placeholder*='search']");

    public FarmerRegistrationPage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToFarmerRegistration() throws InterruptedException {
        // Click Farmer Management in sidebar
        if (WaitUtils.isVisible(driver, farmerMgmtMenu)) {
            driver.findElement(farmerMgmtMenu).click();
            Thread.sleep(1000);
        }
        // Click Registration sub-link
        if (WaitUtils.isVisible(driver, registrationLink)) {
            driver.findElement(registrationLink).click();
            Thread.sleep(2000);
        }
    }

    public void navigateDirectly(String baseUrl) throws InterruptedException {
        driver.get(baseUrl + "/farmer-management/registration");
        Thread.sleep(3000);
    }

    public void fillName(String name) {
        if (WaitUtils.isVisible(driver, nameInput)) {
            WebElement el = driver.findElement(nameInput);
            el.clear();
            el.sendKeys(name);
        }
    }

    public void fillMobile(String mobile) {
        if (WaitUtils.isVisible(driver, mobileInput)) {
            WebElement el = driver.findElement(mobileInput);
            el.clear();
            el.sendKeys(mobile);
        }
    }

    public void selectGender(String gender) throws InterruptedException {
        if (WaitUtils.isVisible(driver, genderDropdown)) {
            Select sel = new Select(driver.findElement(genderDropdown));
            Thread.sleep(500);
            for (WebElement opt : sel.getOptions()) {
                if (opt.getText().contains(gender)) {
                    sel.selectByVisibleText(opt.getText());
                    return;
                }
            }
            if (sel.getOptions().size() > 1) sel.selectByIndex(1);
        }
    }

    public void fillAge(String age) {
        if (WaitUtils.isVisible(driver, ageInput)) {
            WebElement el = driver.findElement(ageInput);
            el.clear();
            el.sendKeys(age);
        }
    }

    public void selectState(String state) throws InterruptedException {
        if (WaitUtils.isVisible(driver, stateDropdown)) {
            Select sel = new Select(driver.findElement(stateDropdown));
            Thread.sleep(500);
            for (WebElement opt : sel.getOptions()) {
                if (opt.getText().contains(state)) {
                    sel.selectByVisibleText(opt.getText());
                    return;
                }
            }
        }
    }

    public void selectDistrict(String district) throws InterruptedException {
        Thread.sleep(2000);
        if (WaitUtils.isVisible(driver, districtDropdown)) {
            Select sel = new Select(driver.findElement(districtDropdown));
            Thread.sleep(500);
            for (WebElement opt : sel.getOptions()) {
                if (opt.getText().contains(district)) {
                    sel.selectByVisibleText(opt.getText());
                    return;
                }
            }
            if (sel.getOptions().size() > 1) sel.selectByIndex(1);
        }
    }

    public void selectBlock() throws InterruptedException {
        Thread.sleep(2000);
        if (WaitUtils.isVisible(driver, blockDropdown)) {
            Select sel = new Select(driver.findElement(blockDropdown));
            if (sel.getOptions().size() > 1) sel.selectByIndex(1);
        }
    }

    public void fillVillage(String village) {
        if (WaitUtils.isVisible(driver, villageInput)) {
            WebElement el = driver.findElement(villageInput);
            el.clear();
            el.sendKeys(village);
        }
    }

    public void fillLandSize(String size) {
        if (WaitUtils.isVisible(driver, landSizeInput)) {
            WebElement el = driver.findElement(landSizeInput);
            el.clear();
            el.sendKeys(size);
        }
    }

    public void clickSubmit() throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(500);
        if (WaitUtils.isVisible(driver, submitButton)) {
            driver.findElement(submitButton).click();
            Thread.sleep(4000);
        }
    }

    public void searchFarmer(String searchText) throws InterruptedException {
        driver.get(driver.getCurrentUrl().split("/registration")[0]);
        Thread.sleep(2000);
        if (WaitUtils.isVisible(driver, searchInput)) {
            WebElement el = driver.findElement(searchInput);
            el.clear();
            el.sendKeys(searchText);
            el.sendKeys(Keys.ENTER);
            Thread.sleep(3000);
        }
    }

    public boolean isResultVisible() {
        return WaitUtils.isVisible(driver, By.cssSelector("table tbody tr"));
    }

    /**
     * Generates unique phone number by incrementing 8th digit.
     * Base: 6000000999 → 6000001999 → 6000002999 etc.
     */
    public static String generateFarmerPhone() {
        long ts = System.currentTimeMillis();
        int increment = (int) (ts % 100);
        // Format: 60000XX999 where XX = unique per run
        String base = "60000";
        String suffix = String.format("%02d", increment) + "999";
        return base + suffix;
    }
}
