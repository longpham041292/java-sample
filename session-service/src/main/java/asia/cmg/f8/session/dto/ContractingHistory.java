package asia.cmg.f8.session.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 1/10/17.
 */
public class ContractingHistory {

    private ContractingHistory.BasicOrderInfo userOrder;
    private List<ContractingHistory.Session> sessions;

    public BasicOrderInfo getUserOrder() {
        return userOrder;
    }

    public void setUserOrder(final BasicOrderInfo userOrder) {
        this.userOrder = userOrder;
    }

    public List<Session> getSessions() {
        return new ArrayList<>(sessions);
    }

    public void setSessions(final List<Session> sessions) {
        this.sessions = new ArrayList<>(sessions);
    }

    public static class BasicUserInfo {

        private String uuid;
        private String avatar;
        private String fullName;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(final String uuid) {
            this.uuid = uuid;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(final String avatar) {
            this.avatar = avatar;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(final String fullName) {
            this.fullName = fullName;
        }
    }

    public static class BasicOrderInfo {

        private String uuid;
        private String orderCode;
        private Integer numberOfSession;
        private Integer numberOfBurnedSession;
        private Long expiredDate;
        private Integer limitOfDay;
        private Integer numberOfConfirmedSession;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(final String uuid) {
            this.uuid = uuid;
        }

        public String getOrderCode() {
            return orderCode;
        }

        public void setOrderCode(final String orderCode) {
            this.orderCode = orderCode;
        }

        public Integer getNumberOfSession() {
            return numberOfSession;
        }

        public void setNumberOfSession(final Integer numberOfSession) {
            this.numberOfSession = numberOfSession;
        }

        public Integer getNumberOfBurnedSession() {
            return numberOfBurnedSession;
        }

        public void setNumberOfBurnedSession(final Integer numberOfBurnedSession) {
            this.numberOfBurnedSession = numberOfBurnedSession;
        }
        
        public Integer getNumberOfConfirmedSession() {
            return numberOfConfirmedSession;
        }

        public void setNumberOfConfirmedSession(final Integer numberOfConfirmedSession) {
            this.numberOfConfirmedSession = numberOfConfirmedSession;
        }

        public Long getExpiredDate() {
            return expiredDate;
        }

        public void setExpiredDate(final Long expiredDate) {
            this.expiredDate = expiredDate;
        }

        public Integer getLimitOfDay() {
            return limitOfDay;
        }

        public void setLimitOfDay(final Integer limitOfDay) {
            this.limitOfDay = limitOfDay;
        }
    }

    public static class Session {

        private String uuid;
        private String status;
        private Long startTime;
        private String packageUuid;
        private String trainerName;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(final String uuid) {
            this.uuid = uuid;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(final Long startTime) {
            this.startTime = startTime;
        }

        public String getPackageUuid() {
            return packageUuid;
        }

        public void setPackageUuid(final String packageUuid) {
            this.packageUuid = packageUuid;
        }

        public String getTrainerName() {
            return trainerName;
        }

        public void setTrainerName(final String trainerName) {
            this.trainerName = trainerName;
        }
    }
    
    public static class BasicTrainerInfo {

        private String uuid;
        private String avatar;
        private String fullName;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(final String uuid) {
            this.uuid = uuid;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(final String avatar) {
            this.avatar = avatar;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(final String fullName) {
            this.fullName = fullName;
        }
    }
}
