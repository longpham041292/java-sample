package asia.cmg.f8.profile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "userprofile")
public class UserProfileProperties {
    private String userGridClientId;
    private String userGridClientSecret;
    private String elasticsearchHost;
    private Integer elasticsearchPort;

    @Value("${userprofile.elasticIndice}")
    private String elasticIndice;

    @Value("${userprofile.elasticType}")
    private String elasticType;

    private Questionaire questionaire = new Questionaire();

    private final ProfileMedia profileMedia = new ProfileMedia();

    private final Avro avro = new Avro();

    private final Contact contact = new Contact();
    
    private final User user = new User();

    private String dateTimeFormat;
    
    private final Database database = new Database();
    
    private Integer suggestTrainersLimit;
    
    private Long connectWalletActionId;
    
    public Long getConnectWalletActionId() {
		return connectWalletActionId;
	}

	public void setConnectWalletActionId(Long connectWalletActionId) {
		this.connectWalletActionId = connectWalletActionId;
	}

	public Integer getSuggestTrainersLimit() {
		return suggestTrainersLimit;
	}

	public void setSuggestTrainersLimit(Integer suggestTrainersLimit) {
		this.suggestTrainersLimit = suggestTrainersLimit;
	}

	public Database getDatabase() {
		return database;
	}

	public String getElasticsearchHost() {
        return elasticsearchHost;
    }

    public void setElasticsearchHost(final String elasticsearchHost) {
        this.elasticsearchHost = elasticsearchHost;
    }

    public Integer getElasticsearchPort() {
        return elasticsearchPort;
    }

    public void setElasticsearchPort(final Integer elasticsearchPort) {
        this.elasticsearchPort = elasticsearchPort;
    }

    public String getUserGridClientId() {
        return userGridClientId;
    }

    public void setUserGridClientId(final String userGridClientId) {
        this.userGridClientId = userGridClientId;
    }

    public String getUserGridClientSecret() {
        return userGridClientSecret;
    }

    public void setUserGridClientSecret(final String userGridClientSecret) {
        this.userGridClientSecret = userGridClientSecret;
    }

    @Bean
    public String getElasticIndice() {
        return elasticIndice;
    }

    public void setElasticIndice(final String elasticIndice) {
        this.elasticIndice = elasticIndice;
    }

    @Bean
    public String getElasticType() {
        return elasticType;
    }

    public void setElasticType(final String elasticType) {
        this.elasticType = elasticType;
    }

    public Questionaire getQuestionaire() {
        return questionaire;
    }

    public void setQuestionaire(final Questionaire questionaire) {
        this.questionaire = questionaire;
    }

    public Avro getAvro() {
        return avro;
    }

    public User getUser() {
		return user;
	}

	public Contact getContact() {
        return contact;
    }

    public ProfileMedia getProfileMedia() {
        return profileMedia;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(final String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public class Questionaire {
        private int questionLimit;
        private int answerLimit;

        public int getQuestionLimit() {
            return questionLimit;
        }

        public void setQuestionLimit(final int questionLimit) {
            this.questionLimit = questionLimit;
        }

        public int getAnswerLimit() {
            return answerLimit;
        }

        public void setAnswerLimit(final int answerLimit) {
            this.answerLimit = answerLimit;
        }

    }

    public static class Avro {
        private String storeFolder;

        public String getStoreFolder() {
            return storeFolder;
        }

        public void setStoreFolder(final String storeFolder) {
            this.storeFolder = storeFolder;
        }

    }

    public static class User {
    	private int sizeLoad;
        private int maxLoad;
		public int getSizeLoad() {
			return sizeLoad;
		}
		public void setSizeLoad(int sizeLoad) {
			this.sizeLoad = sizeLoad;
		}
		public int getMaxLoad() {
			return maxLoad;
		}
		public void setMaxLoad(int maxLoad) {
			this.maxLoad = maxLoad;
		}
    }
    
    public static class Contact {
        private int sizeLoad;
        private int maxLoad;

        public int getSizeLoad() {
            return sizeLoad;
        }

        public void setSizeLoad(final int sizeLoad) {
            this.sizeLoad = sizeLoad;
        }

        public int getMaxLoad() {
            return maxLoad;
        }

        public void setMaxLoad(final int maxLoad) {
            this.maxLoad = maxLoad;
        }

    }

    public static class ProfileMedia {
        private int cover;
        private int photo;
        private int client;
        private int video;
        private int caption;

        public int getCover() {
            return cover;
        }

        public int getPhoto() {
            return photo;
        }

        public int getClient() {
            return client;
        }

        public int getVideo() {
            return video;
        }

        public void setCover(final int cover) {
            this.cover = cover;
        }

        public void setPhoto(final int photo) {
            this.photo = photo;
        }

        public void setClient(final int client) {
            this.client = client;
        }

        public void setVideo(final int video) {
            this.video = video;
        }

        public int getCaption() {
            return caption;
        }

        public void setCaption(final int caption) {
            this.caption = caption;
        }
    }
    
    public static class Database {
        private boolean initDb;
        private String initDbSource;

        public String getInitDbSource() {
            return initDbSource;
        }

        public void setInitDbSource(final String initDbSource) {
            this.initDbSource = initDbSource;
        }

        public boolean isInitDb() {
            return initDb;
        }

        public void setInitDb(final boolean initDb) {
            this.initDb = initDb;
        }

    }
}
