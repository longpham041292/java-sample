package asia.cmg.f8.commerce.entity.credit;

import asia.cmg.f8.commerce.operation.wallet.CreditWalletLevelOperation;
import asia.cmg.f8.commerce.operation.wallet.CreditWalletLevelDefaultOperation;

import com.fasterxml.jackson.annotation.JsonValue;

import asia.cmg.f8.commerce.operation.wallet.CreditWalletLevelBasicOperation;
import asia.cmg.f8.commerce.operation.wallet.CreditWalletLevelGoldOperation;
import asia.cmg.f8.commerce.operation.wallet.CreditWalletLevelDiamondOperation;
import asia.cmg.f8.commerce.operation.wallet.CreditWalletLevelPlatinumOperation;


public enum CreditWalletLevel implements CreditWalletLevelOperation {
	DEFAULT(new CreditWalletLevelDefaultOperation(), 0), 
	BASIC(new CreditWalletLevelBasicOperation(), 500000), 
	GOLD(new CreditWalletLevelGoldOperation(), 1500000), 
	PLATINUM(new CreditWalletLevelPlatinumOperation(), 2500000), 
	DIAMOND(new CreditWalletLevelDiamondOperation(), 5000000);
	
	private final int accumulationAmount;
	private final CreditWalletLevelOperation operation;
	
	private CreditWalletLevel(final CreditWalletLevelOperation operation, final int accumulationAmount) {
		this.operation = operation;
		this.accumulationAmount = accumulationAmount;
	}

	public int getAccumulationAmount() {
		return accumulationAmount;
	}

	@Override
	public CreditWalletLevel upgradeCreditWalletLevelOperation(Double walletAccumulationAmount) {
		return operation.upgradeCreditWalletLevelOperation(walletAccumulationAmount);
	}
	
	@JsonValue
    public int toValue() {
        return ordinal();
    }
}
