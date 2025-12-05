package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.mapper.*;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class TriggerJobTest {
    @Mock
    private XxlJobGroupMapper xxlJobGroupMapper;
    @Mock
    private XxlJobInfoMapper xxlJobInfoMapper;
    @Mock
    public XxlJobLogMapper xxlJobLogMapper;
    @Mock
    private XxlJobLogGlueMapper xxlJobLogGlueMapper;
    @Mock
    private XxlJobLogReportMapper xxlJobLogReportMapper;
    @InjectMocks
    private XxlJobServiceImpl xxlJobService;

    private static final XxlJobInfo XXL_JOB_INFO = new XxlJobInfo();

    static {
        XXL_JOB_INFO.setId(1);
        XXL_JOB_INFO.setJobGroup(1);
        XXL_JOB_INFO.setJobDesc("jobDesc");
        XXL_JOB_INFO.setAddTime(new Date());
        XXL_JOB_INFO.setUpdateTime(new Date());
        XXL_JOB_INFO.setAuthor("author");
        XXL_JOB_INFO.setAlarmEmail("alarmEmail");
        XXL_JOB_INFO.setScheduleType("scheduleType");
        XXL_JOB_INFO.setScheduleConf("scheduleConf");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(xxlJobGroupMapper, xxlJobInfoMapper, xxlJobLogMapper, xxlJobLogGlueMapper, xxlJobLogReportMapper);
    }

    @Test
    void triggerWhenJobNotExists() {
        Mockito.when(xxlJobInfoMapper.loadById(Mockito.anyInt())).thenReturn(null);
        try (MockedStatic<I18nUtil> mockedStatic = Mockito.mockStatic(I18nUtil.class)) {
            mockedStatic.when(() -> I18nUtil.getString(Mockito.anyString())).then(invocation -> invocation.getArgument(0));
            Response<String> result = xxlJobService.trigger(null, 1, null, null);
            Assertions.assertFalse(result.isSuccess());
            Assertions.assertEquals("jobinfo_glue_jobid_unvalid", result.getMsg());
        }
    }

    @Test
    void testJobGroupPermission() {
        LoginInfo hasPermission = new LoginInfo();
        hasPermission.setExtraInfo(Map.of("jobGroups", "1,2"));
        Assertions.assertTrue(JobGroupPermissionUtil.hasJobGroupPermission(hasPermission, 1));
        Assertions.assertFalse(JobGroupPermissionUtil.hasJobGroupPermission(hasPermission, 3));
    }

}