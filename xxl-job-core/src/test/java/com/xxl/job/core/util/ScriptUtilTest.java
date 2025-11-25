package com.xxl.job.core.util;

import com.xxl.tool.io.FileTool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;


class ScriptUtilTest {
    private static final String HOME_PATH = System.getProperty("user.home");

    @AfterAll
    static void tearDownAll() {
        FileTool.delete(HOME_PATH + "/test.py");
    }

    @Test
    void testMakeScriptFile() throws IOException {
        String fileContent = "print('hello world')";
        ScriptUtil.markScriptFile(HOME_PATH + "/test.py", fileContent);
        String content = FileTool.readString(HOME_PATH + "/test.py");
        Assertions.assertEquals(fileContent, content);
    }
  
}