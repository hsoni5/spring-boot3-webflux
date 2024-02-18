package com.soni.file.upload.service;

import com.soni.file.upload.dto.FileMetaInfo;
import com.soni.file.upload.entity.FileMetaData;
import com.soni.file.upload.respository.FileMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public interface FileStorageService {
     void init();

     Mono<String> save(Mono<FilePart> filePartMono);

     Flux<String> saveAll(Flux<FilePart> filePartMono);

     Flux<DataBuffer> load(String filename);

     Mono<UrlResource> getResourceByName(String name);

     Flux<FileMetaInfo> loadAll() throws IOException;

}
