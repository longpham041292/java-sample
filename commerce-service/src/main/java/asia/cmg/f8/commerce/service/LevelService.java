package asia.cmg.f8.commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.commerce.dto.LevelDTO;
import asia.cmg.f8.commerce.entity.LevelEntity;
import asia.cmg.f8.commerce.repository.LevelEntityRepository;

@Service
public class LevelService {
	
	@Autowired
	LevelEntityRepository levelRepo;
	public List<LevelEntity> getListLevelEntity() {
		try {
			return levelRepo.findAll();
		} catch (Exception e) {
			return null;
		}
	}
	public LevelEntity updateLevelEntity(LevelDTO levelDataRequest) {
		Optional<LevelEntity> levelEntity = levelRepo.findByCode(levelDataRequest.getCode());
		if(levelEntity.isPresent()) {
			LevelEntity level = levelEntity.get();
			level.setPtCommission(levelDataRequest.getPtCommission()); 
			level.setPtBurnedCommission(levelDataRequest.getPtBurnedCommission()); 
			level.setPtBookingCredit(levelDataRequest.getPtBookingCredit());
			levelRepo.save(level);
			return level;
		}
		return null;
	} 
}
