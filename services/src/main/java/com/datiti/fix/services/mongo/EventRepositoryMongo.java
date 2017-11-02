package com.datiti.fix.services.repositories;

import com.datiti.fix.services.model.Event;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.sql.Timestamp;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "mongo_events", path = "mongo_events")
@Api("/mongo_events")
public interface EventRepositoryMongo extends MongoRepository<Event, String> {
    @ApiOperation(value = "Find all events after ts parameter", notes = "ts parameter must be given in the form: YYYY-MM-DD HH:mm:ss")
    List<Event> findByTsAfter(@Param("ts") Timestamp ts);
}
