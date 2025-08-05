package com.sorta.service.workflow.user;

import com.sorta.service.dao.SpaceDao;
import com.sorta.service.models.db.User;
import com.sorta.service.models.userprofile.AddRoomInfoRequest;
import com.sorta.service.models.userprofile.PatchUserProfileRequest;
import com.sorta.service.processors.DescribeImageProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Log4j2
@Singleton
public class AddRoomPhotosPatchWorkflow extends BaseRoomPhotosWorkflow<PatchUserProfileRequest> implements PatchUserProfileWorkflow {

    @Inject
    public AddRoomPhotosPatchWorkflow(final DescribeImageProcessor describeImageProcessor,
                                      final SpaceDao spaceDao) {
        super(describeImageProcessor, spaceDao);
    }

    @Override
    protected List<AddRoomInfoRequest> getRoomInfo(PatchUserProfileRequest request) {
        return request.getRoomInfo();
    }

    @Override
    public User run(final PatchUserProfileRequest request, final User user) {
        if (shouldRun(request)) return processRoomPhotos(request, user);
        return user;
    }

    @Override
    public Boolean shouldRun(final PatchUserProfileRequest request) {
        return shouldProcess(request);
    }
}
