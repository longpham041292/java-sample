package asia.cmg.f8.communication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.communication.model.ChatRoom;
import asia.cmg.f8.communication.repository.ChatRoomRepository;

@Service
public class ChatRoomService {

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	public String getChatId(String senderId, String recipientId, boolean createIfNotExist) {

		Optional<String> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId).map(ChatRoom::getChatId);
		
		if (!chatRoom.isPresent()) {
			if (!createIfNotExist) {
				return null;
			} else {
				String chatId = String.format("%s_%s", senderId, recipientId);
				ChatRoom senderRecipient = new ChatRoom
                        .Builder()
                        .chatId(chatId)
                        .senderId(senderId)
                        .recipientId(recipientId)
                        .build();
				ChatRoom recipientSender = new ChatRoom
                        .Builder()
                        .chatId(chatId)
                        .senderId(recipientId)
                        .recipientId(senderId)
                        .build();
				chatRoomRepository.save(senderRecipient);
                chatRoomRepository.save(recipientSender);

                return chatId;
			}
		}
		return chatRoom.get();
	}
}
