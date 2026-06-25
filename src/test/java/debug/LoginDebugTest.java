package debug;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class LoginDebugTest {

    @Test
    public void debugLogin() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications", "--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get("https://ks-aams-web.digitalindiacorporation.in/login");
            Thread.sleep(3000);

            // Step 1: Enter mobile
            WebElement mobile = driver.findElement(By.xpath("//input[@placeholder='Enter your mobile number']"));
            mobile.click();
            mobile.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            mobile.sendKeys(Keys.DELETE);
            mobile.sendKeys("8709769742");
            System.out.println("=== Mobile entered: " + mobile.getAttribute("value"));
            Thread.sleep(500);

            // Step 2: Enter password using sendKeys character by character
            WebElement pwd = driver.findElement(By.xpath("//input[@placeholder='Enter password']"));
            pwd.click();
            pwd.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            pwd.sendKeys(Keys.DELETE);
            Thread.sleep(200);
            // Type each character individually
            for (char c : "ICAr@1234".toCharArray()) {
                pwd.sendKeys(String.valueOf(c));
                Thread.sleep(50);
            }
            System.out.println("=== Password typed char by char");
            System.out.println("=== Password field value: " + pwd.getAttribute("value"));
            Thread.sleep(500);

            // Step 3: Tick checkbox only if not already checked
            List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
            System.out.println("=== Found " + checkboxes.size() + " checkboxes");
            for (int i = 0; i < checkboxes.size(); i++) {
                WebElement cb = checkboxes.get(i);
                System.out.println("=== Checkbox " + i + " displayed=" + cb.isDisplayed() + " selected=" + cb.isSelected());
                if (cb.isDisplayed() && !cb.isSelected()) {
                    cb.click();
                    System.out.println("=== Ticked checkbox " + i);
                }
            }
            Thread.sleep(500);

            // Step 4: Scroll down then click Login
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(500);
            WebElement loginBtn = driver.findElement(By.xpath("//button[text()='Login']"));
            System.out.println("=== Login button enabled: " + loginBtn.isEnabled());
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", loginBtn);
            Thread.sleep(300);
            loginBtn.click();
            System.out.println("=== Login clicked");

            // Step 5: Wait for redirect
            Thread.sleep(10000);
            System.out.println("=== POST-LOGIN URL: " + driver.getCurrentUrl());

            // Take screenshot
            try {
                java.io.File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                java.nio.file.Files.copy(src.toPath(), java.nio.file.Paths.get("screenshots/debug_post_login.png"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("=== Screenshot saved: screenshots/debug_post_login.png");
            } catch (Exception e) { }

            if (!driver.getCurrentUrl().contains("/login")) {
                System.out.println("=== LOGIN SUCCESSFUL!");

                // Check for role selection
                List<WebElement> roleItems = driver.findElements(By.xpath("//*[contains(text(),'Expert')]"));
                System.out.println("=== Elements with 'Expert' text: " + roleItems.size());
                for (WebElement r : roleItems) {
                    if (r.isDisplayed()) {
                        System.out.println("    Visible: '" + r.getText() + "' tag=" + r.getTagName());
                    }
                }
            } else {
                System.out.println("=== STILL ON LOGIN PAGE");
                // Print any visible text that might be an error
                String bodyText = driver.findElement(By.tagName("body")).getText();
                // Look for error-like messages
                if (bodyText.contains("Invalid") || bodyText.contains("incorrect") || bodyText.contains("wrong")) {
                    System.out.println("=== ERROR FOUND IN PAGE");
                }
                System.out.println("=== Body (300 chars): " + bodyText.substring(0, Math.min(300, bodyText.length())));
            }

        } finally {
            driver.quit();
        }
    }
}
