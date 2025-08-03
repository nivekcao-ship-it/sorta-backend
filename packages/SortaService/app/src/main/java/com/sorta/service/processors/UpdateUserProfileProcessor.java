package com.sorta.service.processors;

import com.sorta.service.dao.UserDao;
import com.sorta.service.exceptions.NotFoundException;
import com.sorta.service.models.userprofile.UpdateUserProfileRequest;
import com.sorta.service.models.userprofile.UpdateUserProfileResponse;
import com.sorta.service.models.db.User;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class UpdateUserProfileProcessor {
    private final UserDao userDao;

    @Inject
    public UpdateUserProfileProcessor(UserDao userDao) {
        this.userDao = userDao;
    }

    public UpdateUserProfileResponse updateUserProfile(final UpdateUserProfileRequest request) {
        final User existingUser = userDao.getUser(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.getUserId()));

        existingUser.setRoomInfo(request.getRoomInfo());
        userDao.updateUser(existingUser);

        final UpdateUserProfileResponse response = new UpdateUserProfileResponse();
        response.setUserId(request.getUserId());
        return response;
    }
}