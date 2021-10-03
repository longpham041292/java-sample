package asia.cmg.f8.commerce.operation.wallet;

import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;

public interface CreditWalletLevelOperation {

	CreditWalletLevel upgradeCreditWalletLevelOperation(final Double walletAccumulationAmount);
}
