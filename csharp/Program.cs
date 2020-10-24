using System;
using System.Threading;
using System.IO;
using System.Linq;
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
            this.Wait = new WebDriverWait(Driver, TimeSpan.FromSeconds(60));
            this.TableName = "ArtCollection";
            this.ShopName = "DLSite";
            this.DynamoDB = connection;
            
        }
        ~ScrapingDLSite()
        {
            destructor();
        }
        public void destructor()
        {
            Driver.Close();
            Driver.Quit();
        }
        public void StartScraping()
        {
            
            String apppath;
            try
            {
                apppath = (String)System.AppDomain.CurrentDomain.BaseDirectory;
            }
            catch(Exception ex)
            {
                Console.WriteLine(ex.Message);
                return;
            }

            Driver.Navigate().GoToUrl("https://www.dlsite.com/index.html");
            // Driver.WindowHandles.
            // self.driver.set_window_size(1440, 797)

            Wait.Until(driver =>driver.FindElement(By.XPath("(//a[contains(text(),\'同人\')])[2]")).Enabled);
            Driver.FindElement(By.XPath("(//a[contains(text(),\'同人\')])[2]")).Click();

            // age validation
            // this popup is shown when you visit first time.
            try
            {
                Wait.Until(driver =>driver.FindElement(By.CssSelector("li.btn_yes.btn-approval > a")).Enabled);
                Driver.FindElement(By.CssSelector("li.btn_yes.btn-approval > a")).Click();
            }
            catch(Exception ex)
            {
                Console.WriteLine(ex.Message);
            }

            Thread.Sleep(TransitionInterval);

            // login user
            Wait.Until(driver =>driver.FindElement(By.LinkText("ログイン")).Enabled);
            Driver.FindElement(By.LinkText("ログイン")).Click();

            Wait.Until(driver =>driver.FindElement(By.Id("form_id")).Enabled);
            Driver.FindElement(By.Id("form_id")).Click();
            Driver.FindElement(By.Id("form_id")).SendKeys(Environment.GetEnvironmentVariable("DLSITE_ID"));

            Wait.Until(driver =>driver.FindElement(By.Id("form_password")).Enabled);
            Driver.FindElement(By.Id("form_password")).Click();
            Driver.FindElement(By.Id("form_password")).SendKeys(Environment.GetEnvironmentVariable("DLSITE_PASSWORD"));

            Wait.Until(driver =>driver.FindElement(By.CssSelector(".type-clrDefault")).Enabled);
            Driver.FindElement(By.CssSelector(".type-clrDefault")).Click();

            // close modal window for qupon. qupon is shown when user is just login.
            // this popup dont know to show
            // you dont know this popup show or not.
            // このポップアップが表示されるかどうか分からない。
            try
            {
                Wait.Until(driver =>driver.FindElement(By.CssSelector("div > div.modal_close")).Enabled);
                Driver.FindElement(By.CssSelector("div > div.modal_close")).Click();
            }
            catch(Exception ex)
            {
                Console.WriteLine(ex.Message);
            }

            Thread.Sleep(TransitionInterval);


            // read ArtName list file
            //string[] lines = System.IO.File.ReadAllLines(Path.Combine(apppath,"ArtName.txt"));

            foreach (string line in System.IO.File.ReadAllLines(Path.Combine(apppath,"ArtName.txt")))
            {
                // Begining of line [#], this line is ignore.
                if(line.StartsWith("#") || String.IsNullOrEmpty(line))
                {
                    continue;
                }

                var ArtName = line;

                // search for keyword using exact match. and go to page search result.
                Wait.Until(driver =>driver.FindElement(By.Id("search_text")).Enabled);
                // clear the input box for 
                Driver.FindElement(By.Id("search_text")).Clear();
                Driver.FindElement(By.Id("search_text")).Click();
                // self.driver.find_element(By.ID, "search_text").send_keys("\"{}\"".format(ArtName))
                Driver.FindElement(By.Id("search_text")).SendKeys(ArtName);
                Driver.FindElement(By.Id("search_text")).SendKeys(Keys.Enter);


                try
                {
                    Wait.Until(driver =>driver.FindElement(By.CssSelector(String.Format(".search_result_img_box_inner a[title='{}']",ArtName))).Enabled);
                    Driver.FindElement(By.CssSelector(String.Format(".search_result_img_box_inner a[title='{}']",ArtName))).Click();
                }
                catch(Exception ex)
                {
                    continue;
                }

                Thread.Sleep(TransitionInterval);

                // url からArtIdを取得。
                // ex) https://www.dlsite.com/pro/work/=/product_id/VJ009935.html -> VJ000935
                var ShopArtId = (Driver.Url.Split('/',1).Last()).Split('.')[0];
        
            }
            // driver.FindElement(By.Name("q")).SendKeys("cheese" + Keys.Enter);
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
