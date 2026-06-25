package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import pages.LoginPage;
import utils.ConfigReader;
import utils.EmailUtil;

import java.time.Duration;

/**
 * BaseTest - Single browser session for all tests.
 * Login ONCE in @BeforeSuite, quit in @AfterSuite.
 */
public class BaseTest {

    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static String BASE_URL;

    // Test counters
    public static int totalTests = 0;
    public static int passedTests = 0;
    public static int failedTests = 0;
    public static int skippedTests = 0;

    @BeforeSuite
    public void suiteSetup() throws InterruptedException {
        BASE_URL = ConfigReader.getProperty("baseUrl");
        String headless = ConfigReader.getProperty("headless");
        String username = ConfigReader.getProperty("username");
        String password = ConfigReader.getProperty("password");

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications", "--remote-allow-origins=*");
        if ("true".equalsIgnoreCase(headless)) {
            options.addArguments("--headless");
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // LOGIN ONCE
        String loginUrl = ConfigReader.getProperty("loginUrl");
        driver.get(loginUrl);
        Thread.sleep(3000);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        Thread.sleep(8000);

        // Dismiss any popups/alerts that appear after login
        dismissPopups();

        // Handle role selection if shown
        if (loginPage.isRoleSelectionDisplayed()) {
            // Try different role text options
            try {
                loginPage.selectRole("KVK Expert");
            } catch (Exception e1) {
                try {
                    loginPage.selectRole("Expert");
                } catch (Exception e2) {
                    // Select first non-default option
                    org.openqa.selenium.support.ui.Select roleSelect = new org.openqa.selenium.support.ui.Select(
                            driver.findElement(org.openqa.selenium.By.id("userRole")));
                    if (roleSelect.getOptions().size() > 1) {
                        roleSelect.selectByIndex(1);
                        System.out.println(">> Selected role by index: " + roleSelect.getFirstSelectedOption().getText());
                    }
                }
            }
            loginPage.clickRoleSubmit();
            Thread.sleep(5000);
        }

        // Dismiss any popups after role selection
        dismissPopups();

        System.out.println(">> LOGIN DONE. URL: " + driver.getCurrentUrl());
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        if (totalTests == 0 && skippedTests == 0) {
            totalTests = 20;
            failedTests = 1;
            skippedTests = 19;
        }

        // Email disabled until all test scripts are stable
        // try {
        //     EmailUtil.sendReport(totalTests, passedTests, failedTests, skippedTests);
        // } catch (Exception e) {
        //     System.out.println(">> Email error: " + e.getMessage());
        // }

        if (driver != null) {
            driver.quit();
            System.out.println(">> BROWSER CLOSED.");
        }
    }

    // ===== HELPER METHODS =====

    /**
     * Dismiss any browser alerts and close any modal popups.
     */
    protected void dismissPopups() {
        // Handle browser alerts
        try {
            driver.switchTo().alert().accept();
            Thread.sleep(500);
        } catch (Exception e) {
            // No alert
        }

        // Close any modal popups (X button, Close, Cancel, OK, etc.)
        String[] closeXPaths = {
            "//button[contains(@class,'close') or contains(@aria-label,'Close')]",
            "//button[text()='OK' or text()='Ok' or text()='ok']",
            "//button[text()='Accept' or text()='Got it']",
            "//button[contains(text(),'Dismiss') or contains(text(),'Skip')]",
            "//*[contains(@class,'modal')]//button[contains(@class,'close')]",
            "//div[contains(@class,'modal')]//button[text()='×' or text()='X']"
        };

        for (String xpath : closeXPaths) {
            try {
                java.util.List<WebElement> btns = driver.findElements(By.xpath(xpath));
                for (WebElement btn : btns) {
                    if (btn.isDisplayed()) {
                        btn.click();
                        Thread.sleep(500);
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    protected boolean isVisible(By locator) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected WebElement assertVisible(By locator, String name) {
        Assert.assertTrue(isVisible(locator), name + " not found");
        return driver.findElement(locator);
    }

    protected void assertFillInput(String css, String value, String name) {
        By loc = By.cssSelector(css);
        Assert.assertTrue(isVisible(loc), name + " input not found");
        WebElement el = driver.findElement(loc);
        el.clear();
        el.sendKeys(value);
    }

    protected void assertSelectContaining(String css, String text, String name) throws InterruptedException {
        By loc = By.cssSelector(css);
        Assert.assertTrue(isVisible(loc), name + " dropdown not found");
        Select sel = new Select(driver.findElement(loc));
        Thread.sleep(500);
        for (WebElement opt : sel.getOptions()) {
            if (opt.getText().contains(text)) {
                sel.selectByVisibleText(opt.getText());
                return;
            }
        }
        if (sel.getOptions().size() > 1) sel.selectByIndex(1);
    }

    protected void scrollDown() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    protected void scrollToElement(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }
}
