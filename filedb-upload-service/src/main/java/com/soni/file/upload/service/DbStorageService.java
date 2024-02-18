package com.soni.file.upload.service;

import com.soni.file.upload.entity.ImageData;
import com.soni.file.upload.respository.ImageDataRepository;
import com.soni.file.upload.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class DbStorageService {
//    @Autowired
//    private ImageDataRepository repository;
//    public String uploadImage(MultipartFile file) throws IOException {
//        ImageData imageData = repository.save(ImageData.builder()
//                .name(file.getOriginalFilename())
//                .type(file.getContentType())
//                .imageData(ImageUtils.compressImage(file.getBytes())).build());
//        if (null != imageData ) {
//            return "file uploaded successfully : " + file.getOriginalFilename();
//        }
//        return null;
//    }
//
//    public byte[] downloadImage(String fileName) {
//        Optional<ImageData> dbImageData = repository.findByName(fileName);
//        return ImageUtils.decompressImage(dbImageData.get().getImageData());
//    }
}
