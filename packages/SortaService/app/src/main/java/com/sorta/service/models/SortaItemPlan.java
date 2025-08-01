package com.sorta.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class SortaItemPlan {
    double x;
    double y;
    double width;
    double height;
}
