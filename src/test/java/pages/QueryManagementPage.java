package pages;

import org.openqa.selenium.*;
import utils.WaitUtils;

/**
 * QueryManagementPage - Page Object for Query Management (Pending queries, provide solution).
 */
public class QueryManagementPage {

    private WebDriver driver;

    private By queryMenu = By.xpath("//span[contains(text(),'Query')] | //a[contains(@href,'query')]");
    private By pendingTab = By.xpath("//button[contains(text(),'Pending')] | //div[contains(text(),'Pending')] | //span[contains(text(),'Pending')]");
    private By firstQuery = By.cssSelector("table tbody tr:first-child");
    private By solutionTextArea = By.cssSelector("textarea[placeholder*='solution'], textarea[placeholder*='Solution'], textarea[placeholder*='comment'], textarea[placeholder*='Comment']");
    private By sendSmsNo = By.xpath("//input[@type='radio' and @value='No'] | //label[contains(text(),'No')]/preceding-sibling::input | //label[contains(text(),'No')]");
    private By submitSolutionBtn = By.xpath("//button[contains(text(),'Submit') or contains(text(),'Send Solution') or contains(text(),'Provide Solution')]");
    private By successPopup = By.xpath("//*[contains(text(),'success') or contains(text(),'Success') or contains(text(),'submitted')]");
    private By closedTab = By.xpath("//button[contains(text(),'Closed')] | //div[contains(text(),'Closed')] | //span[contains(text(),'Closed')]");

    public QueryManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToQueryManagement(String baseUrl) throws InterruptedException {
        driver.get(baseUrl + "/query-management");
        Thread.sleep(3000);
    }

    public void clickPendingTab() throws InterruptedException {
        if (WaitUtils.isVisible(driver, pendingTab)) {
            driver.findElement(pendingTab).click();
            Thread.sleep(2000);
        }
    }

    public boolean hasPendingQueries() {
        return WaitUtils.isVisible(driver, firstQuery);
    }

    public void openFirstPendingQuery() throws InterruptedException {
        if (WaitUtils.isVisible(driver, firstQuery)) {
            driver.findElement(firstQuery).click();
            Thread.sleep(3000);
        }
    }

    public void enterSolution(String solutionText) throws InterruptedException {
        // Scroll down to find solution area
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1000);

        if (WaitUtils.isVisible(driver, solutionTextArea)) {
            WebElement el = driver.findElement(solutionTextArea);
            el.clear();
            el.sendKeys(solutionText);
        }
    }

    public void selectSmsNo() throws InterruptedException {
        if (WaitUtils.isVisible(driver, sendSmsNo)) {
            driver.findElement(sendSmsNo).click();
            Thread.sleep(500);
        }
    }

    public void clickSubmitSolution() throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(500);
        if (WaitUtils.isVisible(driver, submitSolutionBtn)) {
            WebElement btn = driver.findElement(submitSolutionBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            Thread.sleep(300);
            btn.click();
            Thread.sleep(4000);
        }
    }

    public boolean isSolutionSubmittedSuccessfully() {
        return WaitUtils.isVisible(driver, successPopup);
    }

    public void clickClosedTab() throws InterruptedException {
        if (WaitUtils.isVisible(driver, closedTab)) {
            driver.findElement(closedTab).click();
            Thread.sleep(2000);
        }
    }

    public int getTableRowCount() {
        By rows = By.cssSelector("table tbody tr");
        if (WaitUtils.isVisible(driver, rows)) {
            return driver.findElements(rows).size();
        }
        return 0;
    }
}
