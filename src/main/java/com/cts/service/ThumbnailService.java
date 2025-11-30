package com.cts.service;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


public interface ThumbnailService {

     public String store(MultipartFile file) throws IOException;
     public Optional<GridFsResource> loadAsResource(String fileId);
     public void delete(String fileId);
}
