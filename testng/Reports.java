package com.hc1.testautomation.lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.mail.EmailException;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;
import com.sun.mail.smtp.SMTPTransport;


public class Reports extends org.testng.reporters.EmailableReporter{
	@Override
	public void generateReport(List<XmlSuite> xml, List<ISuite> suites, String outdir) {
	    super.generateReport(xml, suites, outdir);
	    try {
			emailResults("/AutomatedTestsRunReport/SmokeTestReport.pdf");
		} catch (EmailException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public boolean emailResults(String fileAttachment)
		throws EmailException, MessagingException {
		
		Properties props = System.getProperties();
		props.put("mail.smtps.host", "smtp.gmail.com");
		props.put("mail.smtps.auth", "true");
		
		String recipients = TestFunctionsLib.readPropertiesFile("/testrun.properties","test.emailto");
		String sender = TestFunctionsLib.readPropertiesFile("/testrun.properties","test.emailuser");
		String accountEmail = TestFunctionsLib.readPropertiesFile("/testrun.properties","test.emailuser");
		String acctPwd = TestFunctionsLib.readPropertiesFile("/testrun.properties","test.emailpwd");
		
		
		Session session = Session.getInstance(props, null);
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(sender));
		;
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(recipients, false));
		msg.setSubject("Test Email (Sun SMPT)");
		
		// create the message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		// fill message
		messageBodyPart.setText("Test mail one");
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		// Part two is attachment
		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(fileAttachment);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(fileAttachment);
		multipart.addBodyPart(messageBodyPart);
		// Put parts in message
		msg.setContent(multipart);
		
		msg.setHeader("X-Mailer", "Test Results");
		SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
		t.connect("smtp.gmail.com", accountEmail, acctPwd);
		t.sendMessage(msg, msg.getAllRecipients());
		t.close();
		return true;
		}
	
	public static String readPropertiesFile(String propFile, String property) {

		String value = null;

		Properties prop = new Properties();

		try {
			// load a properties file
			prop.load(new FileInputStream(propFile));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		value = prop.getProperty(property);

		return value;
	}
	
}
