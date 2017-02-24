package com.empiritek.maground.service.extradata;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
class PdfExtraDataLoader implements FileExtraDataLoader {

    @Override
    public Map<String, Serializable> getExtraData(String filePath) throws IOException {
        HashMap<String, Serializable> metadata = new HashMap<>();
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDDocumentCatalog pdCatalog = document.getDocumentCatalog();
            PDAcroForm pdAcroForm = pdCatalog.getAcroForm();

            pdAcroForm.getFields().forEach(pdField ->
                    extractFieldData(pdField, metadata)
            );
        }
        return metadata;
    }

    private void extractFieldData(PDField pdField, HashMap<String, Serializable> metadata) {
        if (pdField.getFullyQualifiedName().startsWith("Item")) {
            metadata.put(pdField.getFullyQualifiedName().substring(4), pdField.getValueAsString());
        }
    }

    @Override
    public List<String> getSupportedFileExtensions() {
        return Collections.singletonList("pdf");
    }
}
