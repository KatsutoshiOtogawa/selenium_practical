package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Properties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
 
        Properties properties = new Properties();
        try 
        {
            // .env file is the same format with propertiy file.
            properties.load(new FileInputStream(String.join("/",System.getProperty("user.dir"),"resources",".env")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return;
        }
        System.out.println(properties.getProperty("DLSITE_AFFILIATE_SITE"));
        //System.getenv("PATH");

        List<String> lines = null;
        try 
        {
            lines = Files.readAllLines(Paths.get(System.getProperty("user.dir"),"resources","ArtName.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) 
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return;
        }

        WebDriver driver = new ChromeDriver();

        WebDriverWait wait = new WebDriverWait(driver, 60);
        try 
        {
            driver.get("https://google.com/ncr");
            driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
            WebElement firstResult = wait.until(presenceOfElementLocated(By.cssSelector("h3>div")));
            System.out.println(firstResult.getAttribute("textContent"));
        } finally {
            driver.quit();
        }
    }
}
