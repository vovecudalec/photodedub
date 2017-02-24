package com.empiritek.maground.service;


import java.io.IOException;

public interface FileLoader {
    void loadFoldersHierarchy(String path) throws IOException;
}
