package com.soni.video.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;

@Data
@Table(name = "videos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Video extends Auditing implements Serializable {
    @Serial
    private static final long serialVersionUID = 265315120455998196L;

    @Id
    private String id;
    private String name;
    private String thumbnail;
    private String duration;
    private String description;
    private String uri;
    @JsonIgnore
    @Transient
    private Path location;
}