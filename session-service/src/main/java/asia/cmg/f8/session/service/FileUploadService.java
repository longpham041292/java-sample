package asia.cmg.f8.session.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.session.dto.UploadMembersInfo;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.ContractUploadEntity;
import asia.cmg.f8.session.entity.MemberUploadEntity;
import asia.cmg.f8.session.entity.TrainerUploadEntity;
import asia.cmg.f8.session.repository.BasicUserRepository;
//import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.ContractUploadRepository;
import asia.cmg.f8.session.repository.MemberUploadRepository;
import asia.cmg.f8.session.repository.TrainerUploadRepository;



@Service
public class FileUploadService {

	private static final String STRING = " , ";

	private static final Logger LOG = LoggerFactory.getLogger(FileUploadService.class);

	@Autowired
	private MemberUploadRepository memberUploadRepository;

	@Autowired
	private TrainerUploadRepository trainerUploadRepository;

	@Autowired
	private ContractUploadRepository contractUploadRepository;

	private final MigrationUserService migrationUserService;

	private final BasicUserRepository userRepo;

	@Inject
	public FileUploadService(final MigrationUserService migrationUserService, final BasicUserRepository userRepo) {

		this.migrationUserService = migrationUserService;
		this.userRepo = userRepo;
	}
	
	public String uploadData(final MultipartFile upfile) throws IOException {

		final InputStream streamUpfile = upfile.getInputStream();

		final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		final DecimalFormat integerFormat = new DecimalFormat("############");
        final DecimalFormat floatingFormat = new DecimalFormat("#########0.0000");

		final Workbook workbook = new XSSFWorkbook(streamUpfile);
		final Sheet firstSheet = workbook.getSheetAt(0);

		final int totalRows = firstSheet.getPhysicalNumberOfRows();
		final Map<String, Integer> map = new HashMap<String, Integer>(); 
		final Row row = firstSheet.getRow(0);

		final short minColIx = row.getFirstCellNum();
		final short maxColIx = row.getLastCellNum();

		for (short colIx = minColIx; colIx < maxColIx; colIx++) {
			final Cell cell = row.getCell(colIx);
			map.put(cell.getStringCellValue(), cell.getColumnIndex());
		}

		final List<UploadMembersInfo> listOfDataMembersInfo = new ArrayList<UploadMembersInfo>();
		for (int x = 1; x <= totalRows; x++) {

			final UploadMembersInfo uploadMembersInfo = new UploadMembersInfo();
			final Row dataRow = firstSheet.getRow(x);

			final int club = map.get("CLUB");
			final int memberBarcode = map.get("MEMBER_BARCODE");
			final int firstName = map.get("FIRST_NAME");
			final int lastName = map.get("LAST_NAME");
			final int mobile = map.get("MOBILE");
			final int createDate = map.get("CREATE_DATE");
			final int startDate = map.get("START_DATE");
			final int expirationDate = map.get("EXPIRATION_DATE");
			final int contractNumber = map.get("CONTRACT_NUMBER");
			final int type = map.get("TYPE");
			final int itemGroup = map.get("ITEM_GROUP");
			final int status = map.get("STATUS");
			final int pricePerSession = map.get("PricePerSession");
			final int totalAmount = map.get("TOTAL_AMOUNT");
			final int totalSession = map.get("TOTAL_SESSION");
			final int totalUsed = map.get("TOTAL_USED");
			final int totalRemain = map.get("TOTAL_REMAIN");
			final int trainerCode = map.get("TRAINER_CODE");
			final int trainerNickName = map.get("TRAINER_NICKNAME");
			final int trainerName = map.get("TRAINER_NAME");
			final int trainerLevel = map.get("TRAINER_LEVEL");
			final int leepLevel = map.get("LEEP_LEVEL");

			if (dataRow != null) {

				final Cell cellClub = dataRow.getCell(club);
				final Cell cellMemberBarcode = dataRow.getCell(memberBarcode);
				final Cell cellFirstName = dataRow.getCell(firstName);
				final Cell cellLastName = dataRow.getCell(lastName);
				final Cell cellMobile = dataRow.getCell(mobile);
				final Cell cellCreateDate = dataRow.getCell(createDate);
				final Cell cellStartDate = dataRow.getCell(startDate);
				final Cell cellExpirationDate = dataRow.getCell(expirationDate);
				final Cell cellContractNumber = dataRow.getCell(contractNumber);
				final Cell cellType = dataRow.getCell(type);
				final Cell cellItemGroup = dataRow.getCell(itemGroup);
				final Cell cellStatus = dataRow.getCell(status);
				final Cell cellPricePerSession = dataRow.getCell(pricePerSession);
				final Cell cellTotalAmount = dataRow.getCell(totalAmount);
				final Cell cellTotalSession = dataRow.getCell(totalSession);
				final Cell cellTotalUsed = dataRow.getCell(totalUsed);
				final Cell cellTotalRemain = dataRow.getCell(totalRemain);
				final Cell cellTrainerCode = dataRow.getCell(trainerCode);
				final Cell cellTrainerNickName = dataRow.getCell(trainerNickName);
				final Cell cellTrainerName = dataRow.getCell(trainerName);
				final Cell cellTrainerLevel = dataRow.getCell(trainerLevel);
				final Cell cellLeepLevel = dataRow.getCell(leepLevel);

				if (cellClub != null) {
					uploadMembersInfo.setClub(cellClub.getStringCellValue());
				}
				if (cellMemberBarcode != null) {
					uploadMembersInfo.setMemberBarcode(cellMemberBarcode.getStringCellValue());
				}
				if (cellFirstName != null) {
					uploadMembersInfo.setFirstName(cellFirstName.getStringCellValue());
				}
				if (cellLastName != null) {
					uploadMembersInfo.setLastName(cellLastName.getStringCellValue());
				}
				if (cellMobile != null) {
					switch (cellMobile.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						if (cellMobile.getStringCellValue() != null) {
							uploadMembersInfo.setMobile(cellMobile.getStringCellValue());
							break;
						}
					case Cell.CELL_TYPE_NUMERIC:

						uploadMembersInfo.setMobile("0"+integerFormat.format(cellMobile.getNumericCellValue()));
						break;
					    default :
					}
				}

				if (cellCreateDate != null && cellCreateDate.getDateCellValue() != null) {
					uploadMembersInfo.setCreateDate(dateFormat.format(cellCreateDate.getDateCellValue()));
				}

				if (cellStartDate != null && cellStartDate.getDateCellValue() != null) {
					uploadMembersInfo.setStartDate(dateFormat.format(cellStartDate.getDateCellValue()));
				}

				if (cellExpirationDate != null && cellExpirationDate.getDateCellValue() != null) {
					uploadMembersInfo.setExpirationDate(dateFormat.format(cellExpirationDate.getDateCellValue()));
				}

				if (cellContractNumber != null) {
					uploadMembersInfo.setContractNumber(cellContractNumber.getStringCellValue());
				}

				if (cellType != null) {
					uploadMembersInfo.setType(cellType.getStringCellValue());
				}
				if (cellItemGroup != null) {
					uploadMembersInfo.setItemGroup(cellItemGroup.getStringCellValue());
				}

				if (cellStatus != null) {
					uploadMembersInfo.setStatus(cellStatus.getStringCellValue());
				}

				if (cellPricePerSession != null) {
					uploadMembersInfo.setPricePerSession(floatingFormat.format(cellPricePerSession.getNumericCellValue()).toString());
				}

				if (cellTotalAmount != null) {
					uploadMembersInfo.setTotalAmount(floatingFormat.format(cellTotalAmount.getNumericCellValue()).toString());
				}

				if (cellTotalSession != null) {
					uploadMembersInfo.setTotalSession(integerFormat.format(cellTotalSession.getNumericCellValue()).toString());
				}

				if (cellTotalUsed != null) {
					uploadMembersInfo.setTotalUsed(integerFormat.format(cellTotalUsed.getNumericCellValue()).toString());
				}

				if (cellTotalRemain != null) {
					uploadMembersInfo.setTotalRemain(integerFormat.format(cellTotalRemain.getNumericCellValue()).toString());
				}

				if (cellTrainerCode != null) {

					switch (cellTrainerCode.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						if (cellTrainerCode.getStringCellValue() != null) {
							uploadMembersInfo.setTrainerCode(cellTrainerCode.getStringCellValue());
							break;
						}
					case Cell.CELL_TYPE_NUMERIC:

						uploadMembersInfo
								.setTrainerCode(integerFormat.format(cellTrainerCode.getNumericCellValue()) + "");
						break;

						default :
					}

				}

				
				
				if (cellTrainerNickName != null) {
					uploadMembersInfo.setTrainerNickName(cellTrainerNickName.getStringCellValue());
				}

				if (cellTrainerName != null) {
					uploadMembersInfo.setTrainerName(cellTrainerName.getStringCellValue());
				}

				if (uploadMembersInfo.getClub() != null ) {
					listOfDataMembersInfo.add(uploadMembersInfo);
				}
				
				if(cellTrainerLevel  != null)
	            {
					uploadMembersInfo.setTrainerLevel(cellTrainerLevel.getStringCellValue());
	            }
	            
	            if(cellLeepLevel  != null)
	            {
	            	uploadMembersInfo.setLeepLevel(cellLeepLevel.getStringCellValue());
	            }
				
			}

		}

	 final  String respond =	saveData(listOfDataMembersInfo);
		
		for (final UploadMembersInfo uploadMembersInfo : listOfDataMembersInfo) {

			LOG.info(uploadMembersInfo.getClub() + STRING + uploadMembersInfo.getMemberBarcode() + STRING
					+ uploadMembersInfo.getFirstName() + STRING + uploadMembersInfo.getLastName() + STRING
					+ uploadMembersInfo.getMobile() + STRING + uploadMembersInfo.getCreateDate() + STRING
					+ uploadMembersInfo.getStartDate() + STRING + uploadMembersInfo.getExpirationDate() + STRING
					+ uploadMembersInfo.getContractNumber() + STRING + uploadMembersInfo.getType() + STRING
					+ uploadMembersInfo.getItemGroup() + STRING + uploadMembersInfo.getStatus() + STRING
					+ uploadMembersInfo.getPricePerSession() + STRING + uploadMembersInfo.getTotalAmount() + STRING
					+ uploadMembersInfo.getTotalSession() + STRING + uploadMembersInfo.getTotalUsed() + STRING
					+ uploadMembersInfo.getTotalRemain() + STRING + uploadMembersInfo.getTrainerCode() + STRING
					+ uploadMembersInfo.getTrainerNickName() + STRING + uploadMembersInfo.getTrainerName() + STRING
					+ uploadMembersInfo.getTrainerLevel() + STRING + uploadMembersInfo.getLeepLevel());
		}

		return respond;
	
	}
	
	
	private String saveData(final List<UploadMembersInfo> listOfDataMembersInfo) {

		final StringBuffer emptyVal = new StringBuffer(200);
		emptyVal.append("File upload ...."); 
		final List<String> registeredUsers = new ArrayList< String>();
		
		for (final UploadMembersInfo uploadMembersInfo : listOfDataMembersInfo) {

		MemberUploadEntity memberUploadEntity = new MemberUploadEntity();

			final List<MemberUploadEntity> memberUploadEntityList = memberUploadRepository.getMemberByMemberBarCode(uploadMembersInfo.getMemberBarcode());
			
			if(!memberUploadEntityList.isEmpty()){
				LOG.info("--- find member entiry *** 1 ");
				memberUploadEntity = memberUploadEntityList.get(0);
			}
			
			   if(memberUploadEntity.getUserCodechecked() == null || !memberUploadEntity.getUserCodechecked() ){
				
				if (uploadMembersInfo.getClub() != null && uploadMembersInfo.getMemberBarcode() != null
						&& uploadMembersInfo.getFirstName() != null && uploadMembersInfo.getLastName() != null
						&& uploadMembersInfo.getClub().trim().length() != 0
						&& uploadMembersInfo.getMemberBarcode().trim().length() != 0
						&& uploadMembersInfo.getFirstName().trim().length() != 0
						&& uploadMembersInfo.getLastName().trim().length() != 0) {
					memberUploadEntity.setClub(uploadMembersInfo.getClub());
					memberUploadEntity.setMemberBarcode(uploadMembersInfo.getMemberBarcode());
					memberUploadEntity.setFirstName(uploadMembersInfo.getFirstName());
					memberUploadEntity.setLastName(uploadMembersInfo.getLastName());
					memberUploadEntity.setMobile(uploadMembersInfo.getMobile());
					if (memberUploadEntity.getUserCodechecked() == null) {
						memberUploadEntity.setUserCodechecked(false);
					}
					memberUploadRepository.save(memberUploadEntity);

				} else {

					if (uploadMembersInfo.getClub() == null) {
						emptyVal.append(" ,CLUB Id can not be empty.. ");
					}
					if (uploadMembersInfo.getMemberBarcode() == null) {
						emptyVal.append(" , Member Barcode can not be empty.. ");
					}
					if (uploadMembersInfo.getFirstName() == null) {
						emptyVal.append(" , First Name can not be empty.. ");
					}
					if (uploadMembersInfo.getLastName() == null) {
						emptyVal.append(" , Last Name can not be empty.. ");
					}

				}

			}
			   else{
				   registeredUsers.add(uploadMembersInfo.getMemberBarcode());
			   }
		
		}

		for (final UploadMembersInfo uploadTrainerInfo : listOfDataMembersInfo) {

			
			
			TrainerUploadEntity trainerUploadEntity = new TrainerUploadEntity();
			
			final List<TrainerUploadEntity> trainerInfoList = trainerUploadRepository.getTrainerByTrainerCode(uploadTrainerInfo.getTrainerCode());
			
			if(!trainerInfoList.isEmpty()){
				LOG.info("--- find trainer entiry *** 2 ");
				trainerUploadEntity = trainerInfoList.get(0);	
			}
			
			if (trainerUploadEntity.getUserCodechecked() == null || !trainerUploadEntity.getUserCodechecked()) {
				if (uploadTrainerInfo.getClub() != null && uploadTrainerInfo.getTrainerCode() != null && uploadTrainerInfo.getTrainerNickName() != null
						&& uploadTrainerInfo.getTrainerName() != null && uploadTrainerInfo.getLeepLevel() != null
					    && uploadTrainerInfo.getClub().trim().length() != 0
						&& uploadTrainerInfo.getTrainerCode().trim().length() != 0
						&& uploadTrainerInfo.getTrainerNickName().trim().length() != 0
						&& uploadTrainerInfo.getTrainerName().trim().length() != 0
						&& uploadTrainerInfo.getLeepLevel().trim().length() != 0) {
					trainerUploadEntity.setClub(uploadTrainerInfo.getClub());
					trainerUploadEntity.setTrainerCode(uploadTrainerInfo.getTrainerCode());
					trainerUploadEntity.setTrainerNickName(uploadTrainerInfo.getTrainerNickName());
					trainerUploadEntity.setTrainerName(uploadTrainerInfo.getTrainerName());
					trainerUploadEntity.setTrainerLevel(uploadTrainerInfo.getTrainerLevel());
					trainerUploadEntity.setLeepLevel(uploadTrainerInfo.getLeepLevel());
					if (trainerUploadEntity.getUserCodechecked() == null) {
						trainerUploadEntity.setUserCodechecked(false);
					}
					trainerUploadRepository.save(trainerUploadEntity);

				} else {

					if (uploadTrainerInfo.getTrainerCode() == null) {
						emptyVal.append(" ,Trainer Code can not be empty.. ");
					}
					if (uploadTrainerInfo.getTrainerNickName() == null) {
						emptyVal.append(" ,Trainer NickName can not be empty.. ");
					}
					if (uploadTrainerInfo.getTrainerName() == null) {
						emptyVal.append(" ,Trainer Name can not be empty.. ");
					}
				
			}

			}
		}

		for (final UploadMembersInfo uploadcontractInfo : listOfDataMembersInfo) {

			
			
			ContractUploadEntity contractUploadEntity = new ContractUploadEntity();

			final List<ContractUploadEntity> contractUploadEntityList = contractUploadRepository
					.getContracByMemberBarCodeandAndTrainerCodeAndContractNumber(
							uploadcontractInfo.getContractNumber());

			if (!contractUploadEntityList.isEmpty()) {
				contractUploadEntity = contractUploadEntityList.get(0);
				LOG.info("--- find contract entiry *** 3 ");
			}
			
			if(contractUploadEntity.getContractImport() == null || !contractUploadEntity.getContractImport()){
			
				if (uploadcontractInfo.getContractNumber() != null && uploadcontractInfo.getPricePerSession() != null
						&& uploadcontractInfo.getTotalRemain() != null
						&& uploadcontractInfo.getContractNumber().trim().length() != 0
						&& uploadcontractInfo.getPricePerSession().trim().length() != 0
						&& uploadcontractInfo.getTotalRemain().trim().length() != 0
						&& !uploadcontractInfo.getPricePerSession().equals("0") && uploadcontractInfo.getClub() != null
						&& uploadcontractInfo.getMemberBarcode() != null && uploadcontractInfo.getFirstName() != null
						&& uploadcontractInfo.getLastName() != null && uploadcontractInfo.getClub().trim().length() != 0
						&& uploadcontractInfo.getMemberBarcode().trim().length() != 0
						&& uploadcontractInfo.getFirstName().trim().length() != 0
						&& uploadcontractInfo.getLastName().trim().length() != 0
						&& uploadcontractInfo.getTrainerCode() != null
						&& uploadcontractInfo.getTrainerNickName() != null
						&& uploadcontractInfo.getTrainerName() != null && uploadcontractInfo.getLeepLevel() != null
						&& uploadcontractInfo.getTrainerCode().trim().length() != 0
						&& uploadcontractInfo.getTrainerNickName().trim().length() != 0
						&& uploadcontractInfo.getTrainerName().trim().length() != 0
						&& uploadcontractInfo.getLeepLevel().trim().length() != 0) {

				
							contractUploadEntity.setMemberBarcode(uploadcontractInfo.getMemberBarcode());
							contractUploadEntity.setTrainerCode(uploadcontractInfo.getTrainerCode());
							contractUploadEntity.setCreateDate(uploadcontractInfo.getCreateDate());
							contractUploadEntity.setStartDate(uploadcontractInfo.getStartDate());
							contractUploadEntity.setExpirationDate(uploadcontractInfo.getExpirationDate());
							contractUploadEntity.setContractNumber(uploadcontractInfo.getContractNumber());
							contractUploadEntity.setType(uploadcontractInfo.getType());
							contractUploadEntity.setItemGroup(uploadcontractInfo.getItemGroup());
							contractUploadEntity.setStatus(uploadcontractInfo.getStatus());
							contractUploadEntity
									.setPricePerSession(Double.parseDouble(uploadcontractInfo.getPricePerSession()));
							contractUploadEntity
									.setTotalAmount(Double.parseDouble(uploadcontractInfo.getTotalAmount()));
							contractUploadEntity
									.setTotalSession(Integer.parseInt(uploadcontractInfo.getTotalSession()));
							contractUploadEntity.setTotalUsed(Integer.parseInt(uploadcontractInfo.getTotalUsed()));
							contractUploadEntity.setTotalRemain(Integer.parseInt(uploadcontractInfo.getTotalRemain()));
							if (contractUploadEntity.getContractImport() == null) {
								contractUploadEntity.setContractImport(false);
							}

					contractUploadRepository.save(contractUploadEntity);
					

				} else {
					if (uploadcontractInfo.getContractNumber() == null) {
						emptyVal.append(" ,Contract Number can not be empty.. ");
					}
					if (uploadcontractInfo.getPricePerSession() == null) {
						emptyVal.append(" ,PricePer Session can not be empty.. ");
					}
					if (uploadcontractInfo.getTotalRemain() == null) {
						emptyVal.append(" ,Total Remain can not be empty.. ");
					}
			}
			
		}
		}

		if (registeredUsers != null && !registeredUsers.isEmpty()) {

			createFreeSession(registeredUsers);
		}
		
		return emptyVal.toString();
	}
	
	
	@Async
	private void createFreeSession(final List<String> registeredUsers) {
		try {
			for (final String userCode : registeredUsers) {
				LOG.info("-- user code -- , {}", userCode);
				final List<BasicUserEntity> userEntities = userRepo.getUserByUserCode(userCode);

				if (userEntities != null && !userEntities.isEmpty()) {

					migrationUserService.createFreeOrderRequest(userEntities.get(0).getUuid(), userCode,
							UserType.EU.toString());
				}
			}
		} catch (Exception e) {
			LOG.info("--create session --");
		}

	}
}
