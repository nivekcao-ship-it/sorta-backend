package com.sorta.service.workflow.patchuser;

import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.PatchUserProfileRequest;
import com.sorta.service.models.userprofile.RoomInfo;
import lombok.extern.log4j.Log4j2;

import javax.inject.Singleton;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Singleton
public class PatchRoomInfoWorkflow implements PatchUserWorkflow {

    @Override
    public User run(final PatchUserProfileRequest request, final User user) {
        if (shouldRun(request)) {
            user.setRoomInfo(patchRoomInfo(user.getRoomInfo(), request.getRoomInfo()));
        }

        return user;
    }

    @Override
    public Boolean shouldRun(final PatchUserProfileRequest request) {
        return request.getRoomInfo() != null;
    }

    private List<RoomInfo> patchRoomInfo(List<RoomInfo> existing, List<PatchUserProfileRequest.UpdateRoomInfoRequest> updates) {
        Map<String, RoomInfo> roomMap = existing != null ?
                existing.stream().collect(Collectors.toMap(RoomInfo::getId, Function.identity())) :
                new HashMap<>();

        for (PatchUserProfileRequest.UpdateRoomInfoRequest update : updates) {
            RoomInfo existingRoom = roomMap.get(update.getId());
            if (existingRoom != null && update.getImages() != null) {
                Set<String> mergedImages = new LinkedHashSet<>(existingRoom.getImage());
                mergedImages.addAll(update.getImages());
                existingRoom.setImage(new ArrayList<>(mergedImages));
                roomMap.put(update.getId(), existingRoom);
            } else if (update.getImages() != null) {
                roomMap.put(update.getId(), RoomInfo.builder()
                        .id(update.getId())
                        .image(update.getImages())
                        .build());
            }
        }

        return new ArrayList<>(roomMap.values());
    }
}
