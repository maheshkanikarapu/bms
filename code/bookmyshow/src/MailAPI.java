import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailAPI {
	static Properties emailProperties;
	static Session mailSession;
	static MimeMessage emailMessage;
	
	public static void mail(ReadData data, String body) throws Exception {
		String emailPort = "587";//gmail's smtp port
		emailProperties = System.getProperties();
		emailProperties.put("mail.smtp.port", emailPort);
		emailProperties.put("mail.smtp.auth", "true");
		emailProperties.put("mail.smtp.starttls.enable", "true");
		String[] toEmails = data.getReceipients();
		mailSession = Session.getDefaultInstance(emailProperties, null);
		emailMessage = new MimeMessage(mailSession);
		for (int i = 0; i < toEmails.length; i++) {
			emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails[i]));
		}
		emailMessage.setSubject(data.getSubject());
		emailMessage.setContent(body, "text/html");//for a html email
		String emailHost = "smtp.gmail.com";
		String fromUser = data.getFromUser();//just the id alone without @gmail.com
		String fromUserEmailPassword = data.getPassword();
		Transport transport = mailSession.getTransport("smtp");
		transport.connect(emailHost, fromUser, fromUserEmailPassword);
		transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
		transport.close();
		System.out.println("Email sent successfully.");
	}
}