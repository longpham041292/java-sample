package asia.cmg.f8.communication.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import asia.cmg.f8.communication.model.ChatMessage;
import asia.cmg.f8.communication.model.MessageStatus;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

	long countBySenderIdAndRecipientIdAndStatus(String senderId, String recipientId, MessageStatus status);

	List<ChatMessage> findByChatId(String chatId);
}
