package asia.cmg.f8.profile.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;


@Entity
@Table(name = "whos_hot_stats_daily")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SuppressWarnings("squid:S2367")
public class WhosHotEntity {

	private Long id;
	private String ptUuid;
	private int numberOfLikes;
	private int numberOfPosts;
	private int numberOfSessionBurned;
	private int numberOfClients;
	private LocalDateTime updatedDate;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@NotNull
	@Column(name = "pt_uuid", nullable = false)
	public String getPtUuid() {
		return ptUuid;
	}

	public void setPtUuid(final String ptUuid) {
		this.ptUuid = ptUuid;
	}

	@NotNull
	@Column(name = "likes_number", nullable = false)
	public int getNumberOfLikes() {
		return numberOfLikes;
	}

	public void setNumberOfLikes(final int numberOfLikes) {
		this.numberOfLikes = numberOfLikes;
	}

	@NotNull
	@Column(name = "posts_number", nullable = false)
	public int getNumberOfPosts() {
		return numberOfPosts;
	}

	public void setNumberOfPosts(final int numberOfPosts) {
		this.numberOfPosts = numberOfPosts;
	}

	@NotNull
	@Column(name = "sessions_burned_number", nullable = false)
	public int getNumberOfSessionBurned() {
		return numberOfSessionBurned;
	}

	public void setNumberOfSessionBurned(final int numberOfSessionBurned) {
		this.numberOfSessionBurned = numberOfSessionBurned;
	}

	@NotNull
	@Column(name = "clients_number", nullable = false)
	public int getNumberOfClients() {
		return numberOfClients;
	}

	public void setNumberOfClients(final int numberOfClients) {
		this.numberOfClients = numberOfClients;
	}

	@CreationTimestamp
    @Column(name = "updated_date")
	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(final LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}
}
