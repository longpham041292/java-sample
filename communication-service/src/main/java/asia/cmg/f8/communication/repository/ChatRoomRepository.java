package asia.cmg.f8.communication.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import asia.cmg.f8.communication.model.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

	Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
