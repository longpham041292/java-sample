package asia.cmg.f8.profile.api.document;


import asia.cmg.f8.profile.domain.entity.DocumentEntity;

/**
 * Created by tri.bui on 10/25/16.
 */
public class DocumentResponse extends SubmitDocumentRequest {

    private String uuid;
    private String owner;
    private Long created;
    private String displayCreated;

    public DocumentResponse(final DocumentEntity entity) {
        this.uuid = entity.getUuid();
        this.owner = entity.getOwner();
        this.created = entity.getCreated();
        this.displayCreated = entity.getDisplayCreated();
        setPath(entity.getPath());
        setDocumentName(entity.getDocumentName());
        setCategory(entity.getCategory());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public Long getCreated() { return created; }

    public void setCreated(final Long created) { this.created = created; }

    public String getDisplayCreated() { return displayCreated; }

    public void setDisplayCreated(final String displayCreated) { this.displayCreated = displayCreated; }
}
