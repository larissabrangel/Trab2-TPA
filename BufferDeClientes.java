package entity.cms;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;

public class BufferDeClientes implements Buffer<Cliente> {

    private ArquivoSequencial<Cliente> arquivoSequencial;
    private Queue<Cliente> buffer;
    private final int TAMANHO_BUFFER = 100; // Tamanho máximo do buffer
    private String modo; // "leitura" ou "escrita"

    public BufferDeClientes() {
        this.buffer = new LinkedList<>();
    }

    // Associa o buffer a um arquivo sequencial específico
    @Override
    public void associaBuffer(ArquivoSequencial<Cliente> arquivoSequencial) {
        this.arquivoSequencial = arquivoSequencial;
    }

    // Inicializa o buffer, abrindo o arquivo e preparando para leitura ou escrita
    @Override
    public void inicializaBuffer(String modo, String nomeArquivo) {
        this.modo = modo;
        try {
            if (modo.equals("leitura")) {
                arquivoSequencial.abrirArquivo(nomeArquivo, "leitura", Cliente.class);
            } else if (modo.equals("escrita")) {
                arquivoSequencial.abrirArquivo(nomeArquivo, "escrita", Cliente.class);
            } else {
                throw new IllegalArgumentException("Modo inválido: deve ser 'leitura' ou 'escrita'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carrega dados do arquivo para o buffer (modo leitura)
    @Override
    public void carregaBuffer() {
        if (!modo.equals("leitura")) {
            throw new IllegalStateException("Buffer não está em modo de leitura!");
        }

        try {
            // Lê uma lista de clientes do arquivo e os coloca na fila (buffer)
            List<Cliente> clientesLidos = arquivoSequencial.leiaDoArquivo(TAMANHO_BUFFER);
            if (clientesLidos != null) {
                for (Object obj : clientesLidos) {
                    if (obj instanceof Cliente) {
                        buffer.add((Cliente) obj); // Adiciona clientes ao buffer
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Classe não encontrada: " + e.getMessage());
        }
    }

    // Escreve os dados do buffer no arquivo (modo escrita)
    @Override
    public void escreveBuffer() {
        if (!modo.equals("escrita")) {
            throw new IllegalStateException("Buffer não está em modo de escrita!");
        }

        try {
            // Escreve todos os clientes do buffer para o arquivo
            arquivoSequencial.escreveNoArquivo(new LinkedList<>(buffer));
            buffer.clear(); // Limpa o buffer após a escrita
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fecha o arquivo e desassocia o buffer
    @Override
    public void fechaBuffer() {
        try {
            arquivoSequencial.fechaArquivo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adiciona um cliente ao buffer (modo escrita)
    public void adicionaAoBuffer(Cliente cliente) {
        if (!modo.equals("escrita")) {
            throw new IllegalStateException("Buffer não está em modo de escrita!");
        }

        buffer.add(cliente);

        // Se o buffer atingir o tamanho máximo, escreva no arquivo
        if (buffer.size() >= TAMANHO_BUFFER) {
            escreveBuffer();
        }
    }

    // Lê o próximo cliente do buffer (modo leitura)
    public Cliente proximoCliente() {
        if (!modo.equals("leitura")) {
            throw new IllegalStateException("Buffer não está em modo de leitura!");
        }

        if (buffer.isEmpty()) {
            carregaBuffer(); // Recarrega o buffer se estiver vazio
        }

        if (!buffer.isEmpty()) {
            return buffer.poll(); // Retorna e remove o próximo cliente da fila (FIFO)
        }
        return null; // Retorna null se não houver mais clientes
    }

    public String getModo() {
        return modo;
    }

    public Cliente[] proximosClientes(int quantidade) {
        Cliente[] clientes = new Cliente[quantidade];
        int i = 0;

        while (i < quantidade) {
            Cliente cliente = proximoCliente(); // Obtém o próximo cliente
            if (cliente == null) {
                break; // Se não houver mais clientes, sai do loop
            }
            clientes[i] = cliente; // Adiciona o cliente ao array
            i++;
        }

        // Retorna um array com a quantidade solicitada ou menos se não houver mais clientes
        return clientes;
    }

    public boolean removerCliente(String nomeCliente) {
        for (Cliente cliente : buffer) {
            if (cliente.getNome().equals(nomeCliente)) {
                buffer.remove(cliente);  // Remove o cliente da lista
                return true;  // Retorna true se o cliente foi removido
            }
        }
        return false;  // Retorna false se o cliente não foi encontrado
    }


    // Implementação do método removerDoBuffer
    @Override
    public boolean removerDoBuffer(String identificador) {
        if (!modo.equals("escrita")) {
            throw new IllegalStateException("Buffer não está em modo de escrita!");
        }

        // Busca e remove o cliente do buffer com base no identificador
        Cliente clienteParaRemover = null;
        for (Cliente cliente : buffer) {
            if (cliente.getNome().equals(identificador)) {
                clienteParaRemover = cliente;
                break;
            }
        }

        if (clienteParaRemover != null) {
            buffer.remove(clienteParaRemover);
            try {
                // Remove também do arquivo, baseado no identificador
                arquivoSequencial.removerRegistro(identificador);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Implementação do método salvarCliente
    public void salvarCliente(Cliente cliente) {
        if (!modo.equals("escrita")) {
            throw new IllegalStateException("Buffer não está em modo de escrita!");
        }

        adicionaAoBuffer(cliente); // Adiciona o cliente ao buffer

        // Pode optar por escrever no arquivo se o buffer estiver cheio
        if (buffer.size() >= TAMANHO_BUFFER) {
            escreveBuffer();
        }
    }

}
