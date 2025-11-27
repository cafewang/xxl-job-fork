package com.xxl.job.core.util;

import com.xxl.tool.io.FileTool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;


class ScriptUtilTest {
    private static final String HOME_PATH = System.getProperty("user.home");

    @AfterEach
    void tearDown() {
        FileTool.delete(HOME_PATH + "/test.py");
        FileTool.delete(HOME_PATH + "/test.log");
    }

    @Test
    void testMakeScriptFile() throws IOException {
        String fileContent = "print('hello world')";
        ScriptUtil.markScriptFile(HOME_PATH + "/test.py", fileContent);
        String content = FileTool.readString(HOME_PATH + "/test.py");
        Assertions.assertEquals(fileContent, content);
    }

    @Test
    void testExecToFile() throws IOException {
        String fileContent = "print('hello world')";
        ScriptUtil.markScriptFile(HOME_PATH + "/test.py", fileContent);
        int exitCode = ScriptUtil.execToFile("python", HOME_PATH + "/test.py", HOME_PATH + "/test.log");
        List<String> lines = FileTool.readLines(HOME_PATH + "/test.log");
        Assertions.assertEquals(0, exitCode);
        Assertions.assertEquals("hello world", lines.get(0));
    }
  
}