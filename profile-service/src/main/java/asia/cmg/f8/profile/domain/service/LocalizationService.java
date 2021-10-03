package asia.cmg.f8.profile.domain.service;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import asia.cmg.f8.profile.domain.entity.LocalizationEntity;
import asia.cmg.f8.profile.domain.repository.LocalizationRepository;

@Service
public class LocalizationService {

	@Autowired
	private LocalizationRepository localizationRepo;

	public List<LocalizationEntity> getAllByCategoryAndLanguage(String category, final String language) {
		try {
			return localizationRepo.getByCategoryAndLanguage(category, language);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	public List<LocalizationEntity> searchByCategoryAndLanguage(String keyword, String category, final String language) {
		try {
			return localizationRepo.searchByCategoryAndLanguage(category, language, keyword);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}
