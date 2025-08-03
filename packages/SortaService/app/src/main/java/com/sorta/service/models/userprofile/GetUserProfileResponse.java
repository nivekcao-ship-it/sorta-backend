package com.sorta.service.models.userprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserProfileResponse {
    private String userId;
    private String name;
    private Map<String, RoomInfo> roomInfos;
}
