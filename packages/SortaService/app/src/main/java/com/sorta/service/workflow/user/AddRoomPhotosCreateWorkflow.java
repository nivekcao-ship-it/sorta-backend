package com.sorta.service.workflow.user;

import com.sorta.service.dao.SpaceDao;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.AddRoomInfoRequest;
import com.sorta.service.models.userprofile.CreateUserProfileRequest;
import com.sorta.service.processors.DescribeImageProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Log4j2
@Singleton
public class AddRoomPhotosCreateWorkflow extends BaseRoomPhotosWorkflow<CreateUserProfileRequest> implements CreateUserProfileWorkflow {

    @Inject
    public AddRoomPhotosCreateWorkflow(final DescribeImageProcessor describeImageProcessor,
                                       final SpaceDao spaceDao) {
        super(describeImageProcessor, spaceDao);
    }

    @Override
    protected List<AddRoomInfoRequest> getRoomInfo(CreateUserProfileRequest request) {
        return request.getRoomInfo();
    }

    @Override
    public User run(final CreateUserProfileRequest request, final User user) {
        if (shouldRun(request)) return processRoomPhotos(request, user);
        return user;
    }

    @Override
    public Boolean shouldRun(final CreateUserProfileRequest request) {
        return shouldProcess(request);
    }
}