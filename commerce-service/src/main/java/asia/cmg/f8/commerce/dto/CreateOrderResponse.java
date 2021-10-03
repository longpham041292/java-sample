package asia.cmg.f8.commerce.dto;

public class CreateOrderResponse {

    private final String uuid;
    private final String url;

    public CreateOrderResponse(final String uuid, final String url) {
        super();
        this.uuid = uuid;
        this.url = url;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUrl() {
        return url;
    }

}
