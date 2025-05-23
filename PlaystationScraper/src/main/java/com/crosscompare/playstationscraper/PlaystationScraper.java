package com.crosscompare.playstationscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PlaystationScraper {
    private WebDriver driver;

    public PlaystationScraper() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        this.driver = new ChromeDriver(options);
    }

    public String scrape(String juego) throws IOException, ExecutionException, InterruptedException {
        String encodedSearchTerm = URLEncoder.encode(juego, StandardCharsets.UTF_8);
        String url = "https://store.playstation.com/es-es/search/" + encodedSearchTerm;


        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".psw-fill-x.psw-t-truncate-1.psw-l-space-x-2")));


        Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
        Elements elements = doc.getElementsByClass("psw-l-w-1/2@mobile-s psw-l-w-1/2@mobile-l psw-l-w-1/6@tablet-l psw-l-w-1/4@tablet-s psw-l-w-1/6@laptop psw-l-w-1/8@desktop psw-l-w-1/8@max");
        Elements elementsFiltrados = new Elements();
        for (Element element : elements) {
            Element span = element.select("span[data-qa~=search#productTile\\d+#product-type]").first();
            if(span==null || span.text().equals("Paquete de juego")){
                elementsFiltrados.add(element);
            }
        }
        Elements elementsFinal = new Elements();
        if(elementsFiltrados.size()>6){
            for(int k=0; k<6; k++){
                elementsFinal.add(elementsFiltrados.get(k));
            }
        }else {
            elementsFinal = elementsFiltrados;
        }

        ArrayList<Juego> juegos = new ArrayList<>();
        ArrayList<CompletableFuture<Juego>> futures = new ArrayList<>();

        long start = System.nanoTime();


        for (Element e : elementsFinal) {
            String href = "https://store.playstation.com/" + e.select("a").attr("href");
            futures.add(scrapePlaystation(href));
        }

        // Espera a que terminen todos los futuros
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (CompletableFuture<Juego> future : futures) {
            Juego juegoCompleto = future.get();
            if (juegoCompleto != null) {
                juegos.add(juegoCompleto);
            } else {
                System.out.println("Error: No se ha encontrado la información del juego");
            }
        }

        long end = System.nanoTime();
        double durationSeconds = (end - start) / 1_000_000_000.0;

        juegos.add(new Juego("Tiempo de scraping", "", durationSeconds + " s", "", "", "", ""));
        return juegos.toString();
    }


    @Async
    public CompletableFuture<Juego> scrapePlaystation(String url) {

        Juego gameInfo = null;

        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".psw-fill-x.psw-t-truncate-1.psw-l-space-x-2")));

        Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));


        Element divJuego = doc.getElementsByClass("psw-c-bg-card-1 psw-p-y-7 psw-p-x-8 psw-m-sub-x-8 psw-m-sub-x-6@below-tablet-s psw-p-x-6@below-tablet-s").first();
        Element dlJuego = doc.select("dl[data-qa=gameInfo#releaseInformation]").first();
        if (divJuego != null && dlJuego != null) {
            String gameTitle = divJuego.select("h1[data-qa=mfe-game-title#name]").text();
            String publisher = dlJuego.select("dd[data-qa=gameInfo#releaseInformation#publisher-value]").text();
            String releaseDate = dlJuego.select("dd[data-qa=gameInfo#releaseInformation#releaseDate-value]").text();
            String description = doc.select("p[data-qa=mfe-game-overview#description]").text();
            String priceFinal = divJuego.select("span[data-qa=mfeCtaMain#offer0#finalPrice]").text();
            if(priceFinal.isEmpty()){
                priceFinal = divJuego.select("span[data-qa=mfeCtaMain#offer0#originalPrice]").text();
            }
            String platforms = dlJuego.select("dd[data-qa=gameInfo#releaseInformation#platform-value]").text();
            String rating = doc.select("span[data-qa=mfe-star-rating#overall-rating#average-rating]").text();

            gameInfo = new Juego(gameTitle, platforms, priceFinal, publisher, releaseDate, description, rating);
        } else {
            System.out.println("Error: No se ha encontrado la información del juego");
        }

        return CompletableFuture.completedFuture(gameInfo);
    }


}
