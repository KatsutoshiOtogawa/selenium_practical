using System;
using System.Net.Sockets;
using OpenQA.Selenium;
using OpenQA.Selenium.Chrome;
using OpenQA.Selenium.Support.UI;
using dotenv.net;
using Amazon.DynamoDBv2;

namespace csharp
{
    public class ScrapingDLSite
    {
        private IWebDriver Driver;

        private TimeSpan TransitionInterval;

        private WebDriverWait Wait;
        private String CreatedAt;
        private String TableName;
        private String ShopName;
        private AmazonDynamoDBClient DynamoDB;
        public ScrapingDLSite(AmazonDynamoDBClient connection)
        {
            this.Driver = new ChromeDriver();
            this.CreatedAt = DateTime.Now.ToString("yyyy-MM-dd");
            this.TransitionInterval = TimeSpan.FromSeconds(5);
            this.TableName = "ArtCollection";
            this.ShopName = "DLSite";
            this.DynamoDB = connection;
            this.Wait = new WebDriverWait(Driver, TimeSpan.FromSeconds(60));
        }
        ~ScrapingDLSite(){
            destructor();
        }
        public void destructor()
        {
            Driver.Close();
            Driver.Quit();
        }
        public void StartScraping()
        {
            Driver.Navigate().GoToUrl("https://www.dlsite.com/index.html");
            // Driver.WindowHandles.
            // self.driver.set_window_size(1440, 797)

            Wait.Until(driver =>driver.FindElement(By.XPath("(//a[contains(text(),\'同人\')])[2]")).Enabled);
            Driver.FindElement(By.XPath("(//a[contains(text(),\'同人\')])[2]")).Click();

            try
            {
                Wait.Until(driver =>driver.FindElement(By.CssSelector("li.btn_yes.btn-approval > a")).Enabled);
                Driver.FindElement(By.CssSelector("li.btn_yes.btn-approval > a")).Click();
            }
            catch(Exception ex)
            {
                Console.WriteLine(ex.Message);
            }

            // WebDriverWait(self.driver, 60).until(expected_conditions.element_to_be_clickable((By.LINK_TEXT, "ログイン")))
            // self.driver.find_element(By.LINK_TEXT, "ログイン").click()

            // driver.FindElement(By.Name("q")).SendKeys("cheese" + Keys.Enter);
            // wait.Until(driver =>driver.FindElement(By.CssSelector("h3>div")).Displayed);
            // IWebElement firstResult = driver.FindElement(By.CssSelector("h3>div"));
            // Console.WriteLine(firstResult.GetAttribute("textContent"));
            
        }
    }
    class Program
    {
        public static AmazonDynamoDBClient CreateDynamodbConnection()
        {
            AmazonDynamoDBClient connection = null;

            if(String.IsNullOrEmpty(Environment.GetEnvironmentVariable("HOST"))){
                connection = new AmazonDynamoDBClient();
            }else{
                AmazonDynamoDBConfig ddbConfig = new AmazonDynamoDBConfig();
                // ddbConfig.ServiceURL = "http://localhost:8000";
                ddbConfig.ServiceURL = String.Format(
                    "http://{0}:{1}"
                    ,Environment.GetEnvironmentVariable("HOST")
                    ,Environment.GetEnvironmentVariable("PORT")
                    );
                connection = new AmazonDynamoDBClient(ddbConfig);
            }
            return connection;
        }
        static void Main(string[] args)
        {
            // Console.WriteLine("Hello World!");

            // read
            DotEnv.AutoConfig();
            AmazonDynamoDBClient DynamoDB = null;
            try
            {
                DynamoDB = CreateDynamodbConnection();
            }
            catch( Exception ex )
            {
                Console.WriteLine(String.Format("FAILED to create a DynamoDB client; {0}",ex.Message));

                return;
            }

            ScrapingDLSite instance = new ScrapingDLSite(DynamoDB);

            instance.StartScraping();

            instance.destructor();
        }
    }
}
