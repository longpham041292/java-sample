package asia.cmg.f8.commerce.operation.wallet;

import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;

public class CreditWalletLevelPlatinumOperation implements CreditWalletLevelOperation {

	@Override
	public CreditWalletLevel upgradeCreditWalletLevelOperation(Double walletAccumulationAmount) {

		if (walletAccumulationAmount >= CreditWalletLevel.DIAMOND.getAccumulationAmount()) {
			return CreditWalletLevel.DIAMOND;
		}
		
		return CreditWalletLevel.PLATINUM;
	}
}
