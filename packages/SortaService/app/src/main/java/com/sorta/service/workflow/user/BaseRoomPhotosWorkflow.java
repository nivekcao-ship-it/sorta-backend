package com.sorta.service.workflow.user;

import com.sorta.service.dao.SpaceDao;
import com.sorta.service.exceptions.InternalServerException;
import com.sorta.service.models.db.Space;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.AddRoomInfoRequest;
import com.sorta.service.processors.DescribeImageProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Log4j2
public abstract class BaseRoomPhotosWorkflow<T> {
    protected final DescribeImageProcessor describeImageProcessor;
    protected final SpaceDao spaceDao;

    public BaseRoomPhotosWorkflow(final DescribeImageProcessor describeImageProcessor,
                                  final SpaceDao spaceDao) {
        this.describeImageProcessor = describeImageProcessor;
        this.spaceDao = spaceDao;
    }

    protected abstract List<AddRoomInfoRequest> getRoomInfo(T request);

    public User processRoomPhotos(final T request, final User user) {
        List<AddRoomInfoRequest> roomInfoList = getRoomInfo(request);
        if (roomInfoList == null) return user;

        for (final AddRoomInfoRequest roomInfo : roomInfoList) {
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

    public Boolean shouldProcess(final T request) {
        List<AddRoomInfoRequest> roomInfoList = getRoomInfo(request);
        return roomInfoList != null && 
               roomInfoList.stream()
                   .anyMatch(room -> room.getImages() != null && !room.getImages().isEmpty());
    }

    private void updateSpaceDescription(String userId, String spaceId, String description, List<String> photos) {
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