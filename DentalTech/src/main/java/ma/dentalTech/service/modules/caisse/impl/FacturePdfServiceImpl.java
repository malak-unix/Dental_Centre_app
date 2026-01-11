package ma.dentalTech.service.modules.caisse.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import ma.dentalTech.mvc.dto.caisse.FacturePrintDTO;
import ma.dentalTech.service.modules.caisse.api.FacturePdfService;

import java.io.ByteArrayOutputStream;

public class FacturePdfServiceImpl implements FacturePdfService {

    @Override
    public byte[] generateFacturePdf(FacturePrintDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("FacturePrintDTO obligatoire");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            // =========================
            // Titre
            // =========================
            Paragraph title = new Paragraph("FACTURE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // =========================
            // Infos facture
            // =========================
            document.add(new Paragraph("Numero : " + n(dto.getNumeroFacture()), boldFont));
            document.add(new Paragraph("Date : " + n(dto.getDateFacture()), normalFont));
            document.add(new Paragraph("Consultation ID : " + n(dto.getConsultationId()), normalFont));
            document.add(new Paragraph("Statut : " + n(dto.getStatut()), normalFont));
            document.add(Chunk.NEWLINE);

            // =========================
            // Montants
            // =========================
            document.add(new Paragraph("Total facture : " + n(dto.getTotalFacture()) + " DH", boldFont));
            document.add(new Paragraph("Total payé : " + n(dto.getTotalPaye()) + " DH", normalFont));
            document.add(new Paragraph("Reste à payer : " + n(dto.getReste()) + " DH", normalFont));

            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Signature / Cachet", normalFont));

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erreur génération PDF facture : " + e.getMessage(), e
            );
        }
    }

    private String n(Object v) {
        return v == null ? "" : String.valueOf(v);
    }
}
