package com.crosscompare.playstationscraper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class PlaystationScraper {
    private WebDriver driver;

    public PlaystationScraper() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        this.driver = new ChromeDriver(options);
    }


}
