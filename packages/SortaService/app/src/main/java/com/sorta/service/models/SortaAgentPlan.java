package com.sorta.service.models;

import lombok.Data;

import java.util.List;

@Data
public class SortaAgentPlan {
    List<SortaItemPlan> itemPlans;
}
