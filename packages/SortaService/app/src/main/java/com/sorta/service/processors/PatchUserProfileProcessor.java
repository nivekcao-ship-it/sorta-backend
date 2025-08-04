package com.sorta.service.processors;

import com.sorta.service.dao.UserDao;
import com.sorta.service.exceptions.NotFoundException;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.PatchUserProfileRequest;
import com.sorta.service.models.userprofile.PatchUserProfileResponse;
import com.sorta.service.workflow.patchuser.PatchUserWorkflow;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Log4j2
@Singleton
public class PatchUserProfileProcessor {
    private final UserDao userDao;
    private final List<PatchUserWorkflow> workflows;

    @Inject
    public PatchUserProfileProcessor(final UserDao userDao,
                                     final List<PatchUserWorkflow> workflows) {
        this.userDao = userDao;
        this.workflows = workflows;
    }

    public PatchUserProfileResponse updateUserProfile(final PatchUserProfileRequest request) {
        User user = userDao.getUser(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.getUserId()));

        for (PatchUserWorkflow workflow : workflows) {
            if (workflow.shouldRun(request)) {
                user = workflow.run(request, user);
            }
        }
        
        userDao.updateUser(user);

        return PatchUserProfileResponse.builder()
                .userId(request.getUserId())
                .build();
    }
}
