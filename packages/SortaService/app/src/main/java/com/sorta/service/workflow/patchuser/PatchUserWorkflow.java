package com.sorta.service.workflow.patchuser;

import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.PatchUserProfileRequest;
import com.sorta.service.workflow.Workflow;

public interface PatchUserWorkflow extends Workflow<PatchUserProfileRequest, User, User> {
}