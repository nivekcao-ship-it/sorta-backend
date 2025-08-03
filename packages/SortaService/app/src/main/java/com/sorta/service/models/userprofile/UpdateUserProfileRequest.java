package com.sorta.service.models.userprofile;

import lombok.Data;
import java.util.List;

@Data
public class UpdateUserProfileRequest {
    private String userId;
    private List<RoomInfo> roomInfo;
}