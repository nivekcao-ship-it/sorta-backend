package com.sorta.service.models.agent;

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
