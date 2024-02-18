package com.soni.video.service;

import com.soni.video.entity.Video;
import com.soni.video.respository.VideoRepository;
import com.soni.video.util.ContentHelper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Long.min;

@Service
@Log4j2
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Value("${video.location}")
    private String videoLocation;

    @Override
    public Flux<Video> getAllFiles(Pageable pageable) {
        return fromPath(Paths.get(videoLocation), videoRepository.findAllBy(pageable));
    }

    @Override
    public Mono<Video> saveVideo(Video video) {
        return videoRepository.save(video);
    }

    @Override
    public void readFile(Pageable pageable) {

    }

    @Override
    public Mono<UrlResource> getResourceByName(String filename) {
        //return createUriResourceFromVideo(Paths.get(videoLocation).resolve(filename));
        return null;
    }

    @Override
    public Mono<ResourceRegion> getRegion(String name, ServerRequest request) {
        HttpHeaders headers = request.headers().asHttpHeaders();
        HttpRange range = !headers.getRange().isEmpty() ? headers.getRange().get(0) : null;

        AtomicInteger sizeInt = new AtomicInteger();

        request.queryParam("partial").ifPresent(val ->
                sizeInt.set(Integer.parseInt(val)));

        long chunkSize = ContentHelper.getChunkSize(sizeInt.get());

        Mono<UrlResource> resource = getResourceByName(name);
        return resource.map(urlResource -> {
            long contentLength = ContentHelper.lengthOf(urlResource);
            if (range != null) {
                long start = range.getRangeStart(contentLength);
                long end = range.getRangeEnd(contentLength);
                long resourceLength = end - start + 1;
                long rangeLength = min(chunkSize, resourceLength);
                return new ResourceRegion(urlResource, start, rangeLength);
            } else {
                long rangeLength = min(chunkSize, contentLength);
                return new ResourceRegion(urlResource, 0, rangeLength);
            }
        }).doOnError(throwable -> {
            throw Exceptions.propagate(throwable);
        });
    }
    private Mono<UrlResource> createUriResourceFromVideo(Video videoObj) {
        return Mono.<UrlResource>create(monoSink -> {
            try {
                UrlResource video = new UrlResource(videoObj.getLocation().toUri());
                monoSink.success(video);
            } catch (MalformedURLException e) {
                monoSink.error(e);
            }
        }).doOnError(throwable -> {
            throw Exceptions.propagate(throwable);
        });
    }
}