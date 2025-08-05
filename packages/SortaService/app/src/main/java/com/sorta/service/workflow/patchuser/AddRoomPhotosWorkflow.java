package com.sorta.service.workflow.patchuser;

import com.sorta.service.dao.SpaceDao;
import com.sorta.service.exceptions.InternalServerException;
import com.sorta.service.models.db.Space;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.PatchUserProfileRequest;
import com.sorta.service.processors.DescribeImageProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Log4j2
@Singleton
public class AddRoomPhotosWorkflow implements PatchUserWorkflow {
    private final DescribeImageProcessor describeImageProcessor;
    private final SpaceDao spaceDao;

    @Inject
    public AddRoomPhotosWorkflow(final DescribeImageProcessor describeImageProcessor,
                                 final SpaceDao spaceDao) {
        this.describeImageProcessor = describeImageProcessor;
        this.spaceDao = spaceDao;
    }

    @Override
    public User run(final PatchUserProfileRequest request, final User user) {
        if (!shouldRun(request)) return user;
        for (final PatchUserProfileRequest.UpdateRoomInfoRequest roomInfo : request.getRoomInfo()) {
            if (roomInfo.getImages() != null && !roomInfo.getImages().isEmpty()) {
                try {
                    final String description = describeImageProcessor.describeImage(roomInfo.getImages());
                    updateSpaceDescription(user.getUserId(), roomInfo.getId(), description, roomInfo.getImages());
                } catch (Exception e) {
                    log.error("Failed to describe images for room {}: {}", roomInfo.getId(), e.getMessage(), e);
                    throw new InternalServerException("Failed to describe images for room");
                }
            }
        }
        return user;
    }

    @Override
    public Boolean shouldRun(final PatchUserProfileRequest request) {
        return request.getRoomInfo() != null && 
               request.getRoomInfo().stream()
                   .anyMatch(room -> room.getImages() != null && !room.getImages().isEmpty());
    }

    private void updateSpaceDescription(String userId, String spaceId, String description, java.util.List<String> photos) {
        Optional<Space> existingSpace = spaceDao.getSpace(userId, spaceId);
        
        Space space = existingSpace.orElse(Space.builder()
                .userId(userId)
                .spaceId(spaceId)
                .build());
        
        space.setDescription(description);
        space.setSpacePhotos(photos);
        spaceDao.saveSpace(space);
        
        log.info("Updated space {} description for user {}", spaceId, userId);
    }
}
