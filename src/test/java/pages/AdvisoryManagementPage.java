package pages;

import org.openqa.selenium.*;
import utils.WaitUtils;

/**
 * AdvisoryManagementPage - Page Object for Advisory Management (Create & Send Advisory).
 */
public class AdvisoryManagementPage {

    private WebDriver driver;

    private By advisoryMenu = By.xpath("//span[contains(text(),'Advisory')] | //a[contains(@href,'advisory')]");
    private By createAdvisoryLink = By.xpath("//a[contains(@href,'create-advisory')] | //span[contains(text(),'Create')]");
    private By mobileInput = By.cssSelector("input[placeholder*='mobile number'], input[placeholder*='Mobile']");
    private By advisoryTitleInput = By.cssSelector("textarea[placeholder*='title'], input[placeholder*='title']");
    private By advisoryDescInput = By.cssSelector("textarea[placeholder*='description'], textarea[placeholder*='Compose'], textarea[placeholder*='message']");
    private By nextButton = By.xpath("//button[.//span[contains(text(),'Next')] or contains(text(),'Next')]");
    private By submitButton = By.xpath("//button[.//span[contains(text(),'Submit')] or contains(text(),'Submit') or contains(text(),'Send')]");
    private By outboxLink = By.xpath("//a[contains(@href,'outbox')] | //span[contains(text(),'Outbox')]");

    public AdvisoryManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCreateAdvisory(String baseUrl) throws InterruptedException {
        driver.get(baseUrl + "/advisory-management/create-advisory");
        Thread.sleep(3000);
    }

    public void enterRecipientMobile(String mobile) throws InterruptedException {
        if (WaitUtils.isVisible(driver, mobileInput)) {
            WebElement el = driver.findElement(mobileInput);
            el.clear();
            el.sendKeys(mobile);
            el.sendKeys(Keys.ENTER);
            Thread.sleep(2000);
        }
    }

    public void selectDomain(String domain) throws InterruptedException {
        clickCustomDropdown("Domain", domain);
    }

    public void selectSubDomain(String subDomain) throws InterruptedException {
        clickCustomDropdown("Sub Domain", subDomain);
    }

    public void selectCommodity(String commodity) throws InterruptedException {
        clickCustomDropdown("Commodity", commodity);
    }

    public void selectProblemType(String problemType) throws InterruptedException {
        clickCustomDropdown("Problem Type", problemType);
    }

    public void clickNext() throws InterruptedException {
        scrollDown();
        Thread.sleep(500);
        if (WaitUtils.isVisible(driver, nextButton)) {
            WebElement btn = driver.findElement(nextButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            Thread.sleep(300);
            btn.click();
            Thread.sleep(3000);
        }
    }

    public void enterAdvisoryTitle(String title) {
        if (WaitUtils.isVisible(driver, advisoryTitleInput)) {
            driver.findElement(advisoryTitleInput).sendKeys(title);
        }
    }

    public void enterAdvisoryDescription(String desc) {
        if (WaitUtils.isVisible(driver, advisoryDescInput)) {
            driver.findElement(advisoryDescInput).sendKeys(desc);
        }
    }

    public void clickSubmit() throws InterruptedException {
        scrollDown();
        Thread.sleep(500);
        if (WaitUtils.isVisible(driver, submitButton)) {
            WebElement btn = driver.findElement(submitButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            Thread.sleep(300);
            btn.click();
            Thread.sleep(5000);
        }
    }

    public void navigateToOutbox(String baseUrl) throws InterruptedException {
        driver.get(baseUrl + "/advisory-management/outbox-advisories");
        Thread.sleep(3000);
    }

    public boolean isAdvisoryInOutbox() {
        By firstRow = By.cssSelector("table tbody tr:first-child");
        if (!WaitUtils.isVisible(driver, firstRow)) {
            firstRow = By.xpath("(//div[contains(@class,'cursor-pointer')])[1]");
        }
        return WaitUtils.isVisible(driver, firstRow);
    }

    // Helper: click custom dropdown by label and select option
    private void clickCustomDropdown(String label, String optionText) throws InterruptedException {
        scrollDown();
        Thread.sleep(1000);
        By trigger = By.xpath("//h4[contains(text(),'" + label + "')]/following-sibling::div[1]");
        if (!WaitUtils.isVisible(driver, trigger)) {
            trigger = By.xpath("//h4[contains(text(),'" + label + "')]");
        }
        if (WaitUtils.isVisible(driver, trigger)) {
            WebElement el = driver.findElement(trigger);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            Thread.sleep(300);
            el.click();
            Thread.sleep(1500);

            By option = By.xpath("//li[.//span[contains(text(),'" + optionText + "')]]");
            if (!WaitUtils.isVisible(driver, option)) {
                option = By.xpath("//li[contains(.,'" + optionText + "')]");
            }
            if (WaitUtils.isVisible(driver, option)) {
                driver.findElement(option).click();
                Thread.sleep(500);
            }
        }
    }

    private void scrollDown() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }
}
