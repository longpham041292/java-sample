package asia.cmg.f8.profile.partner.leep_web;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import asia.cmg.f8.profile.config.LeepWebConfiguration;
import asia.cmg.f8.profile.domain.entity.home.EContentType;
import asia.cmg.f8.profile.domain.entity.home.HomeSectionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventActionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventEntity;

public class LeepNewsService {

	private LeepWebConnection dbConnection = null;
	private LeepWebConfiguration leepWebConfig = null;
	
	private final String COL_ID = "id";
	private final String COL_TITLE = "title";
	private final String COL_ALIAS = "alias";	
	private final String COL_INTROTEXT = "introtext";
	private final String COL_FULLTEXT = "fulltext";
	private final String COL_PUBLISHED = "published";
	private final String COL_ORDER = "ordering";
	private final String COL_FEATURED = "featured";
	private final String COL_THUMBNAIL = "thumbnail";	
	private final String COL_URLS = "urls";
	private final String COL_CREATED_DATE = "created_date";
	private final String COL_ACTION_ID = "action_id";
	
	public LeepNewsService(LeepWebConfiguration leepWebConfig) {
		this.leepWebConfig = leepWebConfig;
		dbConnection = new LeepWebConnection(leepWebConfig);
	}
	
	public List<LeepContentEntity> getPagedLeepNews(final int page, final int size) {
		try {
			String query = "SELECT * FROM leep_content WHERE published = 1 ORDER BY ordering DESC";
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
	
	public List<LeepContentEntity> getTopLeepContent(final int index, final int limit) {
		try {
			String query = String.format("SELECT * FROM leep_content WHERE published = 1 ORDER BY ordering DESC LIMIT %s, %s", index, limit);
			List<LeepContentEntity> events = new ArrayList<LeepContentEntity>();
			
			ResultSet resultSet = dbConnection.executeQuery(query);
			if(resultSet != null) {
				if(resultSet != null) {
					while (resultSet.next()) {
						LeepContentEntity entity = this.fetchResultSet(resultSet);
						events.add(entity);
					}
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
			entity.alias = resultSet.getString(COL_ALIAS);
			entity.introText = resultSet.getString(COL_INTROTEXT);
			entity.fullText = resultSet.getString(COL_FULLTEXT);
			entity.published = resultSet.getInt(COL_PUBLISHED);
			entity.order = resultSet.getInt(COL_ORDER);
			entity.featured = resultSet.getInt(COL_FEATURED);
			entity.thumbnail = resultSet.getString(COL_THUMBNAIL);
			entity.urls = resultSet.getString(COL_URLS);
			entity.domain = leepWebConfig.getDomain();
			entity.contentType = ELeepContentType.EVENT;
			entity.created_date = resultSet.getDate(COL_CREATED_DATE);
			entity.action_id = resultSet.getLong(COL_ACTION_ID);
			
			return entity;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static TrendingEventEntity convert(HomeSectionEntity homeSection, LeepContentEntity leepContent) {
		TrendingEventEntity trendingEntity = new TrendingEventEntity();
		
		trendingEntity.setContent(leepContent.fullText);
		trendingEntity.setContentType(EContentType.HTML.getText());
		trendingEntity.setCreatedDate(leepContent.created_date);
		trendingEntity.setAction(new TrendingEventActionEntity());
		trendingEntity.setId(leepContent.id);
		trendingEntity.setImage(leepContent.thumbnail);
		trendingEntity.setLanguage("vi");
		trendingEntity.setOrder(leepContent.order);
		trendingEntity.setSection(homeSection);
		trendingEntity.setShortContent(leepContent.introText);
		trendingEntity.setThumbnail(leepContent.thumbnail);
		trendingEntity.setTitle(leepContent.title);
		trendingEntity.setUrl(leepContent.domain.concat(leepContent.urls));
		
		return trendingEntity;
	}
}
