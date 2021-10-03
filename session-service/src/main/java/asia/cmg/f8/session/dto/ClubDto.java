package asia.cmg.f8.session.dto;

public class ClubDto {

	private String uuid;
	private String name;
	private String address;
	
	private ClubDto() {
	}
	
	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public static class Builder {
		
		private String uuid;
		private String name;
		private String address;
		
		public Builder uuid(String clubUuid) {
			this.uuid = clubUuid;
			return this;
		}
		
		public Builder name(String clubName) {
			this.name = clubName;
			return this;
		}
		
		public Builder address(String clubAddress) {
			this.address = clubAddress;
			return this;
		}
		
		public ClubDto build() {
			ClubDto club = new ClubDto();
			club.address = this.address;
			club.name = this.name;
			club.uuid = this.uuid;
			
			return club;
		}
	}
}
