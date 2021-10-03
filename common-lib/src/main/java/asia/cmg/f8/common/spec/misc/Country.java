package asia.cmg.f8.common.spec.misc;

public enum Country {

	VN("vn", "Viet Nam"),
	SG("sg", "Singapore"),
	HK("hk", "Hong Kong");
	
	private String code;
	private String name;
	
	Country(final String code, final String name){
		this.code = code;
		this.name = name;
	}
	
	public String getCode(){
		return code;
	}
	
	public String getName(){
		return name;
	}
}
