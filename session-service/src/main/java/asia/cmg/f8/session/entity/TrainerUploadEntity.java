package asia.cmg.f8.session.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "trainer_upload_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TrainerUploadEntity {

	
	 private static final long serialVersionUID = 1L;

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    private String club;
	    
	    @Column(name = "trainer_code")
	    private String trainerCode;
		
	    @Column(name = "trainer_nick_name")
	    private String trainerNickName;
		
	    @Column(name = "trainer_name")
	    private String trainerName;
	    
	    @Column(name = "user_code_checked")
	    private Boolean userCodechecked;
	    
	    @Column(name = "trainer_level")
	    private String trainerLevel; 
		
	    @Column(name = "leep_level")
	    private String leepLevel;

		public Long getId() {
			return id;
		}

		public void setId(final Long id) {
			this.id = id;
		}

		public String getTrainerCode() {
			return trainerCode;
		}

		public void setTrainerCode(final String trainerCode) {
			this.trainerCode = trainerCode;
		}

		public String getTrainerNickName() {
			return trainerNickName;
		}

		public void setTrainerNickName(final String trainerNickName) {
			this.trainerNickName = trainerNickName;
		}

		public String getTrainerName() {
			return trainerName;
		}

		public void setTrainerName(final String trainerName) {
			this.trainerName = trainerName;
		}

		public Boolean getUserCodechecked() {
			return userCodechecked;
		}

		public void setUserCodechecked(final Boolean userCodechecked) {
			this.userCodechecked = userCodechecked;
		}

		public String getTrainerLevel() {
			return trainerLevel;
		}

		public void setTrainerLevel(final String trainerLevel) {
			this.trainerLevel = trainerLevel;
		}

		public String getLeepLevel() {
			return leepLevel;
		}

		public void setLeepLevel(final String leepLevel) {
			this.leepLevel = leepLevel;
		}

		public String getClub() {
			return club;
		}

		public void setClub(final String club) {
			this.club = club;
		}
	    
	    
	    
}
