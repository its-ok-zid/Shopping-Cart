package com.cts.config;

import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class MongoConfig {

    @Value("${app.thumbnail.gridfs.bucket:itemThumbnails}")
    private String gridFsBucket;

    @Bean
    public GridFsTemplate gridFsTemplate(MongoTemplate mongoTemplate, MappingMongoConverter converter, MongoClient mongoClient) {
        // optionally prevent _class field: converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new GridFsTemplate(mongoTemplate.getMongoDatabaseFactory(), converter, gridFsBucket);
    }
}
