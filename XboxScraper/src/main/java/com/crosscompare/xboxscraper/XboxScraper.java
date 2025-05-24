package com.crosscompare.xboxscraper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class XboxScraper {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private WebDriver driver;

    public XboxScraper() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        this.driver = new ChromeDriver(options);
    }

    public ArrayList<Juego> scrape(String juego) throws Exception {
        String juegoUTF8 = URLEncoder.encode(juego, StandardCharsets.UTF_8);
        String url = "https://www.xbox.com/es-ES/Search/Results?q=" + juegoUTF8;

        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.className("SearchProductGrid-module__container___jew-i")));

        Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
        Element ol = doc.getElementsByClass("SearchProductGrid-module__container___jew-i").first();
        ArrayList<Juego> juegos = new ArrayList<>();
        ArrayList<CompletableFuture<Juego>> futures = new ArrayList<>();
        if (ol != null) {
            Elements elementsAll = ol.select("li");
            Elements elements = new Elements();
            for (int i = 0; i < Math.min(6, elementsAll.size()); i++) {
                elements.add(elementsAll.get(i));
            }
            for (Element e : elements) {
                String href = e.select("a").attr("href");
                futures.add(scrapeXbox(href));
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<Juego> future : futures) {
            Juego gameInfo = future.get();
            if (gameInfo != null) {
                juegos.add(gameInfo);
            } else {
                System.out.println("Error: No se ha encontrado la información del juego");
            }
        }
        return juegos;
    }

    @Async
    public CompletableFuture<Juego> scrapeXbox(String url) {
        Juego gameInfo = null;
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("typography-module__xdsH1___7oFBA")));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("Rating-module__starRow___MsDaQ")));
        } catch (TimeoutException _) {

        }
        Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
        // Extracción de datos igual que antes
        String gameTitle = doc.getElementsByClass(
                "typography-module__xdsH1___7oFBA ProductDetailsHeader-module__productTitle___Hce0B").text();
        String description = doc.getElementsByClass(
                "Description-module__description___ylcn4 typography-module__xdsBody2___RNdGY ExpandableText-module__container___Uc17O")
                .text();
        Element elementPriceFinal = doc.getElementsByClass(
                "Price-module__boldText___1i2Li").first();

        String priceFinal = elementPriceFinal != null ? elementPriceFinal.text() : "";

        StringBuilder plataformas = new StringBuilder();
        for (Element div : doc.getElementsByClass(
                "commonStyles-module__basicContainer___8Vx5e FeaturesList-module__item___J8r6P typography-module__xdsTag3___87dP9")) {
            if (div.text().startsWith("Xbox One") || div.text().startsWith("Xbox Series")
                    || div.text().startsWith("PC")) {
                plataformas.append(div.text());
                plataformas.append(",");
            }
        }
        String publisher = "";
        String releaseDate = "";
        for (Element div : doc.getElementsByClass("ModuleColumn-module__col___StJzB")) {
            if (div.getElementsByClass("typography-module__xdsBody1___+TQLW").text().equals("Publicado por")) {
                publisher = div.getElementsByClass("typography-module__xdsBody2___RNdGY").text();
            } else if (div.getElementsByClass("typography-module__xdsBody1___+TQLW").text()
                    .equals("Fecha de lanzamiento")) {
                releaseDate = div.getElementsByClass("typography-module__xdsBody2___RNdGY").text();
            }
        }
        String rating = "";
        Element ratingElement = doc.getElementsByClass("Rating-module__starRow___MsDaQ").first();
        if (ratingElement != null) {
            rating = ratingElement.attr("aria-label");
            String regex = "\\d+\\.\\d+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(rating);
            if (matcher.find()) {
                rating = matcher.group();
            }
        }
        gameInfo = new Juego(gameTitle, description, releaseDate, publisher, plataformas.toString(), priceFinal, rating);

        Map<String, Object> mensaje = new HashMap<>();
        mensaje.put("productor", "XboxScraper");
        mensaje.put("juego", gameInfo.toString());
        redisTemplate.opsForStream().add("UnificadorStream", mensaje);

        return CompletableFuture.completedFuture(gameInfo);
    }
}
