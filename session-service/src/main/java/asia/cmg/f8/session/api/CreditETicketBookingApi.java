package asia.cmg.f8.session.api;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.CreditETicketBookingRequest;
import asia.cmg.f8.session.dto.CreditETicketBookingResponse;
import asia.cmg.f8.session.dto.cms.BookETicketResponseDto;
import asia.cmg.f8.session.dto.cms.CMSETicketDTO;
import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.service.CmsService;
import asia.cmg.f8.session.service.CreditBookingService;

@RestController
public class CreditETicketBookingApi {
	
	private static final String UUID = "uuid";
	private static final String ID = "id";
	private static final Logger LOGGER = LoggerFactory.getLogger(CreditETicketBookingApi.class);
	
	@Autowired
	private CmsService cmsServie;
	
	@Autowired
	private CreditBookingService creditBookingService;
	
//	@PostMapping(value = "/mobile/v1/wallets/studios/schedules/etickets/{id}/book", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Object> bookClubAccess(@PathVariable(name = ID, required = true) final int id ,
//			final Account account) {
//		
//		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
//		apiResponse.setStatus(ErrorCode.SUCCESS);
//		try {
//			CMSETicketDTO eTicket = cmsServie.getETicketDetail(id);
//			
////			if(eTicket != null) {
////				BookETicketResponseDto dto = cmsServie.bookETicket(eTicket, account);
////				if(dto != null) {
////					apiResponse.setData(dto);
////					apiResponse.setStatus(ErrorCode.SUCCESS);
////				}
////			}
//			
//			// Checkin Success
//			//apiResponse.setData(walletEntity);
//			
//		} catch (Exception e) {
//			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
//			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
//		}
//		
//		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
//	}
	
//	@PostMapping(value = "/mobile/v1/wallets/studios/schedules/etickets/booking/{id}/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Object> clubAccessCheckin(@PathVariable(name = ID, required = true) final int id ,
//			final Account account) {
//		
//		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
//		apiResponse.setStatus(ErrorCode.SUCCESS);
//		try {
//			CMSETicketDTO eTicket = cmsServie.getETicketDetail(id);
//			
//			if(eTicket != null) {
//				BookETicketResponseDto dto = cmsServie.changeETicketStatus(id, CreditBookingSessionStatus.COMPLETED);
//				if(dto != null) {
//					apiResponse.setData(dto);
//					apiResponse.setStatus(ErrorCode.SUCCESS);
//				}
//			}
//			
//			// Checkin Success
//			//apiResponse.setData(walletEntity);
//			
//		} catch (Exception e) {
//			LOGGER.error("[WalletApi] error detail: {}", e.getMessage());
//			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
//		}
//		
//		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
//	}
}
