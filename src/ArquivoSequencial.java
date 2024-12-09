package org.example;

import java.io.IOException;
import java.util.List;

public interface ArquivoSequencial<T> {

    // Abre o arquivo no modo especificado: leitura, escrita ou leitura/escrita.
    void abrirArquivo(String nomeDoArquivo, String modoDeLeitura, Class<T> classeBase) throws IOException;

    // Lê múltiplos registros do arquivo, retornando-os como uma lista de objetos.
    List<T> leiaDoArquivo(int numeroDeRegistros) throws IOException, ClassNotFoundException;

    // Escreve uma lista de objetos no arquivo.
    void escreveNoArquivo(List<T> dados) throws IOException;

    // Fecha o arquivo, liberando os recursos associados.
    void fechaArquivo() throws IOException;


}
