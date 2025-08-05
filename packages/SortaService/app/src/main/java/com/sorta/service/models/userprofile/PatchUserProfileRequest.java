package com.sorta.service.models.userprofile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatchUserProfileRequest {
    @JsonProperty(required = true)
    private String userId;
    private List<UpdateRoomInfoRequest> roomInfo;

    @Data
    public static class UpdateRoomInfoRequest {
        private String id;
        private List<String> images;
    }
}
