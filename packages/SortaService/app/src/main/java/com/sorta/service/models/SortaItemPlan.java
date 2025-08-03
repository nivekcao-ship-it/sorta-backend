package com.sorta.service.models;

import lombok.Data;

@Data
public class SortaItemPlan {
    String itemId;
    String name;
    ImageCoordinates coordinates;
    SortaSuggestedAction suggestedAction;
    String suggestedLocation;
    String reason;
}
