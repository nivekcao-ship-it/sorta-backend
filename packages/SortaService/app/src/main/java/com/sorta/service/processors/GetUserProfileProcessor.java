package com.sorta.service.processors;

import com.sorta.service.dao.UserDao;
import com.sorta.service.exceptions.NotFoundException;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.UserProfileResponse;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Log4j2
public class GetUserProfileProcessor {
    private final UserDao userDao;

    @Inject
    public GetUserProfileProcessor(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserProfileResponse getUserProfile(final String userId) {
        final User user = userDao.getUser(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .spaces(user.getSpaces())
                .build();
    }
}