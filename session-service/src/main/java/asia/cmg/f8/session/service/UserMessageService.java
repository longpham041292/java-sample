package asia.cmg.f8.session.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.session.dto.UserMessageRequest;
import asia.cmg.f8.session.entity.UserMessageEntity;
import asia.cmg.f8.session.repository.UserMessageRepository;

@Service
public class UserMessageService {

	@Autowired
	private UserMessageRepository userMessageRepository;

	public void saveUserMessage(final UserMessageRequest request) {

		UserMessageEntity userMessageEntity = new UserMessageEntity();

		final List<UserMessageEntity> userMessage = userMessageRepository
				.getMessageByLanguageCode(request.getLanguageCode().toLowerCase());

		if (!userMessage.isEmpty()) {
			userMessageEntity = userMessage.get(0);
			userMessageEntity.setMessage(request.getMessage());
		} else {
			userMessageEntity.setLanguageCode(request.getLanguageCode().toLowerCase());
			userMessageEntity.setMessage(request.getMessage());

		}

		userMessageRepository.save(userMessageEntity);

	}

	public List<UserMessageEntity> getUserMessage(final String language){
		
		return userMessageRepository.getMessageByLanguageCode(language.toLowerCase());
	}
	
	
}
