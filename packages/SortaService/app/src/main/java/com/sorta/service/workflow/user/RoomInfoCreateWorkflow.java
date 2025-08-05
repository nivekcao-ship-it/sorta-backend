package com.sorta.service.workflow.user;

import com.sorta.service.dao.SpaceDao;
import com.sorta.service.models.db.Space;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.CreateUserProfileRequest;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class RoomInfoCreateWorkflow implements CreateUserProfileWorkflow {
    private final SpaceDao spaceDao;

    @Inject
    public RoomInfoCreateWorkflow(SpaceDao spaceDao) {
        this.spaceDao = spaceDao;
    }

    @Override
    public User run(final CreateUserProfileRequest request, final User user) {
        if (shouldRun(request)) {
            createSpaces(user.getUserId(), request);
        }
        return user;
    }

    @Override
    public Boolean shouldRun(final CreateUserProfileRequest request) {
        return request.getRoomInfo() != null && !request.getRoomInfo().isEmpty();
    }

    private void createSpaces(final String userId, final CreateUserProfileRequest request) {
        request.getRoomInfo().forEach(roomInfo -> {
            final Space space = Space.builder()
                    .userId(userId)
                    .spaceId(roomInfo.getId())
                    .spacePhotos(roomInfo.getImages())
                    .status("READY")
                    .build();
            spaceDao.saveSpace(space);
        });
    }
}