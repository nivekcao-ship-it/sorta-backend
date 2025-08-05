package com.sorta.service.processors;

import com.sorta.service.dao.SpaceDao;
import com.sorta.service.dao.UserDao;
import com.sorta.service.exceptions.NotFoundException;
import com.sorta.service.models.db.Space;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.RoomInfo;
import com.sorta.service.models.userprofile.GetUserProfileResponse;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
@Log4j2
public class GetUserProfileProcessor {
    private final UserDao userDao;
    private final SpaceDao spaceDao;

    @Inject
    public GetUserProfileProcessor(UserDao userDao, SpaceDao spaceDao) {
        this.userDao = userDao;
        this.spaceDao = spaceDao;
    }

    public GetUserProfileResponse getUserProfile(final String userId) {
        final User user = userDao.getUser(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        final Map<String, RoomInfo> roomInfos = user.getRooms() != null ?
                spaceDao.getSpacesByIds(userId, user.getRooms())
                        .stream()
                        .map(this::toRoomInfo)
                        .collect(Collectors.toMap(RoomInfo::getId, Function.identity())) : Map.of();


        return GetUserProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .roomInfos(roomInfos)
                .build();
    }

    private RoomInfo toRoomInfo(final Space space) {
        return RoomInfo.builder()
                .id(space.getSpaceId())
                .image(space.getSpacePhotos())
                .status(RoomInfo.RoomStatus.valueOf(space.getStatus()))
                .build();
    }
}
