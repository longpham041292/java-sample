/**
 * 
 */
package asia.cmg.f8.session.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.session.client.FeignCommerceClient;
import asia.cmg.f8.session.dto.FreeOrderRequest;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.ContractUploadEntity;
import asia.cmg.f8.session.entity.MemberUploadEntity;
import asia.cmg.f8.session.entity.TrainerUploadEntity;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.ContractUploadRepository;
import asia.cmg.f8.session.repository.MemberUploadRepository;
import asia.cmg.f8.session.repository.TrainerUploadRepository;
import asia.cmg.f8.common.spec.order.ImportUserResult;
/**
 * @author khoa.bui
 *
 */
@Service
public class MigrationUserService {
	private static final String USER_CODE = " userCode: ";
	public static final Logger LOGGER = LoggerFactory.getLogger(MigrationUserService.class);
	private final MemberUploadRepository memUploadRepo;
	private final TrainerUploadRepository trainerUploadRepo;
	private final ContractUploadRepository contractUploadRepo;
	private final static int VALID_USER_CODE = 0;
	private final BasicUserRepository userRepo;
	private final FeignCommerceClient commerceClient;

	@Inject
	public MigrationUserService(final MemberUploadRepository memUploadRepo,
			final TrainerUploadRepository trainerUploadRepo,
			final ContractUploadRepository contractUploadRepo,
			final BasicUserRepository userRepo,
			final FeignCommerceClient commerceClient) {
		this.memUploadRepo = memUploadRepo;
		this.trainerUploadRepo = trainerUploadRepo;
		this.contractUploadRepo = contractUploadRepo;
		this.userRepo = userRepo;
		this.commerceClient = commerceClient; 
	}

	public ImportUserResult validUserCodeInSystem(final String userCode, final String userType) {
		final ImportUserResult result = new ImportUserResult();
		// Check if userName is existed in members_upload_info table
		if(userType.equalsIgnoreCase(UserType.EU.toString())) {
			final List<MemberUploadEntity> userMembersUsage = memUploadRepo.findByMemberBarCodeAndUserCodeChecked(userCode,
					true);
			LOGGER.info("------------------------userMembersUsage() list size: " + userMembersUsage.size() +USER_CODE+userCode);
			if (!CollectionUtils.isEmpty(userMembersUsage)) {
				result.setErrorCode(ErrorCode.DUPLICATE_USER_CODE.getCode());
				return result;
			}
			final List<MemberUploadEntity> userMembersValid = memUploadRepo.findByMemberBarCodeAndUserCodeChecked(userCode,
					false);
			LOGGER.info("------------------------userMembersValid() list size: " + userMembersValid.size()+USER_CODE+userCode);
			if (CollectionUtils.isEmpty(userMembersValid)) {
				//Since import raw data is not performed yet. According to ticket F8-3534, system still allow user to save entered code even if is not existed
				//result.setErrorCode(ErrorCode.INVALID_USER_CODE.getCode());
				result.setErrorCode(VALID_USER_CODE);
				return result;
			}
			result.setErrorCode(VALID_USER_CODE);
			result.setUserCode(userCode);
			if (!CollectionUtils.isEmpty(userMembersValid)) {
				final MemberUploadEntity memberUploadEntity = userMembersValid.get(0);
				result.setName(memberUploadEntity.getFirstName() + " " + memberUploadEntity.getLastName());
				result.setMobile(memberUploadEntity.getMobile());
				result.setClub(memberUploadEntity.getClub());
			}
		}
		else if(userType.equalsIgnoreCase(UserType.PT.toString())) {
			final List<TrainerUploadEntity> trainerMembersUsage = trainerUploadRepo
					.findByTrainerCodeAndUserCodeChecked(userCode, true);
			LOGGER.info("------------------------trainerMembersUsage() list size: " + trainerMembersUsage.size()+USER_CODE+userCode);
			if (!CollectionUtils.isEmpty(trainerMembersUsage)) {
				result.setErrorCode(ErrorCode.DUPLICATE_USER_CODE.getCode());
				return result;
			}
			final List<TrainerUploadEntity> trainerMembersValid = trainerUploadRepo
					.findByTrainerCodeAndUserCodeChecked(userCode, false);
			LOGGER.info("------------------------trainerMembersValid() list size: " + trainerMembersValid.size()+USER_CODE+userCode);
			if (CollectionUtils.isEmpty(trainerMembersValid)) {
				//Since import raw data is not performed yet. According to ticket F8-3534, system still allow user to save entered code even if is not existed
				//result.setErrorCode(ErrorCode.INVALID_USER_CODE.getCode());
				result.setErrorCode(VALID_USER_CODE);
				return result;
			}
			result.setErrorCode(VALID_USER_CODE);
			result.setUserCode(userCode);
			if (!CollectionUtils.isEmpty(trainerMembersValid)) {
				result.setLevel(trainerMembersValid.get(0).getLeepLevel());
				result.setName(trainerMembersValid.get(0).getTrainerName());
				result.setClub(trainerMembersValid.get(0).getClub());
			}
		}
		return result;
	}
	
