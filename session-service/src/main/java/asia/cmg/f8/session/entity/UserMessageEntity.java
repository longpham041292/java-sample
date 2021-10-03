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
@Table(name = "user_message_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserMessageEntity {

	
	 private static final long serialVersionUID = 1L;

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @Column(name = "lang_code", nullable = false) 
	    private String languageCode;

	    @Column(name = "message", nullable = false)
	    private String message;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getLanguageCode() {
			return languageCode;
		}

		public void setLanguageCode(final String languageCode) {
			this.languageCode = languageCode;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(final String message) {
			this.message = message;
		}
	    
	    
	    
	
}
