package com.sorta.service.models.userprofile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatchUserProfileResponse {
    private String userId;
}
