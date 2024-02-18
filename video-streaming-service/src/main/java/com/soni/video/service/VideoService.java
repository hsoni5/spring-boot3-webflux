package com.soni.video.service;

import com.soni.video.entity.Video;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Pageable;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.BaseStream;

public interface VideoService {
    Mono<ResourceRegion> getRegion(String name, ServerRequest request);

    Mono<UrlResource> getResourceByName(String name);

    Flux<Video> getAllFiles(Pageable pageable);

    Mono<Video> saveVideo(Video video);

    void readFile(Pageable pageable);

    /**
     * default method to create a flux from a stream of file paths
     *
     * @param path to traverse
     * @return Flux<Path>
     */

    default Flux<Video> fromPath(Path path, Flux<Video> videoFlux) {
        return Flux.using(() -> Files.walk(path, FileVisitOption.FOLLOW_LINKS),
                        Flux::fromStream, BaseStream::close)
                .doOnDiscard(BaseStream.class, BaseStream::close)
                .flatMapSequential(filePath -> {
                    if (filePath.toFile().isDirectory() || filePath.getFileName().toString().startsWith(".")) {
                        return Flux.empty();
                    }
                    return videoFlux
                            .filter(file -> file.getName().equalsIgnoreCase(filePath.getFileName().toString()))
                            .next()
                            .switchIfEmpty(Mono.fromSupplier(() -> Video.builder().build()))
                            .map(video -> {
                                video.setLocation(filePath);
                                video.setName(filePath.getFileName().toString());
                                return video;
                            });
                });
    }

}