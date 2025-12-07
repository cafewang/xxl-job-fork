package com.xxl.job.admin.scheduler.route;

import com.xxl.job.admin.scheduler.route.strategy.ExecutorRouteConsistentHash;
import com.xxl.job.admin.scheduler.route.strategy.ExecutorRouteFirst;
import com.xxl.job.admin.scheduler.route.strategy.ExecutorRouteLast;
import com.xxl.job.admin.scheduler.route.strategy.ExecutorRouteRound;
import com.xxl.job.core.openapi.model.TriggerRequest;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

class ExecutorRouterTest {
    private static final List<String> ADDRESS_LIST = List.of("192.168.1.1", "192.168.1.2", "192.168.1.3");
    @Test
    void testRouteFirst() {
        ExecutorRouter executorRouter = new ExecutorRouteFirst();
        Response<String> route = executorRouter.route(null, ADDRESS_LIST);
        Assertions.assertEquals(ADDRESS_LIST.get(0), route.getData());
    }

    @Test
    void testRouteLast() {
        ExecutorRouter executorRouter = new ExecutorRouteLast();
        Response<String> route = executorRouter.route(null, ADDRESS_LIST);
        Assertions.assertEquals(ADDRESS_LIST.get(ADDRESS_LIST.size() - 1), route.getData());
    }

    @Test
    void testRouteRound() {
        TriggerRequest triggerParam = new TriggerRequest();
        triggerParam.setJobId(1);
        ExecutorRouter executorRouter = new ExecutorRouteRound();
        // random at first route
        Response<String> firstRoute = executorRouter.route(triggerParam, ADDRESS_LIST);
        int fstIdx = ADDRESS_LIST.indexOf(firstRoute.getData());
        Response<String> sndRoute = executorRouter.route(triggerParam, ADDRESS_LIST);
        Assertions.assertEquals(ADDRESS_LIST.get((fstIdx + 1) % ADDRESS_LIST.size()), sndRoute.getData());
        Response<String> thirdRoute = executorRouter.route(triggerParam, ADDRESS_LIST);
        Assertions.assertEquals(ADDRESS_LIST.get((fstIdx + 2) % ADDRESS_LIST.size()), thirdRoute.getData());
    }

    @Test
    void testRouteConsistentHash() {
        ExecutorRouter executorRouter = new ExecutorRouteConsistentHash();
        TriggerRequest triggerParam = new TriggerRequest();
        triggerParam.setJobId(1);
        Response<String> fstRoute = executorRouter.route(triggerParam, ADDRESS_LIST);
        int idx = ADDRESS_LIST.indexOf(fstRoute.getData());
        List<String> skipNextOfIdx = IntStream.range(0, ADDRESS_LIST.size())
                .filter(i -> i != (idx + 1) % ADDRESS_LIST.size())
                .mapToObj(ADDRESS_LIST::get).toList();
        Response<String> sndRoute = executorRouter.route(triggerParam, skipNextOfIdx);
        Assertions.assertEquals(fstRoute.getData(), sndRoute.getData());
    }

}