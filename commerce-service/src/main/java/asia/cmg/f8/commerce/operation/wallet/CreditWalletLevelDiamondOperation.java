package asia.cmg.f8.commerce.operation.wallet;

import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;

public class CreditWalletLevelDiamondOperation implements CreditWalletLevelOperation {

	@Override
	public CreditWalletLevel upgradeCreditWalletLevelOperation(Double walletAccumulationAmount) {
		
		return CreditWalletLevel.DIAMOND;
	}
}
