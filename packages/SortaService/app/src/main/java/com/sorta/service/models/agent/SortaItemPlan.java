package com.sorta.service.models.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortaItemPlan {
    String itemId;
    String name;
    ImageCoordinates coordinates;
    SortaSuggestedAction suggestedAction;
    String suggestedLocation;
    String reason;
}
