package com.mycompany.digitalconsultant.datamesh.contract;

/**
 * Interface de um Produto de Dados no ecossistema Data Mesh.
 */
public interface DataProduct<T> {

    String getProductId();

    String getDomain();

    DataContract getDataContract();

    T getPayload();

    boolean validateContract();
}
