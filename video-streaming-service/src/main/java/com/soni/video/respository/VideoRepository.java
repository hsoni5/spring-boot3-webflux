package com.soni.video.respository;


import com.soni.video.entity.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VideoRepository extends ReactiveCrudRepository<Video, Long> {
    Flux<Video> findAllBy(Pageable pageable);

    Mono<Video> findVideoByName(String name);
}