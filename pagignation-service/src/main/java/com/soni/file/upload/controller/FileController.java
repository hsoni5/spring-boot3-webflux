package com.soni.file.upload.controller;
//https://www.bezkoder.com/spring-webflux-file-upload-example/

import com.soni.file.upload.dto.ApiResponse;
import com.soni.file.upload.dto.FileMetaInfo;
import com.soni.file.upload.respository.FileMetaDataRepository;
import com.soni.file.upload.service.FileStorageService;
import com.soni.file.upload.util.ContentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;

    @PostMapping(value = "/single")
    public Mono<ResponseEntity<ApiResponse>> uploadFile(@RequestPart("file") Mono<FilePart> filePartMono) {
        return fileStorageService.save(filePartMono).map((filename)
                -> ResponseEntity.ok().body(ApiResponse.builder().message("Uploaded the file successfully: " + filename).build()));
    }

    @PostMapping()
    public Mono<ResponseEntity<ApiResponse>> uploadFiles(@RequestPart("files") Flux<FilePart> filePartFlux) {
        fileStorageService.saveAll(filePartFlux);
        return fileStorageService.saveAll(filePartFlux).collectList().flatMap(filenames -> {
            String message = "Uploaded " + filenames.size() + " files successfully: " + filenames;
            ApiResponse apiResponse = ApiResponse.builder().message(message).build();
            return Mono.just(ResponseEntity.ok().body(apiResponse));
        });
    }

    @GetMapping
    public ResponseEntity<Flux<FileMetaInfo>> readAllFiles() throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(fileStorageService.loadAll());
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Flux<DataBuffer>> getFile(@PathVariable String filename) {
        Flux<DataBuffer> file = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/stream/{filename:.+}")
    @ResponseBody
    public Mono<ResponseEntity<UrlResource>> getStreamingContent(@PathVariable String filename) {
        Mono<UrlResource> resourceMono = fileStorageService.getResourceByName(filename);
        return resourceMono.flatMap(urlResource -> {
            long contentLength = ContentHelper.lengthOf(urlResource);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(contentLength);
            headers.setCacheControl(CacheControl.noCache());
            return Mono.just(ResponseEntity.ok()
                    .headers(headers)
                    .body(urlResource));
        });
    }
}
