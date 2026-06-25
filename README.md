# Kisan Sarathi 2.0 - KVK Expert Automation Framework

## Overview
Hybrid Automation Testing Framework for the KVK Expert module of Kisan Sarathi 2.0.

- **Language:** Java 11
- **Build Tool:** Maven
- **Test Framework:** TestNG
- **Design Pattern:** Page Object Model (POM)
- **Reporting:** Extent Reports + Email Report
- **Browser:** Chrome (configurable)

## Project Structure
```
ks-expert-automation/
├── pom.xml
├── testng.xml
├── run-tests.bat
├── src/test/
│   ├── java/
│   │   ├── base/BaseTest.java
│   │   ├── pages/
│   │   │   ├── LoginPage.java
│   │   │   ├── FarmerRegistrationPage.java
│   │   │   ├── AdvisoryManagementPage.java
│   │   │   └── QueryManagementPage.java
│   │   ├── tests/
│   │   │   ├── LoginTest.java
│   │   │   ├── FarmerRegistrationTest.java
│   │   │   ├── AdvisoryManagementTest.java
│   │   │   └── QueryManagementTest.java
│   │   ├── utils/
│   │   │   ├── ConfigReader.java
│   │   │   ├── WaitUtils.java
│   │   │   ├── ScreenshotUtils.java
│   │   │   ├── ExtentReportManager.java
│   │   │   └── EmailUtil.java
│   │   └── listeners/TestListener.java
│   └── resources/config.properties
├── reports/ (generated)
└── screenshots/ (generated)
```

## Setup
1. Install Java 11+ and Maven
2. Clone this repo
3. Update `src/test/resources/config.properties` with credentials

## Run Tests
```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

Or use the batch file:
```bash
run-tests.bat
```

## Reports
- HTML Report: `reports/TestReport.html`
- Screenshots: `screenshots/` (on failure)
- Email: Auto-sent after every run

## Test Scenarios
1. **Login** - KVK Expert authentication
2. **Farmer Registration** - Register new farmer with unique phone
3. **Advisory Management** - Create and send advisory
4. **Query Management** - Resolve pending query

## QA Engineer
Jalal Khan
