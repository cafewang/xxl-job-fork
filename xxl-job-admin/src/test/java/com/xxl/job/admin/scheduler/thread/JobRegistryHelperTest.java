package com.xxl.job.admin.scheduler.thread;

import com.xxl.job.admin.model.XxlJobRegistry;
import com.xxl.job.core.constant.RegistType;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class JobRegistryHelperTest {
    @Test
    void testBuildAddressMap() {
        List<XxlJobRegistry> registryList = Lists.list(
                new XxlJobRegistry(RegistType.EXECUTOR.name(), "app1", "127.0.0.1:8080"),
                new XxlJobRegistry(RegistType.EXECUTOR.name(), "app1", "127.0.0.1:8081"),
                new XxlJobRegistry(RegistType.EXECUTOR.name(), "app2", "127.0.0.1:8082"),
                new XxlJobRegistry(RegistType.ADMIN.name(), "app2", "127.0.0.1:8083")
        );
        Map<String, List<String>> addressMap = JobRegistryHelper.buildAddressMap(registryList);
        Assertions.assertEquals(addressMap.get("app1"), Lists.newArrayList("127.0.0.1:8080", "127.0.0.1:8081"));
        Assertions.assertEquals(addressMap.get("app2"), Lists.newArrayList("127.0.0.1:8082"));
    }

}