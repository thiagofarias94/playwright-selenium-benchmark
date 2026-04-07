package benchmark;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1280,800");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @RepeatedTest(5)
    void ct01LoginValido(RepetitionInfo repetitionInfo) {
        System.out.println("CT01 - Execução " + repetitionInfo.getCurrentRepetition() + "/5");
        driver.get("https://www.saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
        assertEquals("https://www.saucedemo.com/inventory.html", driver.getCurrentUrl());
    }

    @RepeatedTest(5)
    void ct02LoginInvalido(RepetitionInfo repetitionInfo) {
        System.out.println("CT02 - Execução " + repetitionInfo.getCurrentRepetition() + "/5");
        driver.get("https://www.saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("invalid_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();
        assertTrue(driver.findElement(By.cssSelector("[data-test='error']")).isDisplayed());
    }

    @RepeatedTest(5)
    void ct03FluxoCompletoE2E(RepetitionInfo repetitionInfo) {
        System.out.println("CT03 - Execução " + repetitionInfo.getCurrentRepetition() + "/5");
        // Login
        driver.get("https://www.saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Add product to cart
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();

        // Go to cart
        driver.findElement(By.className("shopping_cart_link")).click();

        // Checkout
        driver.findElement(By.cssSelector("[data-test='checkout']")).click();

        // Fill checkout form
        driver.findElement(By.cssSelector("[data-test='firstName']")).sendKeys("John");
        driver.findElement(By.cssSelector("[data-test='lastName']")).sendKeys("Doe");
        driver.findElement(By.cssSelector("[data-test='postalCode']")).sendKeys("12345");
        driver.findElement(By.cssSelector("[data-test='continue']")).click();

        // Complete purchase
        driver.findElement(By.cssSelector("[data-test='finish']")).click();
        assertEquals("Thank you for your order!", driver.findElement(By.className("complete-header")).getText());
    }

    @RepeatedTest(5)
    void ct04ElementoDinamicoEspera(RepetitionInfo repetitionInfo) {
        System.out.println("CT04 - Execução " + repetitionInfo.getCurrentRepetition() + "/5");
        driver.get("https://www.saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Wait for inventory to load dynamically
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_item")));
        assertEquals(6, driver.findElements(By.className("inventory_item")).size());
    }

    @RepeatedTest(5)
    void ct05InteracaoMultiplosElementos(RepetitionInfo repetitionInfo) {
        System.out.println("CT05 - Execução " + repetitionInfo.getCurrentRepetition() + "/5");
        driver.get("https://www.saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Filter products by price low to high
        driver.findElement(By.className("product_sort_container")).sendKeys("lohi");

        // Add multiple products to cart
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")).click();
        driver.findElement(By.cssSelector("[data-test='add-to-cart-sauce-labs-bike-light']")).click();

        // Check cart badge
        assertEquals("2", driver.findElement(By.className("shopping_cart_badge")).getText());
    }
}
