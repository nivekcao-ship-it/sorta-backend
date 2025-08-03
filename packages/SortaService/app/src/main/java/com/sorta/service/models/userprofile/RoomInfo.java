package com.sorta.service.models.userprofile;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RoomInfo {
    private String id;
    private RoomStatus status;
    private List<String> image;

    public enum RoomStatus {
        READY,
        UPDATING
    }
}
