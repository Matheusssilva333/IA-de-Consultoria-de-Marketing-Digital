package com.mycompany.digitalconsultant.datamesh;

import com.mycompany.digitalconsultant.datamesh.domain.CompanyProfileProduct;
import com.mycompany.digitalconsultant.datamesh.domain.FinOpsTelemetryProduct;
import com.mycompany.digitalconsultant.datamesh.domain.MarketingIntelligenceProduct;
import com.mycompany.digitalconsultant.datamesh.governance.DataMeshFederationRegistry;
import com.mycompany.digitalconsultant.domain.model.ActionItem;
import com.mycompany.digitalconsultant.domain.model.CompanyProfile;
import com.mycompany.digitalconsultant.domain.model.MetricTarget;
import com.mycompany.digitalconsultant.domain.model.StructuredDiagnosis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Arquitetura Data Mesh e Governança Federada")
public class DataMeshAndGovernanceTest {

    @Test
    @DisplayName("Deve validar contrato de dados do domínio CompanyProfile e rejeitar se sem consentimento LGPD")
    void testCompanyProfileDataProductContract() {
        CompanyProfile validProfile = new CompanyProfile();
        validProfile.setNomeEmpresa("Empresa DataMesh S/A");
        validProfile.setOrcamentoMensal(new BigDecimal("10000.00"));
        validProfile.setConsentimentoLGPD(true);

        CompanyProfileProduct product = new CompanyProfileProduct(validProfile);
        assertTrue(product.validateContract());
        assertEquals("CompanyProfileDomain", product.getDomain());
        assertNotNull(product.getDataContract());

        // Perfil sem consentimento LGPD deve violar o contrato de dados
        CompanyProfile invalidProfile = new CompanyProfile();
        invalidProfile.setNomeEmpresa("Empresa Sem LGPD");
        invalidProfile.setOrcamentoMensal(new BigDecimal("5000.00"));
        invalidProfile.setConsentimentoLGPD(false);

        CompanyProfileProduct invalidProduct = new CompanyProfileProduct(invalidProfile);
        assertFalse(invalidProduct.validateContract(), "Contrato deve ser violado caso o consentimento LGPD seja falso");
    }

    @Test
    @DisplayName("Deve validar contrato de dados do domínio Marketing Intelligence")
    void testMarketingIntelligenceProductContract() {
        StructuredDiagnosis diagnosis = new StructuredDiagnosis();
        diagnosis.setDiagnostico("Diagnóstico Válido");
        diagnosis.getOportunidades().add("Oportunidade 1");
        diagnosis.getAcoesPriorizadas().add(new ActionItem("Ação 1", "Desc", "ALTO", "BAIXO", "7 dias"));
        diagnosis.getMetricas().add(new MetricTarget("CAC", "R$ 30", "Meta"));

        MarketingIntelligenceProduct product = new MarketingIntelligenceProduct(diagnosis);
        assertTrue(product.validateContract());
        assertEquals("MarketingIntelligenceDomain", product.getDomain());
    }

    @Test
    @DisplayName("Deve registrar contratos e publicar produtos com sucesso no DataMeshFederationRegistry")
    void testDataMeshFederationRegistry() {
        DataMeshFederationRegistry registry = DataMeshFederationRegistry.getInstance();

        FinOpsTelemetryProduct.TelemetryRecord rec = new FinOpsTelemetryProduct.TelemetryRecord(
                "op-test-123", "sentinel_admin", 500, 300, 800, 0.00135, 120L, true, "OK"
        );
        FinOpsTelemetryProduct finOpsProduct = new FinOpsTelemetryProduct(rec);

        registry.registerDomainContract(finOpsProduct.getDataContract());
        boolean published = registry.publishAndVerify(finOpsProduct);

        assertTrue(published);
        assertTrue(registry.getPublishedProducts().contains(finOpsProduct));
    }
}
