package com.agrilink.agriculturalmanagementservice.service.impl;

import com.agrilink.agriculturalmanagementservice.dto.ResultatUploadImageDTO;
import com.agrilink.agriculturalmanagementservice.exception.DonneesInvalidesException;
import com.agrilink.agriculturalmanagementservice.service.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final long TAILLE_MAXIMALE_OCTETS = 5 * 1024 * 1024; // 5 Mo
    private static final List<String> TYPES_MIME_AUTORISES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif",
            "image/jpg"
    );

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public void validerFichierImage(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new DonneesInvalidesException("Le fichier image est obligatoire et ne doit pas être vide");
        }

        if (fichier.getSize() > TAILLE_MAXIMALE_OCTETS) {
            throw new DonneesInvalidesException("La taille du fichier image dépasse la limite maximale autorisée de 5 Mo");
        }

        String contentType = fichier.getContentType();
        // On accepte aussi application/octet-stream (Postman) et on se fie à l'extension du fichier
        if (contentType != null
                && !contentType.equalsIgnoreCase("application/octet-stream")
                && !contentType.toLowerCase().startsWith("image/")) {
            throw new DonneesInvalidesException(
                    "Format de fichier non supporté. Seules les images (JPEG, PNG, WEBP, GIF) sont acceptées");
        }

        // Vérification par l'extension du nom de fichier
        String nomFichier = fichier.getOriginalFilename();
        if (nomFichier != null) {
            String ext = nomFichier.toLowerCase();
            if (!ext.endsWith(".jpg") && !ext.endsWith(".jpeg")
                    && !ext.endsWith(".png") && !ext.endsWith(".webp")
                    && !ext.endsWith(".gif")) {
                throw new DonneesInvalidesException(
                        "Extension de fichier non supportée. Utilisez : jpg, jpeg, png, webp ou gif");
            }
        }
    }

    @Override
    public ResultatUploadImageDTO uploaderImage(MultipartFile fichier) {
        validerFichierImage(fichier);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    fichier.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "agrilink/produits",
                            "resource_type", "image"
                    )
            );

            String imageUrl = (String) uploadResult.get("secure_url");
            String imagePublicId = (String) uploadResult.get("public_id");

            if (imageUrl == null || imagePublicId == null) {
                throw new DonneesInvalidesException("Réponse inattendue de Cloudinary lors du téléchargement de l'image");
            }

            return new ResultatUploadImageDTO(imageUrl, imagePublicId);
        } catch (IOException e) {
            throw new DonneesInvalidesException("Erreur lors de l'envoi de l'image vers Cloudinary : " + e.getMessage());
        }
    }

    @Override
    public void supprimerImage(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            // Journalisation ou levée selon le besoin, sans bloquer le flux principal
            throw new DonneesInvalidesException("Erreur lors de la suppression de l'image sur Cloudinary : " + e.getMessage());
        }
    }
}
