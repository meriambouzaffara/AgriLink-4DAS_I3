package com.agrilink.agriculturalmanagementservice.service;

import com.agrilink.agriculturalmanagementservice.dto.ResultatUploadImageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    ResultatUploadImageDTO uploaderImage(MultipartFile fichier);

    void supprimerImage(String publicId);

    void validerFichierImage(MultipartFile fichier);
}
