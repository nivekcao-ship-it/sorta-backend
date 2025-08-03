package com.sorta.service.models.userprofile;

import lombok.Data;
import java.util.List;

@Data
public class RoomInfo {
    private String name;
    private List<String> image;
}