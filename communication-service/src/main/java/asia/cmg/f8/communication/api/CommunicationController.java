package asia.cmg.f8.communication.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import asia.cmg.f8.communication.model.ChatMessage;
import asia.cmg.f8.communication.model.ChatNotification;
import asia.cmg.f8.communication.service.ChatMessageService;
import asia.cmg.f8.communication.service.ChatRoomService;

@Controller
public class CommunicationController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private ChatMessageService chatMessageService;

	@Autowired
	private ChatRoomService chatRoomService;

	@MessageMapping("/chat")
	public void channel(@Payload ChatMessage chatMessage) throws Exception {
		String chatId = chatRoomService.getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
		chatMessage.setChatId(chatId);

		ChatMessage saved = chatMessageService.save(chatMessage);

		simpMessagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/messages",
				new ChatNotification(saved.getId(), saved.getSenderId(), saved.getSenderName()));
	}
}
