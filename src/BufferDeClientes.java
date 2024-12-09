package org.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
        System.out.println("Inicializando buffer no modo: " + modo);
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
        if (modo == null || !modo.equals("leitura")) {
            throw new IllegalStateException("Buffer não está em modo de leitura!");
        }

        try {
            List<Cliente> clientesLidos = arquivoSequencial.leiaDoArquivo(TAMANHO_BUFFER);
            if (clientesLidos != null) {
                buffer.addAll(clientesLidos);
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
        if (modo == null || !modo.equals("escrita")) {
            throw new IllegalStateException("Buffer não está em modo de escrita!");
        }

        try {
            arquivoSequencial.escreveNoArquivo(new LinkedList<>(buffer));
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fecha o arquivo e desassocia o buffer
    @Override
    public void fechaBuffer() {
        try {
            if (arquivoSequencial != null) {
                arquivoSequencial.fechaArquivo();
                arquivoSequencial = null;
                buffer.clear();
                modo = null; // Reseta o modo
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adiciona um cliente ao buffer (modo escrita)
    public void adicionaAoBuffer(Cliente cliente) {
        if (modo == null || !modo.equals("escrita")) {
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
        if (modo == null || !modo.equals("leitura")) {
            throw new IllegalStateException("Buffer não está em modo de leitura!");
        }

        if (buffer.isEmpty()) {
            carregaBuffer();
        }

        return buffer.poll(); // Retorna e remove o próximo cliente da fila (FIFO)
    }

    public String getModo() {
        return modo;
    }

    public Cliente[] proximosClientes(int quantidade) {
        if (modo == null || !modo.equals("leitura")) {
            throw new IllegalStateException("Buffer não está em modo de leitura!");
        }

        Cliente[] clientes = new Cliente[quantidade];
        int i = 0;

        while (i < quantidade) {
            Cliente cliente = proximoCliente();
            if (cliente == null) {
                break;
            }
            clientes[i++] = cliente;
        }

        return Arrays.copyOf(clientes, i);
    }
}