package asia.cmg.f8.commerce.operation.wallet;

import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;

public class CreditWalletLevelDefaultOperation implements CreditWalletLevelOperation {

	@Override
	public CreditWalletLevel upgradeCreditWalletLevelOperation(Double walletAccumulationAmount) {
		if (walletAccumulationAmount >= CreditWalletLevel.DIAMOND.getAccumulationAmount()) {
			return CreditWalletLevel.DIAMOND;
		} else if (walletAccumulationAmount >= CreditWalletLevel.PLATINUM.getAccumulationAmount()) {
			return CreditWalletLevel.PLATINUM;
		} else if (walletAccumulationAmount >= CreditWalletLevel.GOLD.getAccumulationAmount()) {
			return CreditWalletLevel.GOLD;
		} else if (walletAccumulationAmount >= CreditWalletLevel.BASIC.getAccumulationAmount()) {
			return CreditWalletLevel.BASIC;
		}
		
		return CreditWalletLevel.DEFAULT;
	}
}
