package asia.cmg.f8.commerce.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name = "data_localized", uniqueConstraints = @UniqueConstraint(columnNames = {
        "entity_uuid", "localized_key", "lang_code"}))
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class LocalizedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_uuid", nullable = false)
    private String entityUuid;

    @Column(name = "localized_key", nullable = false)
    private String localizedKey;

    @Column(name = "localized_value", nullable = false)
    private String localizedValue;

    @Column(name = "lang_code", length = 2, nullable = false)
    private String langCode;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getEntityUuid() {
        return entityUuid;
    }

    public void setEntityUuid(final String entityUuid) {
        this.entityUuid = entityUuid;
    }

    public String getLocalizedKey() {
        return localizedKey;
    }

    public void setLocalizedKey(final String localizedKey) {
        this.localizedKey = localizedKey;
    }

    public String getLocalizedValue() {
        return localizedValue;
    }

    public void setLocalizedValue(final String localizedValue) {
        this.localizedValue = localizedValue;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(final String langCode) {
        this.langCode = langCode;
    }

}
