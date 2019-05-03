import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
public class ReadData {

	private String url;
	private List<String> myTheatres = new ArrayList<String>();
	private String fromUser;
	private String password;
	private String receipients;
	private String subject;
	private String driver;
	private List<String> showTime = new ArrayList<String>();
	private List<String> row = new ArrayList<String>();
	private int minTickets;
	
	public ReadData(String filePath) {
		Properties prop = new Properties();
		FileInputStream input = null;
		try {
			input = new FileInputStream(filePath);
			prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		url = prop.getProperty("URL");
		myTheatres = Arrays.asList(prop.getProperty("THEATRES").split(";"));
		fromUser = prop.getProperty("FROMUSER");
		password = prop.getProperty("PASSWORD");
		receipients = prop.getProperty("RECEIPIENTS");
		subject = prop.getProperty("SUBJECT");
		driver = prop.getProperty("DRIVER");
		showTime = Arrays.asList(prop.getProperty("SHOWTIME").split(";"));
		row = Arrays.asList(prop.getProperty("ROW").split(";"));
		minTickets = Integer.parseInt(prop.getProperty("MINTICKETS"));
	}
	
	public List<String> getShowTime() {
		return showTime;
	}

	public List<String> getRow() {
		return row;
	}

	public int getMinTickets() {
		return minTickets;
	}

	public String getSubject() {
		return subject;
	}
	
	public String getDriver() {
		return driver;
	}

	public String[] getReceipients() {
		return receipients.split(";");
	}

	public String getFromUser() {
		return fromUser;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public List<String> getMyTheatres() {
		return myTheatres;
	}

}