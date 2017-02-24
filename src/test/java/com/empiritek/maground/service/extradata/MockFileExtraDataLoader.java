package com.empiritek.maground.service.extradata;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Component
public class MockFileExtraDataLoader implements FileExtraDataLoader {

    @Override
    public Map<String, Serializable> getExtraData(String filePath) throws IOException {
        return Collections.singletonMap("filePath", filePath);
    }

    @Override
    public List<String> getSupportedFileExtensions() {
        return asList("mock1", "mock2", "mock3");
    }
}
