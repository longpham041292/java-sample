package asia.cmg.f8.commerce.entity;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "order_options")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrderOptionEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Integer id;
	
	@Column(name = "code", unique = true, nullable = false)
    private String code;
	
	/**
	 * Indicate using for PRODUCT or SUBSCRIPTION
	 */
	@Column(name = "order_type", nullable = false)
	private String order_type = OrderType.SUBSCRIPTION.toString();
	
	@Column(name = "level_code", nullable = false)
	private String levelCode = "";
	
	/**
	 * Indicate for ordering on UI
	 */
	@Column(name = "ordering", nullable = false)
	private Integer ordering = 0;
	
	@Column(name = "description_vn", nullable = true, length = 255)
	private String description_vn;
	
	@Column(name = "description_en", nullable = true, length = 255)
	private String description_en;
	
	@Column(name = "visibility", nullable = false)
	private Boolean visibility = Boolean.TRUE;
	
	@Column(name = "default_coupon_code", length = 50)
	@JsonProperty("default_coupon_code")
	private String defaultCouponCode;
	
	@Column(name = "training_style", length = 255, columnDefinition = "varchar(255) default 'OFFLINE'")
	@JsonProperty("training_style")
	private String trainingStyle = "OFFLINE";

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOrder_type() {
		return order_type;
	}

	/**
	 * @param order_type
	 * order_type must be OrderType(PRODUCT, SUBSCRIPTION). Otherwise, the default SUBSCRIPTION value will be set
	 */
	public void setOrder_type(String order_type) {
		if(OrderType.PRODUCT.name().compareTo(order_type) == 0 || OrderType.SUBSCRIPTION.name().compareTo(order_type) == 0) {
			this.order_type = order_type;
		} else {
			this.order_type = OrderType.SUBSCRIPTION.name();
		}
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getOrdering() {
		return ordering;
	}

	public void setOrdering(Integer ordering) {
		this.ordering = ordering;
	}

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	public String getDescription_vn() {
		return description_vn;
	}

	public void setDescription_vn(String description_vn) {
		this.description_vn = description_vn;
	}

	public String getDescription_en() {
		return description_en;
	}

	public void setDescription_en(String description_en) {
		this.description_en = description_en;
	}

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	public String getDefaultCouponCode() {
		return defaultCouponCode;
	}

	public void setDefaultCouponCode(String defaultCouponCode) {
		this.defaultCouponCode = defaultCouponCode;
	}

	public String getTrainingStyle() {
		return trainingStyle;
	}

	public void setTrainingStyle(String trainingStyle) {
		this.trainingStyle = trainingStyle;
	}
}
