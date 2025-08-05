package com.sorta.service.dagger;

import com.google.common.collect.ImmutableList;
import com.sorta.service.dao.SpaceDao;
import com.sorta.service.processors.DescribeImageProcessor;
import com.sorta.service.workflow.user.AddRoomPhotosCreateWorkflow;
import com.sorta.service.workflow.user.AddRoomPhotosPatchWorkflow;
import com.sorta.service.workflow.user.CreateUserProfileWorkflow;
import com.sorta.service.workflow.user.PatchUserProfileWorkflow;
import com.sorta.service.workflow.user.RoomInfoProfileWorkflowPatch;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.List;

@Module
public class UserModule {

    @Provides
    @Singleton
    public List<PatchUserProfileWorkflow> providePatchUserWorkflows(final DescribeImageProcessor describeImageProcessor,
                                                                    final SpaceDao spaceDao) {
        return ImmutableList.of(
                new RoomInfoProfileWorkflowPatch(spaceDao),
                new AddRoomPhotosPatchWorkflow(describeImageProcessor, spaceDao)
        );
    }

    @Provides
    @Singleton
    public List<CreateUserProfileWorkflow> provideCreateUserWorkflows(final DescribeImageProcessor describeImageProcessor,
                                                                      final SpaceDao spaceDao) {
        return ImmutableList.of(
                new AddRoomPhotosCreateWorkflow(describeImageProcessor, spaceDao)
        );
    }
}
