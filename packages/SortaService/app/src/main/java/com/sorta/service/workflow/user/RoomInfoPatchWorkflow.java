package com.sorta.service.workflow.user;

import com.sorta.service.dao.SpaceDao;
import com.sorta.service.models.db.Space;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.AddRoomInfoRequest;
import com.sorta.service.models.userprofile.PatchUserProfileRequest;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Singleton
public class RoomInfoPatchWorkflow implements PatchUserProfileWorkflow {
    private final SpaceDao spaceDao;

    @Inject
    public RoomInfoPatchWorkflow(SpaceDao spaceDao) {
        this.spaceDao = spaceDao;
    }

    @Override
    public User run(final PatchUserProfileRequest request, final User user) {
        if (shouldRun(request)) {
            patchSpaces(user.getUserId(), request.getRoomInfo());
            updateUserRooms(user, request.getRoomInfo());
        }

        return user;
    }

    @Override
    public Boolean shouldRun(final PatchUserProfileRequest request) {
        return request.getRoomInfo() != null;
    }

    private void patchSpaces(final String userId, final List<AddRoomInfoRequest> updates) {
        for (AddRoomInfoRequest update : updates) {
            final Optional<Space> existingSpace = spaceDao.getSpace(userId, update.getId());
            
            if (existingSpace.isPresent() && update.getImages() != null) {
                final Space space = existingSpace.get();
                final Set<String> mergedImages = new LinkedHashSet<>(space.getSpacePhotos() != null ? space.getSpacePhotos() : List.of());
                mergedImages.addAll(update.getImages());
                space.setSpacePhotos(new ArrayList<>(mergedImages));
                spaceDao.saveSpace(space);
            } else if (update.getImages() != null) {
                final Space newSpace = Space.builder()
                        .userId(userId)
                        .spaceId(update.getId())
                        .spacePhotos(update.getImages())
                        .status("READY")
                        .build();
                spaceDao.saveSpace(newSpace);
            }
        }
    }

    private void updateUserRooms(final User user, final List<AddRoomInfoRequest> updates) {
        final Set<String> roomIds = new LinkedHashSet<>(user.getRooms() != null ? user.getRooms() : List.of());
        roomIds.addAll(updates.stream().map(AddRoomInfoRequest::getId).collect(Collectors.toSet()));
        user.setRooms(new ArrayList<>(roomIds));
    }
}
