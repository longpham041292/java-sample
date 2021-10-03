package asia.cmg.f8.commerce.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import asia.cmg.f8.commerce.dto.InstrumentInfoDto;
import asia.cmg.f8.commerce.entity.OnepayInstrumentEntity;
import asia.cmg.f8.commerce.repository.OnepayInstrumentRepository;

@Service
public class OnepayInstrumentService {

	@Inject
	private OnepayInstrumentRepository onepayInstrumentRepository;

	public Optional<OnepayInstrumentEntity> findOneByMerchantTxnRef(final String merchantTxnRef) {
		return onepayInstrumentRepository.findOneByMerchantTxnRef(merchantTxnRef);
	}

	public List<InstrumentInfoDto> findAllInstruments(final String userUuid, final String pspId) {
		final List<OnepayInstrumentEntity> result = onepayInstrumentRepository.findInstrumentByUserIdAndPspId(userUuid, pspId);
		return result.stream().map(x -> buildInstrumentInfo(x)).collect(Collectors.toList());
	}
	
	public List<InstrumentInfoDto> findAllInstruments(final String userUuid) {
		final List<OnepayInstrumentEntity> result = onepayInstrumentRepository.findInstrumentByUserId(userUuid);
		return result.stream().map(x -> buildInstrumentInfo(x)).collect(Collectors.toList());
	}

	public void save(OnepayInstrumentEntity entity){
		onepayInstrumentRepository.save(entity);
	}
	
	public Optional<OnepayInstrumentEntity> findOneById(final Long id) {
		return onepayInstrumentRepository.findOneById(id);
	}
	
	public void deleteInstrument(final OnepayInstrumentEntity instrument) {
		onepayInstrumentRepository.delete(instrument);
	}
	
	
	private InstrumentInfoDto buildInstrumentInfo(final OnepayInstrumentEntity instrumentEntity) {
		return new InstrumentInfoDto(instrumentEntity.getId(), instrumentEntity.getBrandName(), instrumentEntity.getNumber(),
				instrumentEntity.getPspId());
	}

}
