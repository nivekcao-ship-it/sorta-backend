package com.sorta.service.models.userprofile;

import lombok.Data;
import java.util.List;

@Data
public class AddRoomInfoRequest {
    private String id;
    private List<String> images;
}