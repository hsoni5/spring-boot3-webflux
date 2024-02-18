package com.soni.file.upload.respository;


import com.soni.file.upload.entity.FileMetaData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Member;
import java.util.Optional;

@Repository
public interface FileMetaDataRepository extends ReactiveCrudRepository<FileMetaData, Long> {
    Flux<FileMetaData> findByName(String name);

    Mono<Boolean> existsByName(String name);
}
