using System;
using OpenQA.Selenium;
using OpenQA.Selenium.Chrome;
using OpenQA.Selenium.Support.UI;

namespace csharp
{
    public class ScrapingDLSite
    {
        private ChromeDriver driver;
    //         self.driver = webdriver.Chrome()
    // self.created_at = datetime.datetime.now().strftime('%Y-%m-%d')
    // self.transition_interval = 5
    // self.table_name = "ArtCollection"
    // self.shop_name = "DLSite"
    // self.dynamodb = connection
        // public ScrapingDLSite()
        // {
            
        // }
    }
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Hello World!");

            using(IWebDriver driver = new ChromeDriver()) {
            WebDriverWait wait = new WebDriverWait(driver, TimeSpan.FromSeconds(10));
            driver.Navigate().GoToUrl("https://www.google.com/ncr");
            driver.FindElement(By.Name("q")).SendKeys("cheese" + Keys.Enter);
            wait.Until(driver =>driver.FindElement(By.CssSelector("h3>div")).Displayed);
            IWebElement firstResult = driver.FindElement(By.CssSelector("h3>div"));
            Console.WriteLine(firstResult.GetAttribute("textContent"));
    }
        }
    }
}
