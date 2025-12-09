package com.xxl.job.admin.scheduler.trigger;

import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.mapper.XxlJobLogMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.scheduler.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.core.openapi.ExecutorBiz;
import com.xxl.job.core.openapi.model.TriggerRequest;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobTriggerTest {
    @Mock
    private XxlJobInfoMapper xxlJobInfoMapper;
    @Mock
    private XxlJobGroupMapper xxlJobGroupMapper;
    @Mock
    private XxlJobLogMapper xxlJobLogMapper;
    @InjectMocks
    private JobTrigger jobTrigger;
    private static final ExecutorBiz EXECUTOR_BIZ_MOCK = Mockito.mock(ExecutorBiz.class);
    private static final XxlJobInfo XXL_JOB_INFO = new XxlJobInfo();
    private static final XxlJobGroup XXL_JOB_GROUP = new XxlJobGroup();
    private static final List<String> ADDRESS_LIST = List.of("127.0.0.1:8081", "127.0.0.1:8082", "127.0.0.1:8083");

    static {
        I18nUtil i18nUtil = new I18nUtil();
        Field single = ReflectionUtils.findField(i18nUtil.getClass(), "single");
        ReflectionUtils.makeAccessible(single);
        ReflectionUtils.setField(single, "single", i18nUtil);
        Method initI18nEnum = ReflectionUtils.findMethod(i18nUtil.getClass(), "initI18nEnum");
        ReflectionUtils.makeAccessible(initI18nEnum);
        ReflectionUtils.invokeMethod(initI18nEnum, i18nUtil);
        XXL_JOB_INFO.setId(1);
        XXL_JOB_INFO.setGlueUpdatetime(new Date());
        XXL_JOB_INFO.setJobGroup(1);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(xxlJobInfoMapper, xxlJobGroupMapper, xxlJobLogMapper, EXECUTOR_BIZ_MOCK);
    }

    @Test
    void jobNotFound() {
        try (MockedStatic<XxlJobAdminBootstrap> mockedStatic = Mockito.mockStatic(XxlJobAdminBootstrap.class)) {
            mockedStatic.when(() -> XxlJobAdminBootstrap.getExecutorBiz(Mockito.anyString())).thenReturn(EXECUTOR_BIZ_MOCK);
            when(EXECUTOR_BIZ_MOCK.run(Mockito.any())).thenReturn(null);
            when(xxlJobInfoMapper.loadById(1)).thenReturn(null);
            jobTrigger.trigger(1, TriggerTypeEnum.MANUAL, 0, null, null, null);
            verify(EXECUTOR_BIZ_MOCK, never()).run(Mockito.any());
        }
    }

    @Test
    void defaultSetting() {
        try (MockedStatic<XxlJobAdminBootstrap> mockedStatic = Mockito.mockStatic(XxlJobAdminBootstrap.class)) {
            mockedStatic.when(() -> XxlJobAdminBootstrap.getExecutorBiz(Mockito.anyString())).thenReturn(EXECUTOR_BIZ_MOCK);
            when(EXECUTOR_BIZ_MOCK.run(Mockito.any())).thenReturn(Response.ofSuccess());
            when(xxlJobInfoMapper.loadById(1)).thenReturn(XXL_JOB_INFO);
            when(xxlJobGroupMapper.load(1)).thenReturn(XXL_JOB_GROUP);
            XXL_JOB_INFO.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.FIRST.name());
            jobTrigger.trigger(1, TriggerTypeEnum.MANUAL, 0, null, null, String.join(",", ADDRESS_LIST));
            ArgumentCaptor<TriggerRequest> triggerRequestCaptor = ArgumentCaptor.forClass(TriggerRequest.class);
            verify(EXECUTOR_BIZ_MOCK).run(triggerRequestCaptor.capture());
            TriggerRequest triggerRequest = triggerRequestCaptor.getValue();
            Assertions.assertEquals(1, triggerRequest.getJobId());
            Assertions.assertEquals(0, triggerRequest.getBroadcastIndex());
            Assertions.assertEquals(1, triggerRequest.getBroadcastTotal());
            ArgumentCaptor<String> addressCaptor = ArgumentCaptor.forClass(String.class);
            mockedStatic.verify(() -> XxlJobAdminBootstrap.getExecutorBiz(addressCaptor.capture()));
            Assertions.assertEquals(ADDRESS_LIST.get(0), addressCaptor.getValue());
        }
    }

    @Test
    void tryLastRouter() {
        try (MockedStatic<XxlJobAdminBootstrap> mockedStatic = Mockito.mockStatic(XxlJobAdminBootstrap.class)) {
            mockedStatic.when(() -> XxlJobAdminBootstrap.getExecutorBiz(Mockito.anyString())).thenReturn(EXECUTOR_BIZ_MOCK);
            when(EXECUTOR_BIZ_MOCK.run(Mockito.any())).thenReturn(Response.ofSuccess());
            when(xxlJobInfoMapper.loadById(1)).thenReturn(XXL_JOB_INFO);
            when(xxlJobGroupMapper.load(1)).thenReturn(XXL_JOB_GROUP);
            XXL_JOB_INFO.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.LAST.name());
            jobTrigger.trigger(1, TriggerTypeEnum.MANUAL, 0, null, null, String.join(",", ADDRESS_LIST));
            verify(EXECUTOR_BIZ_MOCK).run(Mockito.any());
            ArgumentCaptor<String> addressCaptor = ArgumentCaptor.forClass(String.class);
            mockedStatic.verify(() -> XxlJobAdminBootstrap.getExecutorBiz(addressCaptor.capture()));
            Assertions.assertEquals(ADDRESS_LIST.get(ADDRESS_LIST.size() - 1), addressCaptor.getValue());
        }
    }

}