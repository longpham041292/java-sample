package asia.cmg.f8.communication.service;

import asia.cmg.f8.communication.exception.ResourceNotFoundException;
import asia.cmg.f8.communication.model.ChatMessage;
import asia.cmg.f8.communication.model.MessageStatus;
import asia.cmg.f8.communication.repository.ChatMessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatMessageService {

	@Autowired
	private ChatMessageRepository repository;
	
	@Autowired
	private ChatRoomService chatRoomService;
	
	@Autowired
	private MongoOperations mongoOperations;

	public ChatMessage save(ChatMessage chatMessage) {
		chatMessage.setStatus(MessageStatus.RECEIVED);
		repository.save(chatMessage);
		return chatMessage;
	}

	public long countNewMessages(String senderId, String recipientId) {
		return repository.countBySenderIdAndRecipientIdAndStatus(senderId, recipientId, MessageStatus.RECEIVED);
	}

	public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
		String chatId = chatRoomService.getChatId(senderId, recipientId, false);
		
		List<ChatMessage> messages = repository.findByChatId(chatId);

		if (messages.size() > 0) {
			updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
		}

		return messages;
	}

	public ChatMessage findById(String id) {
		ChatMessage chatMessage = repository.findOne(id);
		if (chatMessage != null) {
			chatMessage.setStatus(MessageStatus.DELIVERED);
			return repository.save(chatMessage);
		} else {
			throw new ResourceNotFoundException("can't find message (" + id + ")");
		}
		
	}

	public void updateStatuses(String senderId, String recipientId, MessageStatus status) {
		Query query = new Query(Criteria.where("senderId").is(senderId).and("recipientId").is(recipientId));
		Update update = Update.update("status", status);
		mongoOperations.updateMulti(query, update, ChatMessage.class);
	}
}
