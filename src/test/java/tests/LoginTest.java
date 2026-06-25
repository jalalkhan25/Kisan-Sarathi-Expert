package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest - Verify KVK Expert login and dashboard access.
 */
public class LoginTest extends BaseTest {

    @Test(priority = 1)
    public void verifyLoginSuccessful() throws InterruptedException {
        // Login already done in @BeforeSuite, just verify dashboard
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/home") || url.contains("/dashboard") || !url.contains("/login"),
                "Login failed. Still on login page. URL: " + url);
        System.out.println("[PASS] Login verified. URL: " + url);
    }

    @Test(priority = 2)
    public void verifyDashboardElements() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);
        // Verify some dashboard element is visible
        boolean hasDashboard = isVisible(By.xpath("//*[contains(text(),'Dashboard') or contains(text(),'Inbound') or contains(text(),'Outbound') or contains(text(),'Query')]"));
        Assert.assertTrue(hasDashboard, "Dashboard elements not found after login");
        System.out.println("[PASS] Dashboard elements visible");
    }
}
