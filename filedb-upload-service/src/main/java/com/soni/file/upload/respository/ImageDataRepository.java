package com.soni.file.upload.respository;

import com.soni.file.upload.entity.ImageData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface ImageDataRepository extends ReactiveCrudRepository<ImageData, Long> {
    Flux<ImageData> findByName(String name);
    Mono<Boolean> existsByName(String name);
}