package asia.cmg.f8.session.wrapper.dto;

import java.sql.Timestamp;

public class ActiveOrderTimeRange {

    private String uuid;
    private Timestamp expireTime;
    private int limitDays;
    private Timestamp minStartTime;
    private String packageUuid;

    public ActiveOrderTimeRange(final String uuid, final Timestamp expireTime,
            final Integer limitDays, final Timestamp minStartTime, final String packageUuid) {
        super();
        this.uuid = uuid;
        this.expireTime = expireTime;
        this.limitDays = limitDays.intValue();
        this.minStartTime = minStartTime;
        this.packageUuid = packageUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(final Timestamp expireTime) {
        this.expireTime = expireTime;
    }

    public int getLimitDays() {
        return limitDays;
    }

    public void setLimitDays(final int limitDays) {
        this.limitDays = limitDays;
    }

    public Timestamp getMinStartTime() {
        return minStartTime;
    }

    public void setMinStartTime(final Timestamp minStartTime) {
        this.minStartTime = minStartTime;
    }

	public String getPackageUuid() {
		return packageUuid;
	}

	public void setPackageUuid(String packageUuid) {
		this.packageUuid = packageUuid;
	}
}
