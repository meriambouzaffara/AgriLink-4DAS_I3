package tn.agrilink.backend.qualitycertificationservice.services;


import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
@AllArgsConstructor

    public class RapportPdfService {

        private final Path rapportStoragePath;

        public String genererPdf(
                String titre,
                List<String[]> donnees,
                String nomFichier) {

            try {
                // Créer le dossier s'il n'existe pas
                Files.createDirectories(rapportStoragePath);

                // Chemin complet du fichier PDF
                Path fichier = rapportStoragePath.resolve(nomFichier);

                // Créer le document PDF
                Document document = new Document();

                PdfWriter.getInstance(
                        document,
                        new FileOutputStream(fichier.toFile())
                );

                document.open();

                // Titre
                Font titreFont = FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18
                );

                document.add(new Paragraph(titre, titreFont));

                document.add(new Paragraph(" "));

                // Date de génération
                document.add(new Paragraph(
                        "Date de génération : " + LocalDateTime.now()
                ));

                document.add(new Paragraph(" "));

                // Tableau
                if (donnees != null && !donnees.isEmpty()) {

                    int nombreColonnes = donnees.get(0).length;

                    PdfPTable tableau = new PdfPTable(nombreColonnes);

                    // En-têtes
                    for (String colonne : donnees.get(0)) {
                        tableau.addCell(new Phrase(colonne));
                    }

                    // Lignes
                    for (int i = 1; i < donnees.size(); i++) {
                        for (String valeur : donnees.get(i)) {
                            tableau.addCell(
                                    valeur == null ? "" : valeur
                            );
                        }
                    }

                    document.add(tableau);
                }

                document.close();

                return fichier.toString();

            } catch (Exception e) {
                throw new RuntimeException(
                        "Erreur lors de la génération du PDF : " + e.getMessage(),
                        e
                );
            }
        }
    }
