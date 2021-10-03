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
@Table(name = "members_upload_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MemberUploadEntity  {

	
	  private static final long serialVersionUID = 1L;

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    private String club;
		
	    @Column(name = "member_barcode", nullable = false)
	    private String memberBarcode;
		
	    @Column(name = "first_name")
	    private String firstName;
		
	    @Column(name = "last_name")
	    private String lastName;
		
	    private String mobile;
	    
	    @Column(name = "user_code_checked")
	    private Boolean userCodechecked;
	    
	   public Long getId() {
			return id;
		}

		public void setId(final Long id) {
			this.id = id;
		}

		public String getClub() {
			return club;
		}

		public void setClub(final String club) {
			this.club = club;
		}

		public String getMemberBarcode() {
			return memberBarcode;
		}

		public void setMemberBarcode(final String memberBarcode) {
			this.memberBarcode = memberBarcode;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(final String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(final String lastName) {
			this.lastName = lastName;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(final String mobile) {
			this.mobile = mobile;
		}

		public Boolean getUserCodechecked() {
			return userCodechecked;
		}

		public void setUserCodechecked(final Boolean userCodechecked) {
			this.userCodechecked = userCodechecked;
		}

	
	   }
