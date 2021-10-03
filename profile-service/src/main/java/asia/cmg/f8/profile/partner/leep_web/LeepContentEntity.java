package asia.cmg.f8.profile.partner.leep_web;

import java.io.Serializable;
import java.util.Date;

public class LeepContentEntity implements Serializable {

	public static final long serialVersionUID = 1L;
	public int id;
	public String title;
	public String alias;
	public String introText;
	public String fullText;
	public int published;
	public String status;
	public int order;
	public int featured;
	public String thumbnail;
	public String urls;
	public ELeepContentType contentType;
	public Date created_date;
	public String domain;
	public Long action_id;
}
