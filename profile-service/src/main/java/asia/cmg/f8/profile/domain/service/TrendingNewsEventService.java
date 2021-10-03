package asia.cmg.f8.profile.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import asia.cmg.f8.common.util.F8Application;
import asia.cmg.f8.profile.domain.entity.home.HomeSectionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventActionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventEntity;
import asia.cmg.f8.profile.domain.repository.HomeSectionRepository;
import asia.cmg.f8.profile.domain.repository.TrendingActionRepository;
import asia.cmg.f8.profile.domain.repository.TrendingNewsEventRepository;
import asia.cmg.f8.profile.dto.PageResponse;

@Service
public class TrendingNewsEventService {

	@Autowired
	TrendingNewsEventRepository trendingRepo;
	
	@Autowired
	HomeSectionRepository homeSectionRepo;
	
	@Autowired
	TrendingActionRepository trendingActionRepo;
	
	public TrendingEventEntity getById(final int id) {
		try {
			return trendingRepo.findOne(id);
		} catch (Exception e) {
			return null;
		}
	}
	
	public PageResponse<TrendingEventEntity> searchTrendingEventsByKeyword(LocalDateTime startDate, LocalDateTime endDate, String keyword, boolean activated, final Pageable pageable) throws Exception {
		try {
			Page<TrendingEventEntity> pagedTrendingEntities = null;
			if(!StringUtils.isEmpty(keyword)) {
				pagedTrendingEntities = trendingRepo.searchTrendingByKeyword(startDate, endDate, activated, keyword, pageable);
			} else {
				pagedTrendingEntities = trendingRepo.searchTrending(startDate, endDate, activated, pageable);
			}
			
			PageResponse<TrendingEventEntity> result = new PageResponse<TrendingEventEntity>();
			result.setItems(pagedTrendingEntities.getContent());
			result.setCount(pagedTrendingEntities.getTotalElements());
			result.setPage(pageable.getPageNumber());
			result.setSize(pageable.getPageSize());
			
			return result;
		} catch (Exception e) {
			F8Application.LOGGER.error("[searchTrendingEventsByKeyword][error: {}]", e.getMessage());
			throw e;
		}
	}
	
	public List<TrendingEventEntity> getAllTrendingEvents() {
		try {
			return trendingRepo.findAll();
		} catch (Exception e) {
			F8Application.LOGGER.error("[getBySectionId][error: {}]", e.getMessage());
			return Collections.emptyList();
		}
	}
	
	public List<TrendingEventEntity> getBySectionId(final int sectionId, final String language, int limit) {
		try {
			return trendingRepo.findTrendingBySectionId(sectionId, language, limit);
		} catch (Exception e) {
			F8Application.LOGGER.error("[getBySectionId][error: {}]", e.getMessage());
			return Collections.emptyList();
		}
	}
	
	public PageResponse<TrendingEventEntity> getBySectionId(final int sectionId, final String language, final Pageable pageable) {
		try {
			Page<TrendingEventEntity> pagedList = trendingRepo.findTrendingBySectionId(sectionId, language, pageable);
			
			PageResponse<TrendingEventEntity> result = new PageResponse<TrendingEventEntity>();
			result.setItems(pagedList.getContent());
			result.setCount(pagedList.getTotalElements());
			result.setPage(pageable.getPageNumber());
			result.setSize(pageable.getPageSize());
			return result;
		} catch (Exception e) {
			F8Application.LOGGER.error("[getBySectionId][error: {}]", e.getMessage());
			throw e;
		}
	}
	
	public List<HomeSectionEntity> getAllSections() {
		try {
			return homeSectionRepo.findAllSections();
		} catch (Exception e) {
			F8Application.LOGGER.error("[getAllSections][error: {}]", e.getMessage());
			throw e;
		}
	}
	
	public List<HomeSectionEntity> getAllActiveSections() {
		try {
			return homeSectionRepo.findAllActivatedSections();
		} catch (Exception e) {
			F8Application.LOGGER.error("[getAllSections][error: {}]", e.getMessage());
			throw e;
		}
	}
	
	public HomeSectionEntity getHomeSectionById(final int sectionId) {
		try {
			return homeSectionRepo.findOne(sectionId);
		} catch (Exception e) {
			return null;
		}
	}
	
	public TrendingEventEntity getTrendingById(final long id) {
		try {
			return trendingRepo.findById(id).get();
		} catch (Exception e) {
			F8Application.LOGGER.error("[getTrendingById][error: {}]", e.getMessage());
			throw e;
		}
	}
	 
	public TrendingEventEntity saveTrending(TrendingEventEntity object) {
		try {
			return trendingRepo.saveAndFlush(object);
		} catch (Exception e) {
			F8Application.LOGGER.error("[saveTrending][error: {}]", e.getMessage());
			throw e;
		}
	}
	
	public TrendingEventActionEntity getTrendingActionById(final long actionId) {
		try {
			return trendingActionRepo.findOne(actionId);
		} catch (Exception e) {
			return null;
		}
	}
}