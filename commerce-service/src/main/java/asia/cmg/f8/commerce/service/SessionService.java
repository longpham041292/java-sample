package asia.cmg.f8.commerce.service;

import asia.cmg.f8.commerce.client.SessionClient;
import asia.cmg.f8.commerce.utils.CommerceUtils;

import org.springframework.stereotype.Service;

import java.util.Map;

import javax.inject.Inject;

@Service
public class SessionService {


    @Inject
    private SessionClient sessionClient;
    
    public boolean isContractExist(final String userId, final String ptId){
        final Map<String, Boolean> contract = sessionClient.checkValidContractWithTrainer(
                userId, ptId);
        return contract == null || contract.get(CommerceUtils.CONTRACT);
    }
}
