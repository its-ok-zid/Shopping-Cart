package com.cts.serviceImpl;

import com.cts.service.ThumbnailService;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class ThumbnailServiceImpl implements ThumbnailService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;

    public ThumbnailServiceImpl(GridFsTemplate gridFsTemplate,
                                GridFsOperations gridFsOperations) {
        this.gridFsTemplate = gridFsTemplate;
        this.gridFsOperations = gridFsOperations;
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try (InputStream is = file.getInputStream()) {
            ObjectId id = gridFsTemplate.store(is, filename, file.getContentType());
            return id != null ? id.toHexString() : null;
        }
    }

    @Override
    public Optional<GridFsResource> loadAsResource(String fileId) {
        if (fileId == null || fileId.isBlank()) return Optional.empty();
        try {
            GridFSFile gridFSFile = gridFsTemplate.findOne(
                    org.springframework.data.mongodb.core.query.Query.query(
                            org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new ObjectId(fileId))
                    )
            );
            if (gridFSFile == null) return Optional.empty();
            GridFsResource resource = gridFsOperations.getResource(gridFSFile);
            return Optional.of(resource);
        } catch (IllegalArgumentException e) {
            // thrown if fileId is not a valid ObjectId
            return Optional.empty();
        }
    }

    @Override
    public void delete(String fileId) {
        if (fileId == null || fileId.isBlank()) return;
        try {
            gridFsTemplate.delete(Query.query(
                   Criteria.where("_id").is(new ObjectId(fileId))
            ));
        } catch (IllegalArgumentException e) {
            // invalid id, ignore or log
        }
    }
}
