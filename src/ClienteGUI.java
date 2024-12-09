package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ClienteGUI {

    private BufferDeClientes bufferDeClientes;
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private List<Cliente> listaClientes;

    public ClienteGUI() {
        bufferDeClientes = new BufferDeClientes();
        listaClientes = new ArrayList<>();
        criarInterface();
    }

    private void criarInterface() {
        JFrame frame = new JFrame("Gerenciador de Clientes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Painel de controle
        JPanel painelControle = new JPanel(new FlowLayout());
        JButton btnCarregar = new JButton("Carregar Clientes");
        JButton btnAdicionar = new JButton("Adicionar Cliente");
        JButton btnRemover = new JButton("Remover Cliente");
        JButton btnOrdenar = new JButton("Ordenar Alfab.");
        JButton btnBuscar = new JButton("Buscar Cliente");

        painelControle.add(btnCarregar);
        painelControle.add(btnAdicionar);
        painelControle.add(btnRemover);
        painelControle.add(btnOrdenar);
        painelControle.add(btnBuscar);
        frame.add(painelControle, BorderLayout.NORTH);

        // Modelo da tabela
        modeloTabela = new DefaultTableModel(new Object[]{"#", "Nome", "Sobrenome", "Endereço", "Telefone", "CreditScore"}, 0);
        tabelaClientes = new JTable(modeloTabela) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede a edição das células
            }
        };

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Ações dos botões
        btnCarregar.addActionListener(e -> carregarClientes());
        btnAdicionar.addActionListener(e -> adicionarCliente());
        btnRemover.addActionListener(e -> removerCliente());
        btnOrdenar.addActionListener(e -> ordenarClientes());
        btnBuscar.addActionListener(e -> buscarCliente());

        frame.setVisible(true);
    }
    //Método que carrega Clientes no arquivos e exibe na tabela
    private void carregarClientes() {
        // Solicita o nome do arquivo
        String nomeArquivo = JOptionPane.showInputDialog(null, "Digite o nome do arquivo de clientes:");
        if (nomeArquivo != null && !nomeArquivo.trim().isEmpty()) {
            // Inicializa o buffer e carrega os dados usando ArquivoCliente
            bufferDeClientes.associaBuffer(new ArquivoCliente());
            bufferDeClientes.inicializaBuffer("leitura", nomeArquivo);

            modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar novos dados
            listaClientes.clear();      // Limpa a lista local

            // Lê os clientes do buffer e adiciona à tabela
            Cliente cliente;
            int contador = 1;
            while ((cliente = bufferDeClientes.proximoCliente()) != null) {
                listaClientes.add(cliente);
                modeloTabela.addRow(new Object[]{
                        contador++, cliente.getNome(), cliente.getSobrenome(),
                        cliente.getEndereco(), cliente.getTelefone(), cliente.getCreditScore()
                });
            }

            // Fecha o buffer
            bufferDeClientes.fechaBuffer();
        } else {
            JOptionPane.showMessageDialog(null, "Nome do arquivo não pode ser vazio.");
        }
    }
    //Método que adicona um novo Cliente na tabela, após o preencimento dos dados e exibe na tabela
    private void adicionarCliente() {
        JTextField nomeField = new JTextField();
        JTextField sobrenomeField = new JTextField();
        JTextField enderecoField = new JTextField();
        JTextField telefoneField = new JTextField();
        JTextField creditScoreField = new JTextField();

        Object[] campos = {
                "Nome:", nomeField,
                "Sobrenome:", sobrenomeField,
                "Endereço:", enderecoField,
                "Telefone", telefoneField,
                "Credit Score:", creditScoreField
        };

        int option = JOptionPane.showConfirmDialog(null, campos, "Adicionar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Cliente cliente = new Cliente(
                        nomeField.getText(),
                        sobrenomeField.getText(),
                        enderecoField.getText(),
                        telefoneField.getText(),
                        Integer.parseInt(creditScoreField.getText())
                );
                listaClientes.add(cliente);
                modeloTabela.addRow(new Object[]{
                        listaClientes.size(),
                        cliente.getNome(),
                        cliente.getSobrenome(),
                        cliente.getEndereco(),
                        cliente.getTelefone(),
                        cliente.getCreditScore()
                });
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Credit Score deve ser um número.");
            }
        }
    }
    //Método que remove o cliente selecionado na tabela
    private void removerCliente() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada != -1) {
            listaClientes.remove(linhaSelecionada);
            modeloTabela.removeRow(linhaSelecionada);
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um cliente para remover.");
        }
    }
    //Método que ordena os clientes da tabela por nome em ordem alfábetica
    private void ordenarClientes() {
        listaClientes.sort(Comparator.comparing(Cliente::getNome));
        atualizarTabela();
    }
    // Método que realiza a busca do cliente na tabela
    private void buscarCliente() {
        String busca = JOptionPane.showInputDialog(null, "Digite o nome e o sobrenome do cliente:");
        if (busca != null && !busca.trim().isEmpty()) {
            for (int i = 0; i < listaClientes.size(); i++) {
                // Concatena o nome e o sobrenome do cliente
                String nomeCompleto = listaClientes.get(i).getNome() + " " + listaClientes.get(i).getSobrenome();

                // Compara o nome completo com a busca (ignorando maiúsculas/minúsculas)
                if (nomeCompleto.equalsIgnoreCase(busca)) {
                    tabelaClientes.setRowSelectionInterval(i, i); // Seleciona a linha correspondente
                    tabelaClientes.scrollRectToVisible(tabelaClientes.getCellRect(i, 0, true)); // Rola para tornar visível
                    return;
                }
            }
            // Caso não encontre o cliente
            JOptionPane.showMessageDialog(null, "Cliente não encontrado.");
        }
    }

    //Método que atualiza os dados exibidos na tabela com os clientes da lista
    private void atualizarTabela() {
        modeloTabela.setRowCount(0); // Limpa a tabela
        int contador = 1;
        for (Cliente cliente : listaClientes) {
            modeloTabela.addRow(new Object[]{
                    contador++, cliente.getNome(), cliente.getSobrenome(),
                    cliente.getEndereco(), cliente.getTelefone(), cliente.getCreditScore()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClienteGUI());
    }
}