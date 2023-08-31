## Easy Apply LinkedIn Bot 🤖

[//]: # (Horizontal rule)
***
Need? Job Search is boring!!! 

Clicking and scrolling through hundreds of job descriptions, and then applying to each one individually is a tedious process. 

Use our bot to automate MOST of the process of applying to jobs on LinkedIn. 

***

# How it works?

Bot will, 
1. Open LinkedIn and login with your credentials.
2. Searches for your job title and location.
3. Applies filters to show only Easy Apply jobs.
4. Opens each job description, takes consent to and applies to the job.
5. Moves to next job and repeats the process.

Click [here](https://www.youtube.com/watch?v=p0o9hinCUGU) 🤖 to watch the video 
# Current Usage

Please note that this bot is still in development. 

Currently, the bot applies for contractor positions, however, with slight modification to filter settings, one can adjust 
for interships, full-times. This is a work in progress..

Clone the library and make changes to `settings.properties` file to adjust the bot to your needs.

# Video with Instructions
Click [here](https://www.youtube.com/watch?v=p0o9hinCUGU) 🤖 to watch the video 

# Prerequisites

* Java 11 or higher (OpenJDK 11.0.19 - I have used Amazon Corretto 11.0.19)
* Apache Maven 3.9.4

# How to use?
1. Clone the repository.<br>
`git clone https://github.com/pheonix-18/linkedin-easy-apply-bot.git`
2. Change directory to linkedin-easy-apply-bot. <br>
`cd linkedin-easy-apply-bot`
3. Create a credentials.properties file in root directory and add your LinkedIn credentials as follows:
<br>
`touch credentials.properties`
<br>
`username=abc@example.com`
<br>
`password=examplePassword`
3. Run Maven commands to install dependencies
4. Run `mvn clean install`
5. Run `Main.java`
6. Enjoy the Magic!

Please raise an issue with detailed info of your environment. 


