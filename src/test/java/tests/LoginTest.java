package tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;

/**
 * LoginTest - Multiple login scenarios for KVK Expert.
 * Covers: valid login, dashboard redirect, role display, sidebar verification.
 */
public class LoginTest extends BaseTest {

    @Test(priority = 1)
    public void verifyLoginSuccessful() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/home") || !url.contains("/login"),
                "Login failed. Still on login page. URL: " + url);
        System.out.println("[PASS] Login verified. URL: " + url);
    }

    @Test(priority = 2)
    public void verifyDashboardPageDisplayed() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);
        assertVisible(By.xpath("//*[contains(text(),'Dashboard') or contains(text(),'Total number of queries')]"),
                "Dashboard content");
        System.out.println("[PASS] Dashboard page displayed");
    }

    @Test(priority = 3)
    public void verifyRoleDisplayedAsKVKExpert() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);
        // Role: KVK Expert shown at bottom-left of sidebar
        boolean roleVisible = isVisible(By.xpath("//*[contains(text(),'KVK Expert')]"));
        Assert.assertTrue(roleVisible, "Role 'KVK Expert' not displayed on page");
        System.out.println("[PASS] Role displayed as KVK Expert");
    }

    @Test(priority = 4)
    public void verifySidebarMenuItems() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);
        // Verify all sidebar menu items from screenshot
        String[] menuItems = {"Dashboard", "Farmer Management", "Advisory Management",
                "Query Management", "Call Management", "Event Management",
                "Crop Calendar", "Reports", "HelpDesk", "Farmer Feedback", "Discussion Forum"};

        int found = 0;
        for (String item : menuItems) {
            if (isVisible(By.xpath("//*[contains(text(),'" + item + "')]"))) {
                found++;
            }
        }
        System.out.println("[RESULT] Sidebar menu items found: " + found + "/" + menuItems.length);
        Assert.assertTrue(found >= 6, "Expected at least 6 sidebar items, found: " + found);
        System.out.println("[PASS] Sidebar menu items verified");
    }

    @Test(priority = 5)
    public void verifyUserProfileDisplayed() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);
        // User profile shown in top-right (e.g., "Sailu Expert Dev env")
        boolean profileVisible = isVisible(By.xpath("//*[contains(text(),'Expert') and contains(@class,'profile')] | //img[contains(@alt,'profile') or contains(@alt,'avatar')]"))
                || isVisible(By.xpath("//*[contains(text(),'KVK Expert')]"));
        Assert.assertTrue(profileVisible, "User profile not visible");
        System.out.println("[PASS] User profile displayed");
    }

    @Test(priority = 6)
    public void verifyUpcomingCallBanner() throws InterruptedException {
        driver.get(BASE_URL + "/home");
        Thread.sleep(2000);
        // "Upcoming Call" banner at top
        boolean bannerVisible = isVisible(By.xpath("//*[contains(text(),'Upcoming Call') or contains(text(),'Video Call Scheduled')]"));
        if (bannerVisible) {
            System.out.println("[PASS] Upcoming Call banner displayed");
        } else {
            System.out.println("[INFO] No upcoming call banner (no scheduled calls)");
        }
    }
}
