package com.sorta.service.workflow.user;

import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.PatchUserProfileRequest;
import com.sorta.service.workflow.Workflow;

public interface PatchUserProfileWorkflow extends Workflow<PatchUserProfileRequest, User, User> {
}