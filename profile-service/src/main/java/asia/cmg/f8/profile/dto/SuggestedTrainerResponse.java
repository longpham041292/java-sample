package asia.cmg.f8.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class SuggestedTrainerResponse {
    @JsonProperty("status")
    private Map<String, Object> status;

    @JsonProperty("data")
    private List<SuggestedTrainersDTO> data;

    public Map<String, Object> getStatus() {
        return status;
    }

    public void setStatus(Map<String, Object> status) {
        this.status = status;
    }

    public List<SuggestedTrainersDTO> getData() {
        return data;
    }

    public void setData(List<SuggestedTrainersDTO> data) {
        this.data = data;
    }
}