	public void createFreeOrderRequest(final String userUuid, final String userCode, final String userType) {
		List<ContractUploadEntity> contracts = null;
		if(userType.equalsIgnoreCase(UserType.EU.toString())) {
			contracts = contractUploadRepo.findByMemberBarcodeAndContractImport(userCode, false);
		} else {
			contracts = contractUploadRepo.findByTrainerCodeAndContractImport(userCode, false);
		}
		LOGGER.info("------------------------contracts() contracts size: " + contracts.size());
		final List<FreeOrderRequest> orderRequestList = new ArrayList<FreeOrderRequest>();
		String euUuid = null;
		String ptUuid = null;
		if (!CollectionUtils.isEmpty(contracts)) {
			for (final ContractUploadEntity entity : contracts) {
				if(userType.equalsIgnoreCase(UserType.EU.toString())) {
					euUuid = userUuid;
					final List<BasicUserEntity> ptEntities = userRepo.getUserByUserCode(entity.getTrainerCode());
					ptUuid = (CollectionUtils.isEmpty(ptEntities) ? null : ptEntities.get(0).getUuid());
				}
				else {
					final List<BasicUserEntity> userEntities = userRepo.getUserByUserCode(entity.getMemberBarcode());
					euUuid  = (CollectionUtils.isEmpty(userEntities) ? null : userEntities.get(0).getUuid());
					ptUuid = userUuid;
				}
				LOGGER.info("------------------------getFreeOrderRequest() euUuid: " + euUuid + " ptUuid: "+ ptUuid);
				if (!StringUtils.isEmpty(euUuid) && !StringUtils.isEmpty(ptUuid)) {
					final Calendar calStart = new GregorianCalendar();
					calStart.setTime(new Date());
					calStart.set(Calendar.DAY_OF_YEAR, calStart.get(Calendar.DAY_OF_YEAR) + entity.getTotalRemain());
					calStart.set(Calendar.HOUR_OF_DAY, 23);
					calStart.set(Calendar.MINUTE, 59);
					calStart.set(Calendar.SECOND, 59);
					calStart.set(Calendar.MILLISECOND, 999);
					orderRequestList.add(FreeOrderRequest.builder().euUuid(euUuid).ptUuid(ptUuid)
							.expireDate(String.valueOf(calStart.getTimeInMillis()))
							.numOfSessions(entity.getTotalRemain())
							.ptServiceFree(entity.getPricePerSession() * entity.getTotalRemain())
							.contractNumber(entity.getContractNumber()).build());
				}

			}
			LOGGER.info("------------------------orderRequestList() orderRequestList size: " + orderRequestList.size());
			if(!CollectionUtils.isEmpty(orderRequestList)) {
				commerceClient.createFreeOrderMigrationUsers(orderRequestList);
			}
		}
	}
	
}
