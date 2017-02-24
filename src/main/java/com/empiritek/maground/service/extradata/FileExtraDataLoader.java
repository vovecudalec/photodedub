package com.empiritek.maground.service.extradata;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


interface FileExtraDataLoader {

    Map<String, Serializable> getExtraData(String filePath) throws IOException;

    List<String> getSupportedFileExtensions();
}
