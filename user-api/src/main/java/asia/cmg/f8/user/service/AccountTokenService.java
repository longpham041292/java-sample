package asia.cmg.f8.user.service;

import org.springframework.stereotype.Service;

import asia.cmg.f8.user.entity.AccountTokenEntity;

@Service
public interface AccountTokenService {
	public AccountTokenEntity findByUUID(final String uuid);
	public AccountTokenEntity verifyToken(final String uuid,final String accessToken,final String refreshToken);	
	public AccountTokenEntity findByToken(final String accessToken);
	public AccountTokenEntity findByResetPasswordToken(final String resetPasswordToken);
	public void saveToken(final String accessToken,final String refreshToken,final String uuid);
	public void saveToken(AccountTokenEntity accToken);
	public void resetToken(final String accessToken,final String refreshToken,final String uuid);
	public void removeToken(AccountTokenEntity accToken);
}
