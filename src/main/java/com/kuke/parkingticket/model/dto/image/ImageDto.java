package com.kuke.parkingticket.model.dto.image;


import com.kuke.parkingticket.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto implements Serializable {
    private Long id;
    private String path;
    private String basePath;

    public static ImageDto convertImageToDto(Image image) {
        return new ImageDto(image.getId(), image.getPath(), image.getBasePath());
    }
}
