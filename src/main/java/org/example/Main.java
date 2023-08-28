package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

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

    private static Boolean fastApply;
    static Scanner scanner;

    static Properties settingsProperties;

    static Wait<WebDriver> wait;

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
        Properties credentialsProperties = getProperties("credentials.properties");
        loginEmail = credentialsProperties.getProperty("username");
        loginPass = credentialsProperties.getProperty("password");
        settingsProperties = getProperties("settings.properties");;

        String property = settingsProperties.getProperty("skipJobsHavingTitleWordsInThisList");
        List<String> mustNotHave = Arrays.asList(property.split(","));
        mustNotHave = Arrays.stream(mustNotHave.toArray(new String[0])).map(String::trim).collect(Collectors.toList());
        MUST_NOT_HAVE = new ArrayList<String>(mustNotHave);
        List<String> mustHave = Arrays.asList(settingsProperties.getProperty("askToApplyForJobsWithTitleWordsInThisList")
                .split(","));
        // trim each word in the list
        mustHave = Arrays.stream(mustHave.toArray(new String[0])).map(String::trim).collect(Collectors.toList());
        MUST_HAVE = new ArrayList<String>(mustHave);
        String fApply = settingsProperties.getProperty("fastApply");
        if(fApply.equalsIgnoreCase("yes"))
            fastApply = true;
        else
            fastApply = false;
        scanner = new Scanner(System.in);
        rand = new Random();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)).ignoring(StaleElementReferenceException.class);

    }

    private static Properties getProperties(String filePath) {
        Properties properties = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath);
            properties.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    public static void main(String[] args) {
        logger = LoggerFactory.getLogger(Main.class);
        //
        loginToLinkedIn();
        randomSleep(SHORT_SLEEP);

        String jobTitle = settingsProperties.getProperty("defaultJobTitle", "full stack java developer");
        logger.info("Search by default for" + jobTitle);
        String jobLocation = settingsProperties.getProperty("defaultJobLocation", "United States");
        getSearchResultsFor(jobTitle);
        String closeMessagesButtonXPath = "/html/body/div[5]/div[4]/aside/div[1]/header/div[3]/button[2]";
        clickButtonByXPath(closeMessagesButtonXPath);
        setFilters();
        boolean choice = false;
        do {

            List<WebElement> jobCards = driver
                    .findElements(By.xpath("/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li"));
            int i = 1;

            for (WebElement card : jobCards) {
                WebElement view = driver.findElement(
                        By.xpath("/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[1]/div/ul/li[" + i + "]"));

                scrollToWebElement(view);

                WebElement aTag = view.findElement(By.cssSelector(".disabled.ember-view.job-card-container__link.job-card-list__title"));

                // Extract the aria-label content
                String jobTitleInCard = aTag.getAttribute("aria-label");
                logger.info("jobTitleInCard: " + jobTitleInCard);
                i++;
                // process job Title in Card by removing special characters except space with space and split the job Title into words

                jobTitleInCard = jobTitleInCard.trim().replaceAll("[^a-zA-Z0-9]", " ");
                // remove mulitple spaces with single space
                jobTitleInCard = jobTitleInCard.replaceAll("\\s+", " ").toLowerCase();
                // scan the jobTitle for must not have words
                if (hasWords(jobTitleInCard, MUST_NOT_HAVE) || !hasWords(jobTitleInCard, MUST_HAVE)) {
                    logger.info("Skipping job: " + jobTitleInCard);
                    continue;
                }
                logger.info("Processing job: " + jobTitleInCard);
                card.click();

                randomSleep(SHORT_SLEEP);
                if (!isThisJobApplied()) {
                    logger.info("Job was previously applied");
                    continue;
                }
                String jobDescriptionXPath = "/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[2]/div/div[2]/div[1]/div/div[4]/article";
//                WebElement jobDescription = driver.findElement(By.class(jobDescriptionXPath));
                WebElement jobDescription = driver.findElement(By.cssSelector("article.jobs-description__container"));

                String jobDescriptionText = jobDescription.getText();
                logger.info("Job Description: " + jobDescriptionText);
                if(filterJobDescription(jobDescriptionText))
                    continue;
                chooseToApply(scanner);
            }
            choice = offerUserChoices();
        } while (choice);
        closeBrowser();
    }

    private static boolean filterJobDescription(String jobDescriptionText) {
        //Replace all special characters with space
        jobDescriptionText = jobDescriptionText.trim().replaceAll("[^a-zA-Z0-9]", " ");
        //Replace all multiple spaces with single space
        jobDescriptionText = jobDescriptionText.replaceAll("\\s+", " ");
        //Convert to lower case
        jobDescriptionText = jobDescriptionText.toLowerCase();
        String defaultExperience = settingsProperties.getProperty("filterJobsThatNeedMoreThanThisYearsOfExperience", "9");
        int defaultExp = Integer.valueOf(defaultExperience);
        List<String> excludeExperienceWords = new ArrayList<>();
        while(defaultExp < 14){
            excludeExperienceWords.add(defaultExp + " years");
            defaultExp++;
        }
        for(String word: excludeExperienceWords) {
            if (jobDescriptionText.contains(word)) {
                logger.info("Filtering job with experience: " + word);
                return true;
            }
        }
        return false;
    }

    private static boolean isThisJobApplied() {
        boolean containsClass;
        try {
            String easyApplyBtnDivXPath = "/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[2]/div/div[2]/div[1]/div/div[1]/div/div[1]/div[1]/div[4]/div/div/div";

            WebElement easyApplyBtnDiv = driver.findElement(By.xpath(easyApplyBtnDivXPath));
            // Check if the easyApplyBtnDiv has class "jobs-apply-button--top-card"
            String className = "jobs-apply-button--top-card";
            containsClass = (boolean) js.executeScript("return arguments[0].classList.contains(arguments[1])", easyApplyBtnDiv, className);
        }
        catch (Exception e){
            containsClass = false;
        }
        return containsClass;
    }

    private static void closeBrowser() {
        scanner.close();
        driver.quit();
    }

    private static boolean hasWords(String jobTitle, List<String> WORDS) {
        boolean mustHave = false;
        //check if the jobTitleInCardWords contains any of the words in the list
        for (String word : WORDS) {
            if (jobTitle.contains(word)) {
                logger.info("Found word: " + word + " in job title: " + jobTitle);
                mustHave = true;
                break;
            }
        }
        return mustHave;
    }

    private static boolean offerUserChoices() {
        String jobLocation = "United States";
        String jobTitle = "full stack java developer";
        System.out.println("Choose from Options \n 1. Search new job Title \n 2. Change Job Location \n 3. Click next page & press 3 \n 4. exit \n");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                Scanner sc2 = new Scanner(System.in); //System.in is a standard input stream
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
        if(fastApply){
            applyForThisJob();
            return;
        }
        System.out.println("Do you want to apply for the job? (y/n) e to exit");
        String input = scanner.nextLine();
        if (input.trim().equalsIgnoreCase("e")) {
            System.out.println("Exiting the program");
            closeBrowser();
            System.exit(0);
        }
        if (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) {
            // Click on apply button
            applyForThisJob();
        }
    }

    private static void applyForThisJob() {
        WebElement applyButton = driver.findElement(By.xpath(
                "/html/body/div[5]/div[3]/div[4]/div/div/main/div/div[2]/div/div[2]/div[1]/div/div[1]/div/div[1]/div[1]/div[4]/div/div/div/button"));
        applyButton.click();

        // xpath for next button:
        // /html/body/div[3]/div/div/div[2]/div/div[2]/form/footer/div[2]/button

        // submit button xpath:
        // /html/body/div[3]/div/div/div[2]/div/div[2]/div/footer/div[3]/button[2]
        /// html/body/div[3]/div/div/div[2]/div/div[2]/div/footer/div[2]/button[2]
        ///html/body/div[3]/div/div/div[2]/div/div[2]/div/footer/div[3]/button[2]
        // scroll to the submit button

        try {
            WebElement formFooter = driver.findElement(By.xpath(
                    "/html/body/div[3]/div/div/div[2]/div/div[2]/form/footer"));
            WebElement nextButton = formFooter.findElement(By.cssSelector(".artdeco-button--primary.ember-view"));
            //find the button in the formFooter that has the CSS class artdeco-button--primary ember-view
            while (formFooter != null && nextButton != null) {
                scrollToWebElement(nextButton, false);
                logger.info("Found next button");
                nextButton.click();
                logger.info("Clicked next button");
                randomSleep(SHORT_SLEEP);
                nextButton = formFooter.findElement(By.cssSelector(".artdeco-button--primary.ember-view"));
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        try {
            WebElement submitButton = driver.findElement(By.xpath(
                    "/html/body/div[3]/div/div/div[2]/div/div[2]/div/footer")).findElement(By.cssSelector(".artdeco-button--primary.ember-view"));
            scrollToWebElement(submitButton, false);
            submitButton.click();
        } catch (NoSuchElementException e) {
            WebElement submitButton = driver.findElement(By.xpath(
                    "/html/body/div[3]/div/div/div[2]/div/div/form/footer")).findElement(By.cssSelector(".artdeco-button--primary.ember-view"));
            scrollToWebElement(submitButton, false);
            submitButton.click();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        String closeButtonXPath = "/html/body/div[3]/div/div/button";
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .ignoring(StaleElementReferenceException.class)
                .until((WebDriver d)->{
                    d.findElement(By.xpath(closeButtonXPath)).click();
                    return true;
                });
    }
    //
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
    }

    private static void scrollToWebElement(WebElement contractExperienceLevel, boolean sleep) {
        js.executeScript("arguments[0].scrollIntoView();", contractExperienceLevel);
        if (sleep)
            randomSleep(SHORT_SLEEP);
    }

    private static void clickWebElement(WebElement webElement) {
        js.executeScript("arguments[0].click();", webElement);
    }

    private static void getSearchResultsFor(String jobTitle) {
        String searchFieldXPathType1 = "/html/body/div[5]/header/div/div/div/div[2]/div[2]/div/div/input[1]";
        String searchFieldXPathType2 = "/html/body/div[4]/header/div/div/div/div[2]/div[2]/div/div[2]/input[1]";
        WebElement searchField = null;
        try {
            searchField = driver.findElement(By.xpath(searchFieldXPathType1));
        }
        catch(Exception e){
            searchField = driver.findElement(By.xpath(searchFieldXPathType2));
        }
        WebElement finalSearchField = searchField;
        wait.until(d -> finalSearchField.isDisplayed());
        js.executeScript("arguments[0].scrollIntoView();", searchField);
        sendKeysToWebElement(jobTitle, searchField);
        sendKeysToWebElement("\n", searchField);
    }

    private static void getSearchResultsFor(String jobTitle, boolean newSearch) {

        WebElement searchField = driver.findElement(By.xpath("/html/body/div[5]/header/div/div/div/div[2]/div[1]/div/div/input[1]"));
        scrollToWebElement(searchField);
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
        enterIntoTextFieldById(emailInputBoxID, loginEmail);
        enterIntoTextFieldById(passwordInputBoxID, loginPass);
        String loginButtonXPath = "//button[@data-id='sign-in-form__submit-btn']";
        clickButtonByXPath(loginButtonXPath);

    }

    private static void clickButtonByXPath(String xpath) {

        WebElement button = driver.findElement(By.xpath(xpath));
        wait.until(d -> button.isDisplayed());
        button.click();
    }

    private static void enterIntoTextFieldById(String inputBoxId, String inputText) {
        WebElement emailField = driver.findElement(By.id(inputBoxId));
        sendKeysToWebElement(inputText, emailField);
    }
}