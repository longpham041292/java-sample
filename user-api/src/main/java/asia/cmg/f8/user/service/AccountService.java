package asia.cmg.f8.user.service;

import org.springframework.stereotype.Service;

import asia.cmg.f8.user.entity.AccountEntity;

@Service
public interface AccountService {
	public AccountEntity findByUserName(final String emailphone);
	public AccountEntity verifyAccount(final String username,final String password);
	public AccountEntity findByUuid(final String uuid);
}
