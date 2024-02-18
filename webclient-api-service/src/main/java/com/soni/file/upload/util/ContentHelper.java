package com.soni.file.upload.util;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.UrlResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;

@Log4j2
public class ContentHelper {
    private static final long BYTE_LENGTH = 1024;
    private static final long CHUNK_SIZE_VERY_LOW = BYTE_LENGTH * 256;
    private static final long CHUNK_SIZE_LOW = BYTE_LENGTH * 512;
    private static final long CHUNK_SIZE_MED = BYTE_LENGTH * 1024;
    private static final long CHUNK_SIZE_HIGH = BYTE_LENGTH * 2048;
    private static final long CHUNK_SIZE_VERY_HIGH = CHUNK_SIZE_HIGH * 2;

    public static long lengthOf(UrlResource urlResource) {
        long fileLength;
        try {
            fileLength = urlResource.contentLength();
        } catch (IOException e) {
            log.error("service could not get resource because the resource does not exist");
            throw Exceptions.propagate(new Exception("Exception on process"));
        }
        return fileLength;
    }

    public static long getChunkSize(int size) {
        long responseSize;
        switch (size) {
            case 1:
                responseSize = CHUNK_SIZE_VERY_LOW;
                break;
            case 2:
                responseSize = CHUNK_SIZE_LOW;
                break;
            case 4:
                responseSize = CHUNK_SIZE_HIGH;
                break;
            case 5:
                responseSize = CHUNK_SIZE_VERY_HIGH;
                break;
            default:
                responseSize = CHUNK_SIZE_MED;
        }
        return responseSize;
    }
}