package com.soni.file.upload.util;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FileService {

    public Mono<String> getMediaType(FilePart filePart) {
        MediaType mediaType = filePart.headers().getContentType();
        String fileName = filePart.filename();
        String fileExtension = getFileExtension(fileName);
        if (mediaType != null) {
            if (mediaType.equals(MediaType.IMAGE_JPEG) || mediaType.equals(MediaType.IMAGE_PNG)) {
                return processImage(filePart);
            } else if (mediaType.equals(MediaType.APPLICATION_PDF)) {
                return processPdf(filePart);
            } else {
                return Mono.error(new UnsupportedOperationException("Unsupported media type: " + mediaType));
            }
        } else {
            return Mono.error(new IllegalArgumentException("Media type information not available."));
        }
    }

    private Mono<String> processImage(FilePart filePart) {
        // Implement your logic for processing image files
        return Mono.just("Image processing logic for file: " + filePart.filename());
    }

    private Mono<String> processPdf(FilePart filePart) {
        // Implement your logic for processing PDF files
        return Mono.just("PDF processing logic for file: " + filePart.filename());
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex != -1) ? fileName.substring(lastDotIndex + 1) : "";
    }
}
