package com.soni.file.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMetaInfo {
    private Long id;
    private String name;
    private String type;
    private String filePath;
}
