package com.sorta.service.models.userprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfo {
    private String id;
    private RoomStatus status;
    private List<String> image;

    public enum RoomStatus {
        READY,
        UPDATING
    }
}
