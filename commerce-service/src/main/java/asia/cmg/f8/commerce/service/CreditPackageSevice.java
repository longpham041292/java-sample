package asia.cmg.f8.commerce.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.commerce.dto.CreditPackageDTO;
import asia.cmg.f8.commerce.entity.credit.CreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageType;
import asia.cmg.f8.commerce.repository.CreditPackageRepository;

@Service
public class CreditPackageSevice {

	@Autowired
	private CreditPackageRepository creditPackageRepo;
	
	public Optional<CreditPackageEntity> getUnitPackage() {
		return creditPackageRepo.getUnitPackage(CreditPackageType.UNIT.ordinal());
	}
	
	public CreditPackageEntity getCreditPackageById(final long id) throws Exception {
		try {
			return creditPackageRepo.findOne(id);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<CreditPackageEntity> getActiveCreditPackages() {
		try {
			return creditPackageRepo.getActiveCreditPackages();
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	public CreditPackageEntity createCreditPackage(CreditPackageDTO requestDTO) throws Exception {
		try {
			CreditPackageEntity entity = new CreditPackageEntity();
			entity.setActive(requestDTO.getActive());
			entity.setCountry(requestDTO.getCountry());
			entity.setCredit(requestDTO.getCredit());
			entity.setBonusCredit(requestDTO.getBonusCredit());
			entity.setTotalCredit(requestDTO.getTotalCredit());
			entity.setCurrency(requestDTO.getCurrency());
			entity.setDescription(requestDTO.getDescription());
			entity.setPrice(requestDTO.getPrice());
			entity.setTotalPrice(requestDTO.getTotalPrice());
			entity.setTitle(requestDTO.getTitle());
			entity.setCreditType(requestDTO.getType());
			
			return creditPackageRepo.saveAndFlush(entity);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public CreditPackageEntity updateCreditPackage(final long id, CreditPackageDTO requestDTO) throws Exception {
		try {
			CreditPackageEntity entity = creditPackageRepo.findOne(id);
			if(entity == null) {
				throw new Exception(String.format("Credit package id %s does not existed", id));
			}
			entity.setActive(requestDTO.getActive());
			entity.setDescription(requestDTO.getDescription());
			entity.setTitle(requestDTO.getTitle());
			
			return creditPackageRepo.saveAndFlush(entity);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public CreditPackageDTO getCreditPackageByType(CreditPackageType type) {
		try {
			return creditPackageRepo.getCreditPackageByType(type);
		} catch (Exception e) {
			throw e;
		}
	}
}
