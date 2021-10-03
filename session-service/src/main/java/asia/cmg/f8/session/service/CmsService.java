package asia.cmg.f8.session.service;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import asia.cmg.f8.session.client.cms.CmsClientConfig;
import asia.cmg.f8.session.config.CmsProperties;
import asia.cmg.f8.session.dto.cms.BookETicketResponseDto;
import asia.cmg.f8.session.dto.cms.CMSClassBookingDTO;
import asia.cmg.f8.session.dto.cms.CMSClassCategoryBookingDTO;
import asia.cmg.f8.session.dto.cms.CMSAuthenticationResponseDTO;
import asia.cmg.f8.session.dto.cms.CMSETicketDTO;
import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.repository.CreditBookingRepository;

@Service
public class CmsService {

	@Autowired
	private CreditBookingRepository creditBookingRepository;

	@Autowired
	private CmsProperties properties;

	private final String GET_TOKEN_URL = "%s/v2/users/signin/email";
	private final String GET_CLASS_DETAIL_URL = "%s/v2/studios/{studio-uuid}/schedule/sessions/{class-id}";
	private final String GET_ETICKET_DETAIL_URL = "%s/v2/etickets/{id}?booking_day=%s";
	private final String GET_ETICKET_ALL_DAY_URL = "%s/v2/studios/{studio-uuid}/all-day-eticket";
	private final String CHECK_PUBLISH_STUDIO = "%s/v2/studios/{studio_uuid}";
	private final String CHECK_CATEGORY_BELONG_SESSIONS = "%s/v2/categories/{category_id}/sessions/belong?session_ids=%s";
	private final String CHECK_PUBLISH_CLASS = "%s/v2/studios/{id}/schedule/sessions/{session_id}";

	private static final Logger LOGGER = LoggerFactory.getLogger(CmsService.class);

