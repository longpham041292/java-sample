package asia.cmg.f8.user.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import asia.cmg.f8.user.entity.AccountEntity;
import asia.cmg.f8.user.repository.AccountRepository;
import asia.cmg.f8.user.service.AccountService;

/**
 * Created on 07/29/22.
 */
@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	AccountRepository accountRepository;
	
//	@Autowired
//	BCryptPasswordEncoder bCryptPasswordEncoder;

	
	 @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
	 
	@Override
	public AccountEntity findByUserName(final String username) {
		// TODO Auto-generated method stub
		return accountRepository.findByUserName(username);
	}

	@Override
	public AccountEntity verifyAccount(final String username,final String password) {
		// TODO Auto-generated method stub
		AccountEntity accEntity = accountRepository.findByUserName(username);
		if(accEntity != null) {
			PasswordEncoder passwordEnocder = new BCryptPasswordEncoder();
			if (passwordEnocder.matches(password, accEntity.getPassword())) {
			    return accEntity;
			}
		}
		return null;
	}

	@Override
	public AccountEntity findByUuid(String uuid) {
		// TODO Auto-generated method stub
		return accountRepository.findByUuid(uuid);
	}
	
	
}
