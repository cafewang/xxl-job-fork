package com.xxl.job.core.context;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class XxlJobHelperTest {

    @Test
    void getJobId() {
        long jobId = 1;
        XxlJobContext context = new XxlJobContext(jobId, "param", 1, "log", 1, 1);
        XxlJobContext.setXxlJobContext(context);
        // check in child thread
        try {
            CompletableFuture.runAsync(() -> assertEquals(jobId, XxlJobHelper.getJobId())).get();
        } catch (Exception e) {
            fail();
        }
    }
}