	public String getAuthToken() {

		try {
			long currentTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
			long expiration = CmsClientConfig.getExpiration();

			if (expiration != 0 && expiration > currentTime) {
				return CmsClientConfig.getAuthToken();
			} else {
				RestTemplate restTemplate = this.getHttpsRestTemplate();

				Map<String, String> authen = new HashMap<String, String>();
				authen.put("address", properties.getEmail());
				authen.put("password", properties.getPassword());

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

				ResponseEntity<CMSAuthenticationResponseDTO> response = restTemplate.
												postForEntity(String.format(GET_TOKEN_URL, properties.getUrl()),
																authen,
																CMSAuthenticationResponseDTO.class);
				CMSAuthenticationResponseDTO authenResponse = response.getBody();
				if(authenResponse != null) {
					CmsClientConfig.setToken(authenResponse.getData().getAuthToken());
					CmsClientConfig.setExpiration(authenResponse.getData().getExpiration());
					return CmsClientConfig.getAuthToken();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return "";
	}

	public CMSETicketDTO getETicketDetail(long ticketId, long bookingDayInMillis) {
		try {
			String token = getAuthToken();

			if(!StringUtils.isEmpty(token)) {
				RestTemplate restTemplate = getHttpsRestTemplate();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
				headers.set("Authorization", token);
				HttpEntity<Object> requestHeader = new HttpEntity<Object>(headers);

				Map<String, Object> pathParams = new HashMap<String, Object>();
				pathParams.put("id", ticketId);

				ResponseEntity<CMSETicketDTO> response = restTemplate.exchange(String.format(GET_ETICKET_DETAIL_URL, properties.getUrl(), bookingDayInMillis),
																			HttpMethod.GET,
																			requestHeader,
																			CMSETicketDTO.class,
																			ticketId);
				if(response != null && response.getStatusCode() == HttpStatus.OK) {
					return response.getBody();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return null;
	}

	public CMSClassBookingDTO getClassDetail(String studioUuid, long classId) {
		try {
			String token = getAuthToken();

			if(!StringUtils.isEmpty(token)) {
				RestTemplate restTemplate = getHttpsRestTemplate();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
				headers.set("Authorization", token);
				HttpEntity<Object> requestHeader = new HttpEntity<Object>(headers);

				Map<String, Object> pathParams = new HashMap<String, Object>();
				pathParams.put("studio-uuid", studioUuid);
				pathParams.put("class-id", classId);

				ResponseEntity<CMSClassBookingDTO> response = restTemplate.exchange(String.format(GET_CLASS_DETAIL_URL, properties.getUrl()),
																					HttpMethod.GET,
																					requestHeader,
																					CMSClassBookingDTO.class,
																					pathParams);
				if(response != null && response.getStatusCode() == HttpStatus.OK) {
					return (CMSClassBookingDTO)response.getBody();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return null;
	}

	public CMSETicketDTO getETicketAllDayDetail(String studioUuid) {
		try {
			String token = getAuthToken();

			if(!StringUtils.isEmpty(token)) {
				RestTemplate restTemplate = getHttpsRestTemplate();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
				headers.set("Authorization", token);
				HttpEntity<Object> requestHeader = new HttpEntity<Object>(headers);

				Map<String, Object> pathParams = new HashMap<String, Object>();
				pathParams.put("studio-uuid", studioUuid);

				ResponseEntity<CMSETicketDTO> response = restTemplate.exchange(String.format(GET_ETICKET_ALL_DAY_URL, properties.getUrl()),
						HttpMethod.GET,
						requestHeader,
						CMSETicketDTO.class,
						pathParams);
				if(response != null && response.getStatusCode() == HttpStatus.OK) {
					return response.getBody();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return null;
	}
	
	public CMSClassCategoryBookingDTO checkCategoryBelongSessions(String category_id, List<String> session_ids) {
		try {
			String token = getAuthToken();

			if(!StringUtils.isEmpty(token)) {
				RestTemplate restTemplate = getHttpsRestTemplate();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
				headers.set("Authorization", token);
				HttpEntity<Object> requestHeader = new HttpEntity<Object>(headers);

				Map<String, Object> pathParams = new HashMap<String, Object>();
				pathParams.put("category_id", category_id);
				
				ResponseEntity<CMSClassCategoryBookingDTO> response = restTemplate.exchange(String.format(CHECK_CATEGORY_BELONG_SESSIONS, properties.getUrl(), String.join(",", session_ids)),
						HttpMethod.GET,
						requestHeader,
						CMSClassCategoryBookingDTO.class,
						pathParams);
				if(response != null && response.getStatusCode() == HttpStatus.OK) {
					return response.getBody();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	public CMSClassCategoryBookingDTO checkPublishClass(String id, Long session_id) {
		try {
			String token = getAuthToken();

			if(!StringUtils.isEmpty(token)) {
				RestTemplate restTemplate = getHttpsRestTemplate();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
				headers.set("Authorization", token);
				HttpEntity<Object> requestHeader = new HttpEntity<Object>(headers);

				Map<String, Object> pathParams = new HashMap<String, Object>();
				pathParams.put("id", id);
				pathParams.put("session_id", session_id);

				ResponseEntity<CMSClassCategoryBookingDTO> response = restTemplate.exchange(String.format(CHECK_PUBLISH_CLASS, properties.getUrl()),
						HttpMethod.GET,
						requestHeader,
						CMSClassCategoryBookingDTO.class,
						pathParams);
				if(response != null && response.getStatusCode() == HttpStatus.OK) {
					return response.getBody();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	public CMSClassCategoryBookingDTO checkPublishStudio(String studio_uuid) {
		try {
			String token = getAuthToken();

			if(!StringUtils.isEmpty(token)) {
				RestTemplate restTemplate = getHttpsRestTemplate();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
				headers.set("Authorization", token);
				HttpEntity<Object> requestHeader = new HttpEntity<Object>(headers);

				Map<String, Object> pathParams = new HashMap<String, Object>();
				pathParams.put("studio_uuid", studio_uuid);

				ResponseEntity<CMSClassCategoryBookingDTO> response = restTemplate.exchange(String.format(CHECK_PUBLISH_STUDIO, properties.getUrl()),
						HttpMethod.GET,
						requestHeader,
						CMSClassCategoryBookingDTO.class,
						pathParams);
				if(response != null && response.getStatusCode() == HttpStatus.OK) {
					return response.getBody();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

//	@Transactional
//	public BookETicketResponseDto bookETicket(ETicketDto ticket, Account account) {
//
//		long transactionId = commerceClient.subtractCreditWallet(account.uuid(), (int)ticket.getCreditAmount(), CreditTransactionType.STUDIO_CHECKIN.getText(), "Booking " + ticket.getName(), new ArrayList<String>());
//		CreditBookingEntity booking = new CreditBookingEntity();
//		booking.setBookedBy(account.uuid());
//		booking.setBookingType(BookingServiceType.ETICKET);
//		booking.setCreditAmount((int)ticket.getCreditAmount());
//		booking.setStudioUuid(ticket.getStudio().getId());
//		booking.setStudioName(ticket.getStudio().getName());
//		booking.setStatus(CreditBookingSessionStatus.BOOKED);
//		booking.setClientUuid(account.uuid());
//
//		CreditBookingETicketEntity eTicket = new CreditBookingETicketEntity();
//		eTicket.setServiceFee(ticket.getServiceFee());
//		eTicket.setServiceId(ticket.getId());
//		eTicket.setServiceName(ticket.getName());
//		eTicket.setAllDay(ticket.isAllDay());
//
//		String openingHoursText = "";
//		for (ETicketOpentimeDto time : ticket.getOpenTimes()) {
//			openingHoursText.concat(" & ");
//			openingHoursText.concat(time.getFrom());
//			openingHoursText.concat("-");
//			openingHoursText.concat(time.getTo());
//		}
//		// Remove '&' in start of opening hours text
//		eTicket.setOpeningHours(openingHoursText.substring(1));
//		eTicket.setNoShowFee(ticket.getPayableNoShowFee());
//
//		booking.addETicket(eTicket);
//
//		creditBookingRepository.save(booking);
//		BookETicketResponseDto dto = convertToBookingDto(booking);
//		return dto;
//
//	}

	@Transactional
	public BookETicketResponseDto changeETicketStatus(long bookingId, CreditBookingSessionStatus status) {

		CreditBookingEntity booking = creditBookingRepository.findOne(bookingId);
		if(booking == null)
			return null;

		booking.setStatus(status);

		creditBookingRepository.save(booking);
		BookETicketResponseDto dto = convertToBookingDto(booking);
		return dto;

	}

	private BookETicketResponseDto convertToBookingDto(CreditBookingEntity booking) {
		BookETicketResponseDto dto = new BookETicketResponseDto();
		dto.setId(booking.getId());
		dto.setStatus(booking.getStatus());
		dto.setStudioName(booking.getStudioName());
		dto.setStudioUuid(booking.getStudioUuid());
		return dto;
	}

	private RestTemplate getHttpsRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
	        @Override
	        public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
	            return true;
	        }
	    };
	    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
	    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
	    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
	    requestFactory.setHttpClient(httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);

		return restTemplate;
	}


}
