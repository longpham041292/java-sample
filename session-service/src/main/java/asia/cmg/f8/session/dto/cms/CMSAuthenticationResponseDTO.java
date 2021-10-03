package asia.cmg.f8.session.dto.cms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.client.cms.CmsClientConfig;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CMSAuthenticationResponseDTO {

	private Status status;
	
	private Data data;
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public class Status {
		private int code;
		private String error;
		private String detail;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getError() {
			return error;
		}
		public void setError(String error) {
			this.error = error;
		}
		public String getDetail() {
			return detail;
		}
		public void setDetail(String detail) {
			this.detail = detail;
		}
	};
	
	public class Data {
		private int id = 0;
		
		private String name;
		
		@JsonProperty("auth_token")
		private String authToken = "";
		
		@JsonProperty("exp")
		private long expiration = 0;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAuthToken() {
			return authToken;
		}

		public void setAuthToken(String authToken) {
			this.authToken = authToken;
		}

		public long getExpiration() {
			return expiration;
		}

		public void setExpiration(long expiration) {
			this.expiration = expiration;
		}
	}
}
