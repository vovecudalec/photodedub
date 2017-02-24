package com.empiritek.maground.web;

import com.empiritek.maground.service.FileLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(FileLoaderController.class)
public class FileLoaderControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private FileLoader fileLoader;

    @Test
    public void testStartFileLoading() throws Exception {

        mvc.perform(get("/file-loading/start"))
                .andExpect(status().isOk())
                .andExpect(content().string("file loading is running\n"));

        verify(fileLoader, timeout(500).times(1)).loadFoldersHierarchy(anyString());
    }

    @Test
    public void testDoubleStartFileLoading() throws Exception {
        doAnswer(answer -> {
            Thread.sleep(10000);
            return null;
        }).when(fileLoader).loadFoldersHierarchy(anyString());

        mvc.perform(get("/file-loading/start"));

        mvc.perform(get("/file-loading/start"))
                .andExpect(status().isOk())
                .andExpect(content().string("file loading already has ran\n"));

        verify(fileLoader, timeout(500).times(1)).loadFoldersHierarchy(anyString());
    }

}