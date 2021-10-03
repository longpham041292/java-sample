package asia.cmg.f8.profile.api.document;

/**
 * Created by tri.bui on 10/25/16.
 */
public class SubmitDocumentRequest {

    private String path; // Path of asset
    private String documentName;
    private String category;

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(final String documentName) {
        this.documentName = documentName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }
}
