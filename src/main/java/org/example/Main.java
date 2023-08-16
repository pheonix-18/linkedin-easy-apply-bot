package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
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

    static Random rand;

    public static void randomSleep(int sleep) {
        // print a log message

        try {
            // sleep for a random number of seconds
            int sleeptime = rand.nextInt(3000) + (sleep * 1000);
            Thread.sleep(sleeptime);
            logger.info("Sleeping for " + sleeptime + " seconds");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;
        MUST_NOT_HAVE = new ArrayList<String>(Arrays.asList("architect", "devops", "principal", "lead", "net", "nodejs", "w2", "node", "js", "c"));
        MUST_HAVE = new ArrayList<String>(
                Arrays.asList("senior", "java", "full", "stack", "microservices", "software", "back", "front", "backend", "spring", "boot", "frontend"));
        scanner = new Scanner(System.in);
        rand = new Random();
        Properties properties = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream("/Users/pheonix/IdeaProjects/Automation/credentials.properties");
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
        setFilters();
        boolean choice = false;
        do {
            // Scroll thru the job Description
            List<WebElement> jobCards = driver
            .findElements(By.xpath("/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li"));
            int i = 1;

            for (WebElement card : jobCards) {
                // /html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li[1]
                WebElement view = driver.findElement(
                    By.xpath("/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li[" + i + "]"));

                    scrollToWebElement(view);
                    // get the aria label in this element as job Title In Card,
                    // if it contains any of the MUST_HAVE, then click on the card
                //
                     WebElement aTag = view.findElement(By.cssSelector(".disabled.ember-view.job-card-container__link.job-card-list__title"));

        // Extract the aria-label content
                    String jobTitleInCard = aTag.getAttribute("aria-label");
                    logger.info("jobTitleInCard: " + jobTitleInCard);

        // Print the content

                    logger.info("Clicking job card with xpath: " + "/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li[" + i + "]");
                    i++;

                    // process job Title in Card by removing special characters with space and split the job Title into words
                    jobTitleInCard = jobTitleInCard.trim().replaceAll("[^a-zA-Z0-9]", " ").toLowerCase();
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

                    chooseToApply(scanner);
                }

                choice = offerUserChoices();
            } while (choice);
            // close the browser
        closeBrowser();
    }

    private static void closeBrowser() {
        scanner.close();
        driver.quit();
    }

    private static boolean hasWords(String[] jobTitleInCardWords, List<String> WORDS) {
        boolean mustHave = false;
        for (String word : jobTitleInCardWords) {
            if (WORDS.contains(word)) {
                mustHave = true;
                break;
            }
        }
        return mustHave;
    }
    
    private static boolean offerUserChoices() {
        String jobLocation = "United States";
        String jobTitle = "java developer";
        System.out.println("Choose from Options \n 1. Search new job Title \n 2. Change Job Location \n 3. Click next page & press 3 \n 4. exit \n");
        // take input integer from user
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                Scanner sc2= new Scanner(System.in); //System.in is a standard input stream
                System.out.println("Enter new job title\n");
                jobTitle = sc2.nextLine();              //reads string
                getSearchResultsFor(jobTitle, true);
                break;
            case 2:
                System.out.println("Enter new job location");
                jobLocation = scanner.nextLine();
                break;
            case 3:
                System.out.println("Assuming you clicked on next page");
                break;

            default: 
                // log exiting
                logger.info("Exiting the program");
                return false;
        }
        // call searchForResults
        return true;
    }

    private static boolean elementExists(String xpath) {
        List<WebElement> nextButton = driver.findElements(By.xpath(xpath));
        if (nextButton.size() == 0)
            return false;
        return true;
    }

    private static void chooseToApply(Scanner scanner) {
        System.out.println("Do you want to apply for the job? (y/n) e to exit\n");
        String input = scanner.nextLine();
        if(input.trim().equalsIgnoreCase("e"))
        {
            System.out.println("Exiting the program");
            closeBrowser();
            System.exit(0);
        }
        if (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) {
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
        try {
            WebElement formFooter = driver.findElement(By.xpath(
                    "/html/body/div[3]/div/div/div[2]/div/div[2]/form/footer"));
            WebElement nextButton = formFooter.findElement(By.cssSelector(".artdeco-button--primary.ember-view"));
            //find the button in the formFooter that has the CSS class artdeco-button--primary ember-view
            while (formFooter != null && nextButton != null) {
                scrollToWebElement(nextButton, false);
                logger.info("Found next button");
                //get next button aria lable
                String nextButtonAriaLabel = nextButton.getAttribute("aria-label");
                // log next button aria label
                logger.info("Next button aria label: " + nextButtonAriaLabel);
                nextButton.click();
                //Log Clicked next button
                logger.info("Clicked next button");
                randomSleep(SHORT_SLEEP);
                nextButton = formFooter.findElement(By.cssSelector(".artdeco-button--primary.ember-view"));
            }

            // submit button xpath:
            // /html/body/div[3]/div/div/div[2]/div/div[2]/div/footer/div[3]/button[2]
            /// html/body/div[3]/div/div/div[2]/div/div[2]/div/footer/div[2]/button[2]
//            /html/body/div[3]/div/div/div[2]/div/div[2]/div/footer/div[3]/button[2]
            // scroll to the submit button


        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
        try {
            WebElement submitButton = driver.findElement(By.xpath(
                    "/html/body/div[3]/div/div/div[2]/div/div[2]/div/footer")).findElement(By.cssSelector(".artdeco-button--primary.ember-view"));
            scrollToWebElement(submitButton, false);
            submitButton.click();
            randomSleep(LONG_SLEEP);
        } catch (NoSuchElementException e) {
            WebElement submitButton = driver.findElement(By.xpath(
                    "/html/body/div[3]/div/div/div[2]/div/div/form/footer/")).findElement(By.cssSelector(".artdeco-button--primary.ember-view"));
            scrollToWebElement(submitButton, false);
            submitButton.click();
            randomSleep(LONG_SLEEP);
        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }

        WebElement closeButton = driver.findElement(By.xpath("/html/body/div[3]/div/div/button"));
        closeButton.click();
        // Sleep for 5 secondsn

        // single click submit jobs
//        <button aria-label="Submit application" id="ember1409" class="artdeco-button artdeco-button--2 artdeco-button--primary ember-view" type="button"><!---->
//<span class="artdeco-button__text">
//                Submit application
//                </span></button>
        // xpath: /html/body/div[3]/div/div/div[2]/div/div/form/footer/div[3]/button

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

    private static void scrollToWebElement(WebElement contractExperienceLevel, boolean sleep) {
        js.executeScript("arguments[0].scrollIntoView();", contractExperienceLevel);
        if (sleep)
            randomSleep(SHORT_SLEEP);
    }
    private static void clickWebElement(WebElement webElement) {
        js.executeScript("arguments[0].click();", webElement);
        randomSleep(SHORT_SLEEP);
    }

    private static void getSearchResultsFor(String jobTitle) {
///html/body/div[4]/header/div/div/div/div[2]/div[2]/div/div[2]/input[1]
        WebElement searchField = driver.findElement(By.xpath("/html/body/div[5]/header/div/div/div/div[2]/div[2]/div/div/input[1]"));
        js.executeScript("arguments[0].scrollIntoView();", searchField);
//        scrollToWebElement(searchField);
        sendKeysToWebElement(jobTitle, searchField);
        sendKeysToWebElement("\n", searchField);
    }

    private static void getSearchResultsFor(String jobTitle, boolean newSearch){

        WebElement searchField = driver.findElement(By.xpath("/html/body/div[5]/header/div/div/div/div[2]/div[1]/div/div/input[1]"));
        scrollToWebElement(searchField);
        // Clear the search field
        searchField.clear();
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