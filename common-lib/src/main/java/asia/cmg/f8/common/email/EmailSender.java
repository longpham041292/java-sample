package asia.cmg.f8.common.email;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import static asia.cmg.f8.common.email.EmailConstants.*;

public class EmailSender {
	
	private static final Logger LOG = LoggerFactory.getLogger(EmailSender.class);

	public Boolean sendEmail(String sender, String recipients, String subject, String body) {
		try {
			HttpHeaders headers = new HttpHeaders();
			String encodeAuth = MAILGUN_USER + ":" + MAILGUN_SECRET_KEY;
			String authHeader = "Basic " + Base64.getEncoder().encodeToString(encodeAuth.getBytes());
			headers.add("Authorization", authHeader);
			headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());

			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
			requestBody.add("to", recipients);
			requestBody.add("from", sender);
			requestBody.add("subject", subject);
			requestBody.add("html", body);

			HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);

			ResponseEntity<?> responseMail = new RestTemplate().exchange(MAILGUN_URL, HttpMethod.POST, formEntity,
					String.class);
		} catch (Exception e) {
			LOG.error("Can not sending email to {}. Message: {}", recipients, e.getMessage());
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
