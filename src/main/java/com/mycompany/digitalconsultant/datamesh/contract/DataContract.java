package com.mycompany.digitalconsultant.datamesh.contract;

import java.util.List;
import java.util.Map;

/**
 * Contrato de Dados (Data Contract) do Data Mesh.
 * Define schema, SLA de qualidade e políticas de governança e LGPD do domínio.
 */
public record DataContract(
        String domainName,
        String dataProductName,
        String schemaVersion,
        List<String> schemaFields,
        Map<String, String> classificationLabels, // ex: "PII" -> "LGPD-Sensitive", "FinOps" -> "Internal"
        String qualitySlaDescription
) {}
