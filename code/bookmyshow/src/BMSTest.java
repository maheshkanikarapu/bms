import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BMSTest {
	WebDriver driver = null;
	List<String> status = new ArrayList<String>();
	String mailBody = "";
	
	@Test
	public void test(String filePath) {
		ReadData data = new ReadData(filePath);
		String url = data.getUrl();
		List<String> myTheatreList = data.getMyTheatres();
		String driverToLaunch = data.getDriver();
		if(driverToLaunch.equalsIgnoreCase("CHROME")) {
			System.out.println("CHROME");
			System.setProperty("webdriver.chrome.driver", "driver\\chromedriver.exe");
			driver = new ChromeDriver();
		} else if(driverToLaunch.equalsIgnoreCase("IE")) {
			System.out.println("IE");
			System.setProperty("webdriver.ie.driver", "driver\\IEDriverServer.exe");
			driver = new InternetExplorerDriver();
		} else {
			System.out.println("FIREFOX");
			System.setProperty("webdriver.gecko.driver", "driver\\geckodriver.exe");
			driver = new FirefoxDriver();
		}
		implicitWait();
		driver.manage().window().maximize();
		driver.get(url);
		pageLoad();
		driver.findElement(By.xpath("//li[@class='region-list']/a[contains(@onclick,'Hyderabad')]")).click();
		pageLoad();
		String availableShows = "//ul[@id='venuelist']//div[contains(@class,'body')]//div[contains(@class,'available') or @data-online='Y']/a";
		List<WebElement> availableShowsLs = driver.findElements(By.xpath(availableShows));
		System.out.println(availableShowsLs.size());
		for ( WebElement availableSlot  : availableShowsLs) {
			if(availableSlot==null) {
				continue;
			}
			String currentThreatre = availableSlot.findElement(By.xpath("ancestor::li//*[@class='__venue-name']")).getText();
			System.out.println(currentThreatre);
			if(!myTheatreList.contains(currentThreatre)) {
				continue;
			}
			availableSlot.click();
			pageLoad();
			
			//Accept
			boolean isAcceptDisplayed = false;
			WebElement acceptObj = driver.findElement(By.id("btnPopupAccept"));
			if(isDisplayed(acceptObj)) {
				System.out.println("Accept "+acceptObj.getCssValue("display"));
				isAcceptDisplayed = true;
				acceptObj.click();
				pageLoad();
			}
			
			//Once Accepted, verify no seats
			boolean isSeatAvailable = true;
			if(isAcceptDisplayed) {
				WebElement noSeatsObj = driver.findElement(By.id("tnc"));
				if(isDisplayed(noSeatsObj)) {
					System.out.println("No Seats "+noSeatsObj.getCssValue("display"));
					isSeatAvailable = false;
					driver.findElement(By.xpath("//*[@id='tnc']//*[@class='__dismiss']")).click();
					pageLoad();
					continue;
				}
			}
			
			//Once Accepted, verify error
			boolean isErrorPresent = false;
			if(isAcceptDisplayed) {
				WebElement errorObj = driver.findElement(By.id("error-div"));
				if(isDisplayed(errorObj)) {
					System.out.println("Error "+errorObj.getCssValue("display"));
					isErrorPresent = true;
					driver.findElement(By.xpath("//*[@id='error-div']//*[@class='__dismiss']")).click();
					pageLoad();
					continue;
				}
			}

			if(isAcceptDisplayed && isSeatAvailable && !isErrorPresent) {
				WebElement selectSeatsObj = driver.findElement(By.id("qty-sel"));
				WebElement screenOverlayObj = driver.findElement(By.xpath("//*[@class='modal']//*[@class='__overlay']"));
				if(isDisplayed(selectSeatsObj)) {
					System.out.println("Select Seats "+selectSeatsObj.getCssValue("display"));
					driver.findElement(By.id("proceed-Qty")).click();
					pageLoad();
				} else if(isDisplayed2(screenOverlayObj)) {
					driver.navigate().back();
					System.out.println("refresh");
					continue;
				}

				int seats = driver.findElements(By.xpath("//div[@class='seatI']/a[@class='_available']")).size();
				if(seats>0) {
					String theatre = driver.findElement(By.id("strVenName")).getText();
					String time = driver.findElement(By.id("strDate")).getText();
					status.add(theatre+" - "+time+" - "+seats);
					mailBody = mailBody+"<tr><td>"+theatre+"</td><td>"+time+"</td><td>"+seats+"</td></tr>";
					System.out.println(theatre+" - "+time+" - "+seats);
				}
			
				WebElement backObj = driver.findElement(By.id("disback"));
				if(isDisplayed(backObj)) {
					System.out.println("Back "+backObj.getCssValue("display"));
					backObj.click();
					pageLoad();
				}
			}
		}
		
		System.out.println(mailBody);
		try {
			String format1 = "<table style=\"width:100%\" border=\"1\"><tr><th>Theatre</th><th>Show Time</th><th>Available Seats</th></tr>";
			String format2 = "</table>";
			String footer = "<br><sub>--auto generated mail--</sub>";
			MailAPI.mail(data, format1+mailBody+format2+footer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		driver.close();
	}

	private boolean isDisplayed(WebElement obj) {
		return obj!=null && obj.isDisplayed();
	}
	
	private boolean isDisplayed2(WebElement obj) {
		constWait(10);
		return obj!=null && obj.isDisplayed();
	}
	
	private void constWait(int secs) {
		try {
			Thread.sleep(secs*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * private void pageLoad2() { new WebDriverWait(driver, 30).until( driver ->
	 * ((JavascriptExecutor)
	 * driver).executeScript("return document.readyState").equals("complete") ); }
	 */
	
	private void pageLoad() {
		constWait(1);
		new WebDriverWait(driver, 30).until(ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";"));
	}
	
	private void implicitWait() {
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}
}
