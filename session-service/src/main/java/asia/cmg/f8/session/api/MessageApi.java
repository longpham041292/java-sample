package asia.cmg.f8.session.api;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.UserMessageRequest;
import asia.cmg.f8.session.service.UserMessageService;

@RestController
public class MessageApi {

	 private static final Logger LOG = LoggerFactory.getLogger(MessageApi.class);
	 
	 
	 @Autowired 
	 private UserMessageService userMessageService;

	@RequestMapping(value = "/admin/userMessage/save", method = RequestMethod.PUT)
	@ResponseBody
	@RequiredAdminRole
	public ResponseEntity<?> massageSave(@RequestBody final UserMessageRequest request) throws IOException {

		if (StringUtils.isEmpty(request.getLanguageCode()) || StringUtils.isEmpty(request.getMessage())) {
			return new ResponseEntity<>(ErrorCode.REQUEST_INVALID, HttpStatus.BAD_REQUEST);
		}

		LOG.info("---- message Save -------");
		LOG.info("------ {} ", request.getLanguageCode());
		LOG.info("----- {} ", request.getMessage());
		userMessageService.saveUserMessage(request);

		return new ResponseEntity<>(Collections.singletonMap("Update", true), HttpStatus.OK);

	}

	@RequestMapping(value = "/admin/userMessage/view/{language}", method = RequestMethod.GET)
	@ResponseBody
	@RequiredAdminRole
	public ResponseEntity<?> massageView(@PathVariable("language") final String language) throws IOException {

		LOG.info("----- << ***  >> ---- {}", language);

		return new ResponseEntity<>(userMessageService.getUserMessage(language), HttpStatus.OK);

	}

}
