package com.sorta.service.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageCoordinates {
    double x;
    double y;
    double width;
    double height;
}
