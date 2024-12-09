package entity.cms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClienteGUI {

    private BufferDeClientes bufferDeClientes;
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;

    public ClienteGUI() {
        bufferDeClientes = new BufferDeClientes();
        criarInterface();
    }

    private void criarInterface() {
        JFrame frame = new JFrame("Gerenciador de Clientes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Painel de controle
        JPanel painelControle = new JPanel();
        JButton btnCarregar = new JButton("Carregar Clientes");
        JButton btnAdicionar = new JButton("Adicionar Cliente");
        JButton btnBuscar = new JButton("Buscar Cliente");

        JTextField campoBusca = new JTextField(20);

        painelControle.add(btnCarregar);
        painelControle.add(btnAdicionar);
        painelControle.add(campoBusca);
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

        // Ajusta a largura da primeira coluna
        tabelaClientes.getColumnModel().getColumn(0).setPreferredWidth(30);

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Ação do botão Carregar Clientes
        btnCarregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarClientes();
            }
        });

        // Ação do botão Buscar Cliente
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarCliente(campoBusca.getText());
            }
        });

        // Ação do botão Adicionar Cliente
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarCliente();
            }
        });

        frame.setVisible(true);
    }

    // Método para carregar os clientes do arquivo e ordená-los alfabeticamente
    private void carregarClientes() {
        // Solicita o nome do arquivo
        String nomeArquivo = JOptionPane.showInputDialog(null, "Digite o nome do arquivo de clientes:");

        if (nomeArquivo != null && !nomeArquivo.trim().isEmpty()) {
            // Inicializa o buffer e carrega os dados usando ArquivoCliente
            bufferDeClientes.associaBuffer(new ArquivoCliente());
            bufferDeClientes.inicializaBuffer("leitura", nomeArquivo);

            modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar novos dados

            // Lê os clientes do buffer e adiciona à tabela
            Cliente cliente;
            int contador = 1; // Contador de linhas
            List<Cliente> clientes = new ArrayList<>();
            while ((cliente = bufferDeClientes.proximoCliente()) != null) {
                clientes.add(cliente);
            }

            // Ordena os clientes em ordem alfabética
            Collections.sort(clientes, Comparator.comparing(Cliente::getNome).thenComparing(Cliente::getSobrenome));

            // Adiciona os clientes ordenados à tabela
            for (Cliente c : clientes) {
                modeloTabela.addRow(new Object[]{contador++, c.getNome(), c.getSobrenome(), c.getEndereco(), c.getTelefone(), c.getCreditScore()});
            }

            // Fecha o buffer
            bufferDeClientes.fechaBuffer();
        } else {
            JOptionPane.showMessageDialog(null, "Nome do arquivo não pode ser vazio.");
        }
    }

    // Método para buscar clientes
    private void buscarCliente(String nome) {
        modeloTabela.setRowCount(0); // Limpa a tabela

        // Lógica de busca (busca simples por nome)
        List<Cliente> clientesEncontrados = new ArrayList<>();
        Cliente cliente;
        while ((cliente = bufferDeClientes.proximoCliente()) != null) {
            if (cliente.getNome().toLowerCase().contains(nome.toLowerCase())) {
                clientesEncontrados.add(cliente);
            }
        }

        // Ordena os clientes encontrados
        Collections.sort(clientesEncontrados, Comparator.comparing(Cliente::getNome));

        // Adiciona os clientes encontrados à tabela
        for (Cliente c : clientesEncontrados) {
            modeloTabela.addRow(new Object[]{modeloTabela.getRowCount() + 1, c.getNome(), c.getSobrenome(), c.getTelefone(), c.getEndereco(), c.getCreditScore()});
        }
    }

    // Método para adicionar um novo cliente
    private void adicionarCliente() {
        String nome = JOptionPane.showInputDialog("Nome do Cliente:");
        String sobrenome = JOptionPane.showInputDialog("Sobrenome do Cliente:");
        String endereco = JOptionPane.showInputDialog("Endereço do Cliente:");
        String telefone = JOptionPane.showInputDialog("Telefone do Cliente:");
        String creditScoreStr = JOptionPane.showInputDialog("Credit Score do Cliente:");

        try {
            int creditScore = Integer.parseInt(creditScoreStr);
            Cliente novoCliente = new Cliente(nome, sobrenome, endereco, telefone, creditScore);

            // Salva o novo cliente no arquivo (essa parte precisa de implementação no ArquivoCliente)
            bufferDeClientes.salvarCliente(novoCliente);

            // Atualiza a tabela
            carregarClientes();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir o cliente.");
        }
    }

    private void removerCliente(String nomeCliente) {
        // Chama o método de remoção da classe BufferDeClientes
        boolean sucesso = bufferDeClientes.removerCliente(nomeCliente);

        // Exibe uma mensagem com base no sucesso ou falha da remoção
        if (sucesso) {
            JOptionPane.showMessageDialog(null, "Cliente removido com sucesso.");
            carregarClientes(); // Atualiza a tabela ou lista de clientes após a remoção
        } else {
            JOptionPane.showMessageDialog(null, "Cliente não encontrado.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClienteGUI::new);
    }
}
