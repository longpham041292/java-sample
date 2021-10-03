package asia.cmg.f8.profile.database.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "level")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class LevelEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String uuid;

    @Id
    @Column(length = 10)
    private String code;
    
    @Column(name = "value", columnDefinition = "int default 0")
    private Integer value;
    
    @Column(name = "pt_booking_credit", columnDefinition = "int default 0")
    private Integer ptBookingCredit;
    
    @Column(name = "pt_commission", columnDefinition = "double default 0")
    private Double ptCommission;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

	public Integer getPtBookingCredit() {
		return ptBookingCredit;
	}

	public void setPtBookingCredit(Integer ptBookingCredit) {
		this.ptBookingCredit = ptBookingCredit;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Double getPtCommission() {
		return ptCommission;
	}

	public void setPtCommission(Double ptCommission) {
		this.ptCommission = ptCommission;
	}
}
