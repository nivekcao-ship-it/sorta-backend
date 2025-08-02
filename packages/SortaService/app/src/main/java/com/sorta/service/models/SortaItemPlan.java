package com.sorta.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class SortaItemPlan {
    String itemId;
    String name;
    ImageCoordinates coordinates;
    SortaSuggestedAction suggestedAction;
    String suggestedLocation;
    String reason;
}
