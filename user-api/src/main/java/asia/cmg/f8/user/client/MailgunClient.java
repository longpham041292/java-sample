package asia.cmg.f8.user.client;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import feign.Headers;

@FeignClient(value = "mailgun", url = "https://api.mailgun.net" )
public interface MailgunClient {
	@RequestMapping(value = "/v3/mg.leep.app/messages", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	@Headers("Content-Type: application/x-www-form-urlencoded")
	Map<String, Object> sendEmailByMailgun(@RequestHeader("Authorization") String authHeader, 
								@RequestParam("to") String toEmail,
								@RequestParam("from") String fromEmail,
								@RequestParam("subject") String subject,
								@RequestParam("html") String html
								
	);
	
}