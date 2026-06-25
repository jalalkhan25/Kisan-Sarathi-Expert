package pages;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * LoginPage - Exact same pattern as admin automation (which works).
 */
public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//input[@placeholder='Enter your mobile number']")
    private WebElement usernameInput;

    @FindBy(xpath = "//input[@placeholder='Enter password']")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[text()='Login']")
    private WebElement loginButton;

    @FindBy(id = "userRole")
    private WebElement roleDropdown;

    @FindBy(xpath = "//button[text()='Submit']")
    private WebElement roleSubmitButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public boolean isRoleSelectionDisplayed() {
        try {
            Thread.sleep(2000);
            return roleDropdown.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void selectRole(String roleName) {
        wait.until(ExpectedConditions.visibilityOf(roleDropdown));
        new Select(roleDropdown).selectByVisibleText(roleName);
    }

    public void clickRoleSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(roleSubmitButton));
        roleSubmitButton.click();
    }
}
