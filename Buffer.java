package entity.cms;

public interface Buffer<T> {

    // Associa o buffer a um arquivo sequencial
    void associaBuffer(ArquivoSequencial<T> arquivoSequencial);

    // Inicializa o buffer, abrindo o arquivo e preparando-o para leitura ou escrita
    void inicializaBuffer(String modo, String nomeArquivo);

    // Carrega dados do arquivo para o buffer, caso seja um buffer de leitura
    void carregaBuffer();

    // Escreve os dados do buffer no arquivo, caso seja um buffer de escrita
    void escreveBuffer();

    // Desassocia o buffer e fecha o arquivo
    void fechaBuffer();

    // Remove um registro do buffer e do arquivo
    boolean removerDoBuffer(String identificador);

}
