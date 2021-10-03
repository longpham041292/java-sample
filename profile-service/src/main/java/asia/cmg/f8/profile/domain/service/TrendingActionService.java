package asia.cmg.f8.profile.domain.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.profile.domain.entity.home.TrendingEventActionEntity;
import asia.cmg.f8.profile.domain.repository.TrendingActionRepository;

@Service
public class TrendingActionService {

	@Autowired
	TrendingActionRepository trendingActionRepo;
	
	public TrendingEventActionEntity getById(final Long id) {
		try {
			return trendingActionRepo.findOne(id);
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<TrendingEventActionEntity> getAll() {
		try {
			return trendingActionRepo.findAll();
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}
