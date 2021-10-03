package asia.cmg.f8.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.user.entity.AccountTokenEntity;
import asia.cmg.f8.user.repository.AccountTokenRepository;
import asia.cmg.f8.user.service.AccountTokenService;

/**
 * Created on 07/29/22.
 */
@Service
public class AccountTokenServiceImpl implements AccountTokenService {
	
	@Autowired
	AccountTokenRepository accountTokenRepository;

	@Override
	public AccountTokenEntity findByUUID(final String uuid) {
		// TODO Auto-generated method stub
		return accountTokenRepository.findByUUID(uuid);
	}

	@Override
	@Transactional
	public void saveToken(final String accessToken,final String refreshToken,final String uuid) {
		// TODO Auto-generated method stub
		accountTokenRepository.saveAndFlush(new AccountTokenEntity(uuid, accessToken, refreshToken));
	}

	@Override
	public AccountTokenEntity findByToken(String accessToken) {
		// TODO Auto-generated method stub
		return accountTokenRepository.findByAccessToken(accessToken);
	}

	@Override
	@Transactional
	public void resetToken(final String accessToken,final String refreshToken, String uuid) {
		// TODO Auto-generated method stub
		AccountTokenEntity acc = accountTokenRepository.findByUUID(uuid);
		if(acc != null) {
			acc.setAccessToken(accessToken);
			acc.setRefreshToken(refreshToken);
			accountTokenRepository.save(acc);
		}
			
	}

	@Override
	public void removeToken(AccountTokenEntity accToken) {
		// TODO Auto-generated method stub
		accountTokenRepository.delete(accToken);
	}

	@Override
	public AccountTokenEntity verifyToken(String uuid, String accessToken, String refreshToken) {
		// TODO Auto-generated method stub
		AccountTokenEntity accToken = accountTokenRepository.findByUUID(uuid);
		if(accToken != null && accToken.getAccessToken().equalsIgnoreCase(accessToken) && accToken.getRefreshToken().equalsIgnoreCase(refreshToken))
			return accToken;
		return null;
	}

	@Override
	public AccountTokenEntity findByResetPasswordToken(String resetPasswordToken) {
		return accountTokenRepository.findByResetPasswordToken(resetPasswordToken);
	}

	@Override
	public void saveToken(AccountTokenEntity accToken) {
		accountTokenRepository.save(accToken);
	}
	
	
	
}
