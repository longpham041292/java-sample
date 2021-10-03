package asia.cmg.f8.profile.partner.leep_web;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import asia.cmg.f8.profile.config.LeepWebConfiguration;
import asia.cmg.f8.profile.config.UserProfileProperties;
import asia.cmg.f8.profile.domain.entity.home.EContentType;
import asia.cmg.f8.profile.domain.entity.home.HomeSectionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventActionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventEntity;

public class LeepPostService {

	private LeepWebConnection dbConnection = null;
	private LeepWebConfiguration leepWebConfig = null;
	private UserProfileProperties profileProperties = null;
	
	private final String COL_ID = "ID";
	private final String COL_DATE = "post_date";
	private final String COL_TITLE = "post_title";
	private final String COL_STATUS = "post_status";	
	private final String COL_THUMBNAIL_LINK = "thumnail_link";	
	private final String COL_POST_LINK = "post_link";

	public LeepPostService(LeepWebConfiguration leepWebConfig, UserProfileProperties profileProperties) {
		this.leepWebConfig = leepWebConfig;
		dbConnection = new LeepWebConnection(leepWebConfig);
		this.profileProperties = profileProperties;
	}

	public List<LeepContentEntity> getPagedLeepEvents(final int page, final int size) {
		try {
			String query = "SELECT * FROM leep_events WHERE published = 1 ORDER BY ordering DESC";
			List<LeepContentEntity> events = new ArrayList<LeepContentEntity>();
			ResultSet resultSet = dbConnection.executeQuery(query, page, size);
			if(resultSet != null) {
				while (resultSet.next()) {
					LeepContentEntity entity = this.fetchResultSet(resultSet);
					events.add(entity);
				}
			}
			return events;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	public List<LeepContentEntity> getTopLeepPosts(int index, int limit) {
		try {
			String query = "SELECT p.ID, p.post_date, p.post_title, p.post_status, p.post_name, " +
							"      p.post_type, p.guid as post_link, p2.guid as thumnail_link " + 
							"FROM wp_posts p LEFT JOIN wp_postmeta pm ON p.ID = pm.post_id AND pm.meta_key = '_thumbnail_id' " + 
							"				 LEFT JOIN wp_posts p2 ON pm.meta_value = p2.ID " + 
							"WHERE p.post_type = 'post' and p.post_status = 'publish' " + 
							"ORDER BY p.post_date DESC " + 
							"LIMIT %s, %s";
			List<LeepContentEntity> events = new ArrayList<LeepContentEntity>();
			
			ResultSet resultSet = dbConnection.executeQuery(String.format(query, index, limit));
			if(resultSet != null) {
				while (resultSet.next()) {
					LeepContentEntity entity = this.fetchResultSet(resultSet);
					events.add(entity);
				}
			}
			return events;
		} catch (Exception e) {
			return Collections.emptyList();
		}
		finally {
			dbConnection.close();
		}
	}
	
	private LeepContentEntity fetchResultSet(ResultSet resultSet) throws Exception {
		try {
			LeepContentEntity entity = new LeepContentEntity();
			
			entity.id = resultSet.getInt(COL_ID);
			entity.title = resultSet.getString(COL_TITLE);
			entity.introText = resultSet.getString(COL_TITLE);
			entity.status = resultSet.getString(COL_STATUS);
			entity.published = 1;
			entity.order = resultSet.getInt(COL_ID);
			entity.thumbnail = resultSet.getString(COL_THUMBNAIL_LINK);
			entity.urls = resultSet.getString(COL_POST_LINK);
			entity.domain = leepWebConfig.getDomain();
			entity.contentType = ELeepContentType.EVENT;
			entity.created_date = resultSet.getDate(COL_DATE);
			entity.action_id = profileProperties.getConnectWalletActionId();
			
			return entity;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static TrendingEventEntity convert(HomeSectionEntity homeSection, LeepContentEntity leepContent, TrendingEventActionEntity trendingAction) {
		TrendingEventEntity trendingEntity = new TrendingEventEntity();
		
		trendingEntity.setContentType(EContentType.HTML.getText());
		trendingEntity.setCreatedDate(leepContent.created_date);
		trendingEntity.setId(leepContent.id);
		trendingEntity.setImage(leepContent.thumbnail);
		trendingEntity.setLanguage("vi");
		trendingEntity.setOrder(leepContent.order);
		trendingEntity.setSection(homeSection);
		trendingEntity.setShortContent(leepContent.introText);
		trendingEntity.setThumbnail(leepContent.thumbnail);
		trendingEntity.setTitle(leepContent.title);
		trendingEntity.setUrl(leepContent.urls);
		trendingEntity.setAction(trendingAction == null ? null : trendingAction);
		
		return trendingEntity;
	}
}
