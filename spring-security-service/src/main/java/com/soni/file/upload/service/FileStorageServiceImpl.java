package com.soni.file.upload.service;

import com.soni.file.upload.dto.FileMetaInfo;
import com.soni.file.upload.entity.FileMetaData;
import com.soni.file.upload.respository.FileMetaDataRepository;
import com.soni.file.upload.util.FileService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path root = Paths.get("uploads");
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;
    @Autowired
    private FileService fileService;

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public Mono<String> save(Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(filePart -> filePart.transferTo(root.resolve(filePart.filename())).then(saveMetaData(filePart)));
    }

    @Override
    public Flux<String> saveAll(Flux<FilePart> filePartFlux) {
        return filePartFlux
                .flatMap(filePart ->
                        filePart.transferTo(root.resolve(filePart.filename()))
                                .then(saveMetaData(filePart))
                );
    }

    private Mono<String> saveMetaData(FilePart filePart) {
        String fileName = filePart.filename();
        MediaType mediaType = filePart.headers().getContentType();
        FileMetaData fileMetaData = FileMetaData.builder()
                .name(fileName)
                .filePath(root.toAbsolutePath().toString())
                .type(Objects.requireNonNull(mediaType).getType())  // Set the appropriate type
                .build();
        return fileMetaDataRepository.save(fileMetaData)
                .thenReturn(fileName)
                .doOnNext(savedFileName -> log.info("File metadata saved: {}", savedFileName));
    }

    @Override
    public Flux<DataBuffer> load(String filename) {
        try {
            Path file = root.resolve(filename);
            Long fileSize = Files.size(file);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return DataBufferUtils.read(resource, new DefaultDataBufferFactory(), fileSize.intValue());
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public Mono<UrlResource> getResourceByName(String filename) {
        return createUriResourceFromFile(root.resolve(filename));
    }

    private Mono<UrlResource> createUriResourceFromFile(Path file) {
        return Mono.<UrlResource>create(monoSink -> {
            try {
                UrlResource media = new UrlResource(file.toUri());
                monoSink.success(media);
            } catch (MalformedURLException e) {
                monoSink.error(e);
            }
        }).doOnError(throwable -> {
            throw Exceptions.propagate(throwable);
        });
    }

    @Override
    public Flux<FileMetaInfo> loadAll() throws IOException {
        return Flux.fromStream(Files.walk(root)
                        .filter(path -> !path.equals(root))
                        .map(root::relativize))
                .flatMap(this::mapPathToDTO).distinctUntilChanged();
    }

    private Flux<FileMetaInfo> mapPathToDTO(Path path) {
        return shouldIncludeFile(path)
                .map(this::mapEntityToDTO);
    }

    private Flux<FileMetaData> shouldIncludeFile(Path path) {
        String fileName = path.getFileName().toString();
        return fileMetaDataRepository.findByName(fileName);
    }

    private FileMetaInfo mapEntityToDTO(FileMetaData entity) {
        String fileName = entity.getName();
        String url = UriComponentsBuilder.newInstance().path("/{fileName}")
                .buildAndExpand(fileName).toUriString();
        return FileMetaInfo.builder()
                .id(entity.getId())
                .name(fileName)
                .filePath(url)
                .type(entity.getType())
                .build();
    }
}
