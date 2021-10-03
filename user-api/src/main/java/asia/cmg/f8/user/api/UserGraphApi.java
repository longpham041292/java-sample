package asia.cmg.f8.user.api;

import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.user.entity.AccountEntity;
import asia.cmg.f8.user.entity.AccountTokenEntity;
import asia.cmg.f8.user.entity.JwtResponse;
import asia.cmg.f8.user.enums.RoleEnum;
import asia.cmg.f8.user.service.AccountService;
import asia.cmg.f8.user.service.AccountTokenService;
import asia.cmg.f8.user.service.JwtService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/oauth")
public class UserGraphApi {
	  @Autowired
	  private JwtService jwtService;
	  
	  @Autowired
	  private AccountService accountService;
	  
	  @Autowired
	  private AccountTokenService accountTokenService;
	 
	  
	  /* ---------------- Generate Token ------------------------ */
	  @RequestMapping(value = "/v1/token", method = RequestMethod.POST,produces = APPLICATION_JSON_VALUE)
	  public ResponseEntity<JwtResponse> requestAccessToken(@RequestBody final Map<String,String> account) {
		  
		final AccountEntity accEntity = accountService.verifyAccount(account.get("username" ), account.get("password"));
		AccountTokenEntity accTokenEntity = new AccountTokenEntity();
	    JwtResponse jwt = new JwtResponse("", "", 0, 0, "", null, null);
		if(accEntity != null) {
			 jwt = jwtService.generateTokenLogin(accEntity.getUuid());
			accTokenEntity.setAccessToken(jwt.getAccessToken());
			accTokenEntity.setRefreshToken(jwt.getRefreshToken());
			accTokenEntity.setUuid(accEntity.getUuid());
			accountTokenService.saveToken(jwt.getAccessToken(),jwt.getRefreshToken(),accEntity.getUuid());
			jwt.setStatus("approved");
			jwt.setRoles(RoleEnum.getRole(accEntity.getRoles()));
			jwt.setUser(accEntity.getUserInfo());
			return new ResponseEntity<JwtResponse>(jwt, HttpStatus.OK);
		}
	    return new ResponseEntity<JwtResponse>(jwt, HttpStatus.NOT_ACCEPTABLE);
	  }
	  /* ---------------- Remove Token -Sign out ------------------------ */
	  @RequestMapping(value = "/v1/logout", method = RequestMethod.POST,produces = APPLICATION_JSON_VALUE)
	  public ResponseEntity<Map<String,String>> requestRemoveToken(@RequestParam final String accessToken) {
		  final AccountTokenEntity accTokenEntity =  accountTokenService.findByToken(accessToken);
		  Map<String,String> result = new HashMap<String, String>();
		  result.put("detail", "SYSTEM_ERROR");
		  result.put("code", "-1");
		  if(accTokenEntity != null) {
			accountTokenService.removeToken(accTokenEntity);
			 result.put("code", "0");
			 result.put("detail", "SUCCESS");
		  }
		  return new ResponseEntity<Map<String,String>>(result, HttpStatus.OK);
	  }
	  /* ----------------- Reset Token - Sign out ------------------------ */
	  @RequestMapping(value = "/v1/refresh", method = RequestMethod.POST,produces = APPLICATION_JSON_VALUE)
	  public ResponseEntity<JwtResponse> requestResetToken(@RequestBody final Map<String,String> request) {
		  final AccountTokenEntity accTokenEntity =  accountTokenService.verifyToken(request.get("uuid"),request.get("access_token"),request.get("refresh_token"));
		  JwtResponse jwt = new JwtResponse("", "", 0, 0, "", null, null);
		  if(accTokenEntity != null) {
			  jwt = jwtService.generateTokenLogin(accTokenEntity.getUuid());
			  accountTokenService.resetToken(jwt.getAccessToken(),jwt.getRefreshToken(), accTokenEntity.getUuid());
			  jwt.setStatus("approved");
			  return new ResponseEntity<JwtResponse>(jwt, HttpStatus.OK);
		  }
		  return new ResponseEntity<JwtResponse>(jwt, HttpStatus.NOT_ACCEPTABLE);
	  }
//	  @RequestMapping(value = "/v1/token", method = RequestMethod.GET)
//	  public ResponseEntity<List<AccEntity>> getAllUser() {
//	    return new ResponseEntity<List<AccEntity>>(accountService.findAll(), HttpStatus.OK);
//	  }
//	  /* ---------------- GET USER BY ID ------------------------ */
//	  @RequestMapping(value = "/accounts/{id}", method = RequestMethod.GET)
//	  public ResponseEntity<Object> getUserById(@PathVariable int id) {
//		  AccEntity acc = accountService.findById(id);
//	    if (acc != null) {
//	      return new ResponseEntity<Object>(acc, HttpStatus.OK);
//	    }
//	    return new ResponseEntity<Object>("Not Found User", HttpStatus.NO_CONTENT);
//	  }
//	  /* ---------------- CREATE NEW USER ------------------------ */
//	  @RequestMapping(value = "/accounts", method = RequestMethod.POST)
//	  public ResponseEntity<String> createUser(@RequestBody AccEntity acc) {
//	    if (accountService.add(acc)) {
//	      return new ResponseEntity<String>("Created!", HttpStatus.CREATED);
//	    } else {
//	      return new ResponseEntity<String>("User Existed!", HttpStatus.BAD_REQUEST);
//	    }
//	  }
//	  /* ---------------- DELETE USER ------------------------ */
//	  @RequestMapping(value = "/accounts/{id}", method = RequestMethod.DELETE)
//	  public ResponseEntity<String> deleteUserById(@PathVariable int id) {
//		  accountService.delete(id);
//	    return new ResponseEntity<String>("Deleted!", HttpStatus.OK);
//	  }
//	  @RequestMapping(value = "/login", method = RequestMethod.POST)
//	  public ResponseEntity<String> login(HttpServletRequest request, @RequestBody AccEntity acc) {
//	    String result = "";
//	    HttpStatus httpStatus = null;
//	    try {
//	      if (accountService.checkLogin(acc)) {
//	        result = jwtService.generateTokenLogin(acc.getUsername());
//	        httpStatus = HttpStatus.OK;
//	      } else {
//	        result = "Wrong userId and password";
//	        httpStatus = HttpStatus.BAD_REQUEST;
//	      }
//	    } catch (Exception ex) {
//	      result = "Server Error";
//	      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//	    }
//	    return new ResponseEntity<String>(result, httpStatus);
//	  }
}
