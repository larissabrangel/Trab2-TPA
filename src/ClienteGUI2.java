package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ClienteGUI2 extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private BufferDeClientes bufferDeClientes;
    private List<Cliente> listaClientes;
    private final int TAMANHO_BUFFER = 10000;
    private int registrosCarregados = 0;
    private String arquivoSelecionado;
    private boolean arquivoCarregado = false;

    public ClienteGUI2() {
        setTitle("Gerenciamento de Clientes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        bufferDeClientes = new BufferDeClientes();
        listaClientes = new ArrayList<>();
        criarInterface();
    }

    private void carregarArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        int retorno = fileChooser.showOpenDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            arquivoSelecionado = fileChooser.getSelectedFile().getAbsolutePath();
            bufferDeClientes.associaBuffer(new ArquivoCliente());
            bufferDeClientes.inicializaBuffer("leitura", arquivoSelecionado);
            registrosCarregados = 0;
            tableModel.setRowCount(0);
            carregarMaisClientes();
            arquivoCarregado = true;
        }
    }

    private void carregarMaisClientes() {
        Cliente[] clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER);
        if (clientes != null && clientes.length > 0) {
            for (Cliente cliente : clientes) {
                if (cliente != null) {
                    listaClientes.add(cliente);
                    tableModel.addRow(new Object[]{
                            tableModel.getRowCount() + 1, cliente.getNome(),
                            cliente.getSobrenome(), cliente.getTelefone(),
                            cliente.getEndereco(), cliente.getCreditScore()
                    });
                }
            }
            registrosCarregados += clientes.length;
        }
    }

    private void criarInterface() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCarregar = new JButton("Carregar Clientes");
        JButton btnAdicionar = new JButton("Adicionar Cliente");
        JButton btnRemover = new JButton("Remover Cliente");
        JButton btnOrdenar = new JButton("Ordenar Alfab.");
        JButton btnBuscar = new JButton("Buscar Cliente");

        tableModel = new DefaultTableModel(new String[]{"#", "Nome", "Sobrenome", "Telefone", "Endereço", "Credit Score"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (!scrollPane.getVerticalScrollBar().getValueIsAdjusting()) {
                    if (arquivoCarregado &&
                            scrollPane.getVerticalScrollBar().getValue() +
                                    scrollPane.getVerticalScrollBar().getVisibleAmount() >=
                                    scrollPane.getVerticalScrollBar().getMaximum()) {
                        carregarMaisClientes();
                    }
                }
            }
        });

        btnCarregar.addActionListener(e -> carregarArquivo());
        btnAdicionar.addActionListener(e -> adicionarCliente());
        btnRemover.addActionListener(e -> removerCliente());
        btnOrdenar.addActionListener(e -> ordenarClientes());
        btnBuscar.addActionListener(e -> buscarCliente());

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(btnCarregar);
        controlPanel.add(btnAdicionar);
        controlPanel.add(btnRemover);
        controlPanel.add(btnOrdenar);
        controlPanel.add(btnBuscar);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }

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
                tableModel.addRow(new Object[]{
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

    private void removerCliente() {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada != -1) {
            listaClientes.remove(linhaSelecionada);
            tableModel.removeRow(linhaSelecionada);
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um cliente para remover.");
        }
    }

    private void ordenarClientes() {
        listaClientes.sort(Comparator.comparing(Cliente::getNome));
        atualizarTabela();
    }

    private void buscarCliente() {
        String busca = JOptionPane.showInputDialog(null, "Digite o nome e sobrenome do cliente:");
        if (busca != null && !busca.trim().isEmpty()) {
            for (int i = 0; i < listaClientes.size(); i++) {
                Cliente cliente = listaClientes.get(i);
                // Concatena o nome e sobrenome para buscar a correspondência
                String nomeCompleto = cliente.getNome() + " " + cliente.getSobrenome();
                if (nomeCompleto.equalsIgnoreCase(busca)) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Cliente não encontrado.");
        }
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        int contador = 1;
        for (Cliente cliente : listaClientes) {
            tableModel.addRow(new Object[]{
                    contador++, cliente.getNome(), cliente.getSobrenome(),
                    cliente.getEndereco(), cliente.getTelefone(), cliente.getCreditScore()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteGUI2 gui = new ClienteGUI2();
            gui.setVisible(true);
        });
    }
}