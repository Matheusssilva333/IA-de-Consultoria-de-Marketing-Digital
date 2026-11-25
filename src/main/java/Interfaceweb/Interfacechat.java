package Interfaceweb;

import com.mycompany.digitalconsultant.domain.model.CompanyProfile;
import com.mycompany.digitalconsultant.domain.model.StructuredDiagnosis;

/**
 * Contrato de Interface para visualizadores e controladores de Chat/Consultoria.
 */
public interface Interfacechat {

    void exibirDiagnostico(StructuredDiagnosis diagnosis);

    void carregarPerfil(CompanyProfile profile);

    void atualizarStatusFinOps(int totalTokens, double custoUSD);
}
