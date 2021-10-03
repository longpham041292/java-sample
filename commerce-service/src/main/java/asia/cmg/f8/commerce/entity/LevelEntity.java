package asia.cmg.f8.commerce.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "level")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LevelEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String uuid;

    @Id
    @Column(length = 10)
    private String code;
    @JsonProperty(value = "value")
    @Column(name = "value", columnDefinition = "int default 0")
    private Integer value;
    
    @Column(name = "pt_booking_credit", columnDefinition = "int default 0")
    @JsonProperty(value = "pt_booking_credit")
    private Integer ptBookingCredit;
    
    @Column(name = "pt_commission", columnDefinition = "double default 0")
    @JsonProperty(value = "pt_commission")
    private Double ptCommission;
    
    @Column(name = "pt_burned_commission", columnDefinition = "double default 0")
    @JsonProperty(value = "pt_burned_commission")
    private Double ptBurnedCommission;

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

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getPtBookingCredit() {
		return ptBookingCredit;
	}

	public void setPtBookingCredit(Integer ptBookingCredit) {
		this.ptBookingCredit = ptBookingCredit;
	}

	public Double getPtCommission() {
		return ptCommission;
	}

	public void setPtCommission(Double ptCommission) {
		this.ptCommission = ptCommission;
	}

	public Double getPtBurnedCommission() {
		return ptBurnedCommission;
	}

	public void setPtBurnedCommission(Double ptBurnedCommission) {
		this.ptBurnedCommission = ptBurnedCommission;
	}
}
