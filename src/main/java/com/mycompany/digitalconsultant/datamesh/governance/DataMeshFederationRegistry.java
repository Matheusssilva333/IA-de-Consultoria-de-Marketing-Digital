package com.mycompany.digitalconsultant.datamesh.governance;

import com.mycompany.digitalconsultant.datamesh.contract.DataContract;
import com.mycompany.digitalconsultant.datamesh.contract.DataProduct;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Governança Federada do Data Mesh.
 * Registra domínios, valida contratos de dados e garante conformidade de auditoria.
 */
public class DataMeshFederationRegistry {

    private static final Logger LOGGER = Logger.getLogger(DataMeshFederationRegistry.class.getName());
    private static final DataMeshFederationRegistry INSTANCE = new DataMeshFederationRegistry();

    private final Map<String, List<DataContract>> registeredContracts = new ConcurrentHashMap<>();
    private final List<DataProduct<?>> publishedProducts = Collections.synchronizedList(new ArrayList<>());

    private DataMeshFederationRegistry() {}

    public static DataMeshFederationRegistry getInstance() {
        return INSTANCE;
    }

    public void registerDomainContract(DataContract contract) {
        Objects.requireNonNull(contract, "Contrato não pode ser nulo");
        registeredContracts.computeIfAbsent(contract.domainName(), k -> new ArrayList<>()).add(contract);
        LOGGER.info(() -> String.format("[DataMesh Governance] Contrato registrado para domínio '%s': %s (v%s)",
                contract.domainName(), contract.dataProductName(), contract.schemaVersion()));
    }

    public boolean publishAndVerify(DataProduct<?> product) {
        Objects.requireNonNull(product, "Produto de dados não pode ser nulo");

        // Validação estrita do contrato do produto
        if (!product.validateContract()) {
            LOGGER.severe(() -> String.format("[DataMesh Governance] VIOLAÇÃO DE CONTRATO no produto '%s' do domínio '%s'",
                    product.getProductId(), product.getDomain()));
            return false;
        }

        publishedProducts.add(product);
        LOGGER.fine(() -> String.format("[DataMesh Governance] Produto publicado com sucesso: %s", product.getProductId()));
        return true;
    }

    public List<DataProduct<?>> getPublishedProducts() {
        return Collections.unmodifiableList(publishedProducts);
    }

    public Map<String, List<DataContract>> getRegisteredContracts() {
        return Collections.unmodifiableMap(registeredContracts);
    }
}
