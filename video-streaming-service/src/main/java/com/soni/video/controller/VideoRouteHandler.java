package com.soni.video.controller;

import com.soni.video.dto.VideoDetails;
import com.soni.video.entity.Video;
import com.soni.video.service.VideoService;
import com.soni.video.util.ContentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


 3
10


100000


@Component
public class VideoRouteHandler {
    @Autowired
    private VideoService videoService;

    public Mono<ServerResponse> listVideos(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("1"));
        Pageable paging = PageRequest.of(page, size);
        Flux<Video> videos = videoService.getAllFiles(paging);
        Flux<VideoDetails> videoDetailsFlux = videos.map(video -> mapToVideoDetails(video, request))
                .doOnError(t -> {
                    throw Exceptions.propagate(t);
                });
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .location(request.uri())
                .body(videoDetailsFlux, VideoDetails.class);
    }

    private VideoDetails mapToVideoDetails(Video video, ServerRequest request) {
        return VideoDetails.builder()
                .createdBy(video.getCreatedBy())
                .createdDate(video.getCreatedDate())
                .lastModifiedBy(video.getLastModifiedBy())
                .lastModifiedDate(video.getLastModifiedDate())
                .id(video.getId()).name(video.getName())
                .description(video.getDescription())
                .duration(video.getDuration())
                .thumbnailLink(getUri(request) + '/' + video.getThumbnail())
                .link(getUri(request) + '/' + video.getName())
                .build();
    }

    private String getUri(ServerRequest request) {
        return request.uri().toString().split("\\?")[0];
    }

    public Mono<ServerResponse> getPartialContent(ServerRequest request) {
        String name = request.pathVariable("name");
        Mono<ResourceRegion> resourceRegionMono = videoService.getRegion(name, request);
        return resourceRegionMono
                .flatMap(resourceRegion -> ServerResponse
                        .status(HttpStatus.PARTIAL_CONTENT)
                        .contentLength(resourceRegion.getCount())
                        .headers(headers -> headers.setCacheControl(CacheControl.noCache()))
                        .body(resourceRegionMono, ResourceRegion.class))
                .doOnError(throwable -> {
                    throw Exceptions.propagate(throwable);
                });
    }

    /**
     * This function gets a file from the file system and returns it as a whole
     * videoResource.contentLength() is a blocking call, therefore it is wrapped in a Mono.
     * it returns a FileNotFound exception which is wrapped and propagated down the stream
     */
    public Mono<ServerResponse> getFullContent(ServerRequest request) {
        String fileName = request.pathVariable("name");
        Mono<UrlResource> videoResourceMono = videoService.getResourceByName(fileName);
        return videoResourceMono
                .flatMap(urlResource -> {
                    long contentLength = ContentHelper.lengthOf(urlResource);
                    return ServerResponse
                            .ok()
                            .contentLength(contentLength)
                            .headers(httpHeaders -> httpHeaders.setCacheControl(CacheControl.noCache()))
                            .body(videoResourceMono, UrlResource.class);
                });
    }
}
