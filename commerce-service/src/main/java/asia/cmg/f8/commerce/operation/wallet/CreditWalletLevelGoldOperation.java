package asia.cmg.f8.commerce.operation.wallet;

import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;

public class CreditWalletLevelGoldOperation implements CreditWalletLevelOperation {

	@Override
	public CreditWalletLevel upgradeCreditWalletLevelOperation(Double walletAccumulationAmount) {

		if (walletAccumulationAmount >= CreditWalletLevel.DIAMOND.getAccumulationAmount()) {
			return CreditWalletLevel.DIAMOND;
		} else if (walletAccumulationAmount >= CreditWalletLevel.PLATINUM.getAccumulationAmount()) {
			return CreditWalletLevel.PLATINUM;
		} 
		
		return CreditWalletLevel.GOLD;
	}
}
