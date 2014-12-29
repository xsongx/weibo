package cn.edu.nudt.nlp.crawler;
import java.util.Arrays;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
public class Seleniumtest{
 public static void main(String[] args){
  // 配置服务器
  System.setProperty("webdriver.chrome.driver","/media/work/gitbase/chromedriver");
  // 创建一个WebDriver实例
  DesiredCapabilities capabilities =DesiredCapabilities.chrome();
  capabilities.setCapability("chrome.switches",
    Arrays.asList("--start-maximized"));
  WebDriver driver =new ChromeDriver(capabilities);
  // 访问google
  driver.get("http://www.google.com.hk");
  // 另一种访问方法
  //driver.navigate().to("http://www.google.com");
  // 找到文本框
  WebElement element = driver.findElement(By.name("q"));
  // 输入搜索关键字
  element.sendKeys("Selenium");
  // 提交表单 WebDriver会自动从表单中查找提交按钮并提交
  element.submit();
  // 检查页面title
  System.out.println("Page title is:"+ driver.getTitle());
  // google查询结果是通过javascript动态呈现的.
  // 设置页面等待10秒超时
  (new WebDriverWait(driver,10)).until(new ExpectedCondition<Boolean>(){
   public Boolean apply(WebDriver d){
    return d.getTitle().toLowerCase().startsWith("selenium");
   }
  });
  // 显示查询结果title
  System.out.println("Page title is:"+ driver.getTitle());
  // 关闭浏览器
  driver.quit();
 }
}
