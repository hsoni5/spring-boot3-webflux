package com.soni.video.dto;

import com.soni.video.entity.Auditing;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class VideoDetails extends Auditing {
    private String id;
    private String name;
    private String thumbnailLink;
    private String duration;
    private String description;
    private String link;
}