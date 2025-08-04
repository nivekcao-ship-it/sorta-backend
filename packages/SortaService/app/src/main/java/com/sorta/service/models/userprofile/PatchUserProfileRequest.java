package com.sorta.service.models.userprofile;

import lombok.Data;
import java.util.List;

@Data
public class PatchUserProfileRequest {
    private String userId;
    private List<UpdateRoomInfoRequest> roomInfo;

    @Data
    public static class UpdateRoomInfoRequest {
        private String id;
        private List<String> images;
    }
}
