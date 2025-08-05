package com.sorta.service.workflow.user;

import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.CreateUserProfileRequest;
import com.sorta.service.workflow.Workflow;

public interface CreateUserProfileWorkflow extends Workflow<CreateUserProfileRequest, User, User> {
}