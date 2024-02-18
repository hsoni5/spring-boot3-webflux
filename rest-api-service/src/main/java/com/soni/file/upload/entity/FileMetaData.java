package com.soni.file.upload.entity;

import jakarta.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "file_meta_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetaData {
    @Id
    private Long id;
    private String name;
    private String type;
    private String filePath;
}
