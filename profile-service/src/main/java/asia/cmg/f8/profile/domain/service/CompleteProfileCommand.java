package asia.cmg.f8.profile.domain.service;

import asia.cmg.f8.common.security.Account;

/**
 * @author tung.nguyenthanh
 */
public class CompleteProfileCommand {
    private final Account account;

    public CompleteProfileCommand(final Account account) {
        super();
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

}
