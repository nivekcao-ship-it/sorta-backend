package com.sorta.service.processors;

import com.sorta.service.dao.UserDao;
import com.sorta.service.exceptions.BadRequestException;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.CreateUserProfileRequest;
import com.sorta.service.models.userprofile.CreateUserProfileResponse;
import com.sorta.service.models.userprofile.RoomInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sorta.service.workflow.user.CreateUserProfileWorkflow;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class CreateUserProfileProcessor {
    private final UserDao userDao;
    private final List<CreateUserProfileWorkflow> workflows;

    @Inject
    public CreateUserProfileProcessor(final UserDao userDao,
                                      final List<CreateUserProfileWorkflow> workflows) {
        this.userDao = userDao;
        this.workflows = workflows;
    }

    public CreateUserProfileResponse createUserProfile(final CreateUserProfileRequest request) {
        if (userDao.getUser(request.getUserId()).isPresent()) {
            throw new BadRequestException("User already exists: " + request.getUserId());
        }

        User user = User.builder()
                .userId(request.getUserId())
                .roomInfo(Optional.ofNullable(request.getRoomInfo())
                    .map(roomInfoList -> roomInfoList.stream()
                        .map(r -> RoomInfo.builder()
                            .id(r.getId())
                            .image(r.getImages())
                            .build())
                        .collect(Collectors.toList()))
                    .orElse(null))
                .build();

        for (CreateUserProfileWorkflow workflow : workflows) {
            user = workflow.run(request, user);
        }

        userDao.saveUser(user);

        return CreateUserProfileResponse.builder()
                .userId(request.getUserId())
                .build();
    }
}