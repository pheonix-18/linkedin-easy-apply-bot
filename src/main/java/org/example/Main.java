package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private static final String passwordInputBoxID = "session_password";
    private static final String emailInputBoxID = "session_key";
    public static final int SHORT_SLEEP = 1;
    public static final int MEDIUM_SLEEP = 2;
    public static final int LONG_SLEEP = 5;
    public static final String linkedInSite = "https://www.linkedin.com/jobs";
    public static String loginEmail;
    public static String loginPass;
    static WebDriver driver;
    static JavascriptExecutor js;
    public static final List<String> MUST_NOT_HAVE, MUST_HAVE;
    private static Logger logger;
    static Scanner scanner;

    public static void randomSleep(int sleep) {
        // print a log message
        logger.info("Sleeping for " + sleep + " seconds");
        Random rand = new Random();
        try {
            Thread.sleep((rand.nextInt(3) + sleep) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;
        MUST_NOT_HAVE = new ArrayList<String>(Arrays.asList("architect", "devops", "principal", "lead", "net", "nodejs", "node js", "c"));
        MUST_HAVE = new ArrayList<String>(
                Arrays.asList("senior", "java", "full", "stack", "microservices", "engineer", "software", "backend", "spring", "boot", "frontend"));
        scanner = new Scanner(System.in);
        Properties properties = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream("../../../credentials.properties");
            properties.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loginEmail = properties.getProperty("username");
        loginPass = properties.getProperty("password");

    }

    public static void main(String[] args) {
        logger = LoggerFactory.getLogger(Main.class);
        //
        loginToLinkedIn();
        randomSleep(SHORT_SLEEP);

        String jobTitle = "full stack java developer";
        logger.info("Search by default for" + jobTitle);
        String jobLocation = "United States";
        getSearchResultsFor(jobTitle);
        String closeMessagesButtonXPath = "/html/body/div[5]/div[4]/aside/div[1]/header/div[3]/button[2]";
        clickButtonByXPath(closeMessagesButtonXPath);
        //setFilters();
        boolean choice = false;
        do {
//                 <div id="ember2602" class="jobs-search-results-list__pagination pv5  artdeco-pagination ember-view"><!---->
//   <ul class="artdeco-pagination__pages artdeco-pagination__pages--number">
//         <li data-test-pagination-page-btn="1" id="ember2603" class="artdeco-pagination__indicator artdeco-pagination__indicator--number active selected ember-view">  <button aria-current="true" aria-label="Page 1" type="button">
//     <span>1</span>
//   </button>

// </li>
//         <li data-test-pagination-page-btn="2" id="ember2604" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view">  <button aria-label="Page 2" type="button" data-ember-action="" data-ember-action-2605="2605">
//     <span>2</span>
//   </button>

// </li>
//   </ul>
//     <div class="artdeco-pagination__page-state">
//       Page 1 of 2
//     </div>

// <!----></div>

// <div id="ember2602" class="jobs-search-results-list__pagination pv5 jobs-search-results-list__pagination--hide-last-page artdeco-pagination ember-view"><!---->
//   <ul class="artdeco-pagination__pages artdeco-pagination__pages--number">
//         <li data-test-pagination-page-btn="1" id="ember2993" class="artdeco-pagination__indicator artdeco-pagination__indicator--number active selected ember-view">  <button aria-current="true" aria-label="Page 1" type="button">
//     <span>1</span>
//   </button>

// </li>
//         <li data-test-pagination-page-btn="2" id="ember2994" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view">  <button aria-label="Page 2" type="button" data-ember-action="" data-ember-action-2995="2995">
//     <span>2</span>
//   </button>

// </li>
//         <li data-test-pagination-page-btn="3" id="ember2996" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view">  <button aria-label="Page 3" type="button" data-ember-action="" data-ember-action-2997="2997">
//     <span>3</span>
//   </button>

// </li>
//         <li data-test-pagination-page-btn="4" id="ember2998" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view">  <button aria-label="Page 4" type="button" data-ember-action="" data-ember-action-2999="2999">
//     <span>4</span>
//   </button>

// </li>
//         <li data-test-pagination-page-btn="5" id="ember3000" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view">  <button aria-label="Page 5" type="button" data-ember-action="" data-ember-action-3001="3001">
//     <span>5</span>
//   </button>

// </li>
//         <li data-test-pagination-page-btn="6" id="ember3002" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view">  <button aria-label="Page 6" type="button" data-ember-action="" data-ember-action-3003="3003">
//     <span>6</span>
//   </button>

// </li>
//         <li data-test-pagination-page-btn="7" id="ember3004" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view">  <button aria-label="Page 7" type="button" data-ember-action="" data-ember-action-3005="3005">
//     <span>7</span>
//   </button>

// </li>
//         <li data-test-pagination-page-btn="8" id="ember3006" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view">  <button aria-label="Page 8" type="button" data-ember-action="" data-ember-action-3007="3007">
//     <span>8</span>
//   </button>

// </li>
//         <li id="ember3008" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view"><button aria-label="Page 9" type="button" data-ember-action="" data-ember-action-3009="3009">
//   <span>…</span>
// </button>
// </li>
//         <li data-test-pagination-page-btn="40" id="ember3010" class="artdeco-pagination__indicator artdeco-pagination__indicator--number ember-view">  <button aria-label="Page 40" type="button" data-ember-action="" data-ember-action-3011="3011">
//     <span>40</span>
//   </button>

// </li>
//   </ul>
//     <div class="artdeco-pagination__page-state">
//       Page 1 of 40
//     </div>

// <!----></div>
            // Scroll thru the job Description
            List<WebElement> jobCards = driver
            .findElements(By.xpath("/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li"));
            int i = 1;
            
            for (WebElement card : jobCards) {
                // /html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li[1]
                WebElement view = driver.findElement(
                    By.xpath("/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li[" + i + "]"));
                    scrollToWebElement(view);
                    // randomSleep(SHORT_SLEEP);
                    
                    ///html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li[1]/div/div[1]/div[1]/div[2]/div[1]/a
                    ///html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li[3]/div/div[1]/div[1]/div[2]/div[1]/a
                    // get the aria label in this element as job Title In Card, 
                    // if it contains any of the MUST_HAVE, then click on the card
                     WebElement aTag = view.findElement(By.cssSelector(".disabled.ember-view.job-card-container__link.job-card-list__title"));

        // Extract the aria-label content
                    String jobTitleInCard = aTag.getAttribute("aria-label");

        // Print the content
                    System.out.println("aria-label content: " + jobTitleInCard);
                    // String jobTitleInCard = view.findElement(By.xpath("/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li["+i + "]/div/div[1]/div[1]/div[2]/div[1]/a")).getAttribute("aria-label");
                    i++;

                    if (jobTitleInCard == null) {
                        continue;
                    }
                    // process job Title in Card by removing special characters with space and split the job Title into words
                    jobTitleInCard = jobTitleInCard.replaceAll("[^a-zA-Z0-9]", " ").toLowerCase();
                    String[] jobTitleInCardWords = jobTitleInCard.split(" ");
                    
                    // check if any of the MUST_HAVE words are in the job Title in Card
                    
                    // scan the jobTitle for must not have words
                    if(hasWords(jobTitleInCardWords, MUST_NOT_HAVE) || !hasWords(jobTitleInCardWords, MUST_HAVE))
                    {
                        logger.info("Skipping job: " + jobTitleInCard);
                        continue;
                    }
                    logger.info("Processing job: " + jobTitleInCard);
                    card.click();
                    
                    // Skipping already applied Jobs
                    String easyApplyBtnInJobDesc = "/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[2]/div/div[2]/div[1]/div/div[1]/div/div[1]/div[1]/div[4]/div/div/div/button";
                    if (!elementExists(easyApplyBtnInJobDesc)) {
                        continue;
                    }
                    
                    //chooseToApply(scanner);
                }
                choice = offerUserChoices();
            } while (choice);
            // close the browser
            scanner.close();
        driver.quit();
    }

    private static boolean hasWords(String[] jobTitleInCardWords, List<String> WORDS) {
        boolean mustHave = false;
        for (String word : jobTitleInCardWords) {
            if (WORDS.contains(word)) {
                mustHave = true;
                logger.info("Found word: " + word);
                break;
            }
        }
        return mustHave;
    }
    
    private static Boolean offerUserChoices() {
        String jobLocation = "United States";
        String jobTitle = "full stack java developer";
        System.out.println("Choose from Options \n 1. Search new job Title \n 2.Change Job Location 3. exit\n");
        // take input integer from user
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.println("Enter new job title");
                jobTitle = scanner.nextLine();
                break;
            case 2:
                System.out.println("Enter new job location");
                jobLocation = scanner.nextLine();
                break;
            default: 
                // log exiting
                logger.info("Exiting the program");
                return false;
        }
        // call searchForResults
        getSearchResultsFor(jobTitle);
        return true;
    }

    private static boolean elementExists(String xpath) {
        List<WebElement> nextButton = driver.findElements(By.xpath(xpath));
        if (nextButton.size() == 0)
            return false;
        return true;
    }

    private static void chooseToApply(Scanner scanner) {
        System.out.println("Do you want to apply for the job? (yes/no)");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("yes")) {
            // Click on apply button
            applyForThisJob();
        }
    }
    //disabled ember-view job-card-container__link job-card-list__title

    private static void applyForThisJob() {
        WebElement applyButton = driver.findElement(By.xpath(
                "/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[2]/div/div[2]/div[1]/div/div[1]/div/div[1]/div[1]/div[4]/div/div/div/button"));
        applyButton.click();

        // xpath for next button:
        // /html/body/div[3]/div/div/div[2]/div/div[2]/form/footer/div[2]/button
        List<WebElement> nextButton = driver.findElements(By.xpath(
                "/html/body/div[3]/div/div/div[2]/div/div[2]/form/footer/div[2]/button"));
        int buttonAppender = 2;
        while (nextButton.size() != 0) {
            randomSleep(LONG_SLEEP);
            nextButton.get(0).click();
            randomSleep(MEDIUM_SLEEP);
            nextButton = driver.findElements(By.xpath(
                    "/html/body/div[3]/div/div/div[2]/div/div[2]/form/footer/div[2]/button[" + buttonAppender + "]"));
        }

        // submit button xpath:
        // /html/body/div[3]/div/div/div[2]/div/div[2]/div/footer/div[3]/button[2]
        /// html/body/div[3]/div/div/div[2]/div/div[2]/div/footer/div[2]/button[2]
        WebElement submitButton = null;
        try {
            submitButton = driver.findElement(By.xpath(
                    "/html/body/div[3]/div/div/div[2]/div/div[2]/div/footer/div[3]/button[2]"));
        } catch (Exception e) {
            // Send alert to user to manually submit the application
            logger.info("Please submit the application manually");
            // Send user press to continue
            scanner.nextLine();
            return;
            /// html/body/div[3]/div/div/div[2]/div/div/form/footer/div[3]/button
        }
        // scroll to the submit button
        scrollToWebElement(submitButton);
        submitButton.click();
        randomSleep(LONG_SLEEP);

        WebElement closeButton = driver.findElement(By.xpath("/html/body/div[3]/div/div/button"));
        closeButton.click();
        // Sleep for 5 seconds
        randomSleep(SHORT_SLEEP);
        // Wait for 5 seconds

    }

    private static void setFilters() {

        String allFiltersButtonXPath = "//div[@class='relative mr2']/button";
        clickButtonByXPath(allFiltersButtonXPath);

        String id = "advanced-filter-sortBy-DD";
        findAndClickById(id);

        String timeFilter = "advanced-filter-timePostedRange-r86400";
        findAndClickById(timeFilter);

        String jobTypeFilter = "advanced-filter-jobType-C";
        findScrollAndClickById(jobTypeFilter);

        String easyApplyToggleXPath = "//input[@role='switch' and contains(@class, 'artdeco-toggle__button')]";
        findScrollAndClickByXPath(easyApplyToggleXPath);

        String showResultsButtonXPath = "//button[@data-test-reusables-filters-modal-show-results-button='true']";
        findScrollAndClickByXPath(showResultsButtonXPath);
        randomSleep(LONG_SLEEP);
    }

    private static void findScrollAndClickByXPath(
            String easyApplyToggleXPath) {
        WebElement easyApplyToggle = driver
                .findElement(By.xpath(easyApplyToggleXPath));

        scrollToWebElement(easyApplyToggle);
        clickWebElement(easyApplyToggle);
    }

    private static void findScrollAndClickById(String jobTypeFilter) {
        WebElement contractExperienceLevel = driver.findElement(By.id(jobTypeFilter));
        scrollToWebElement(contractExperienceLevel);
        clickWebElement(contractExperienceLevel);
    }

    private static void findAndClickById(String id) {
        WebElement sortByMostRecent = driver.findElement(By.id(id));
        clickWebElement(sortByMostRecent);
    }

    private static void scrollToWebElement(WebElement contractExperienceLevel) {
        js.executeScript("arguments[0].scrollIntoView();", contractExperienceLevel);
        randomSleep(SHORT_SLEEP);
    }

    private static void clickWebElement(WebElement webElement) {
        js.executeScript("arguments[0].click();", webElement);
        randomSleep(SHORT_SLEEP);
    }

    private static void getSearchResultsFor(String jobTitle) {
        WebElement searchField = driver
                .findElement(By.xpath("/html/body/div[5]/header/div/div/div/div[2]/div[2]/div/div/input[1]"));
        randomSleep(SHORT_SLEEP);
        sendKeysToWebElement(jobTitle, searchField);
        sendKeysToWebElement("\n", searchField);
    }

    private static void sendKeysToWebElement(String jobTitle, WebElement searchField) {
        searchField.sendKeys(jobTitle);
        randomSleep(SHORT_SLEEP);
    }

    private static void loginToLinkedIn() {

        driver.get(linkedInSite);
        // Wait for page to load for 5 seconds
        enterIntoTextFieldById(emailInputBoxID, loginEmail);
        enterIntoTextFieldById(passwordInputBoxID, loginPass);
        String loginButtonXPath = "//button[@data-id='sign-in-form__submit-btn']";
        clickButtonByXPath(loginButtonXPath);

    }

    private static void clickButtonByXPath(String xpath) {
        WebElement button = driver.findElement(By.xpath(xpath));
        button.click();
        randomSleep(SHORT_SLEEP);
    }

    private static void enterIntoTextFieldById(String inputBoxId, String inputText) {
        WebElement emailField = driver.findElement(By.id(inputBoxId));
        sendKeysToWebElement(inputText, emailField);
    }
}