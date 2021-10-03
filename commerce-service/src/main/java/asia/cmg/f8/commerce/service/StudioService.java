package asia.cmg.f8.commerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.commerce.dto.StudioDto;
import asia.cmg.f8.commerce.repository.StudioRepository;

@Service
public class StudioService {
	
	@Autowired
	private StudioRepository repository;
	
	private static final Logger LOG = LoggerFactory.getLogger(StudioService.class);
	
	public StudioDto getStudioByUuid(String studio_uuid) {
			
			try {
				return repository.getStudioByUuid(studio_uuid);
				
			} catch (Exception e) {
				LOG.error(e.getMessage());
				return null;
			}
	}
}