package entity.cms;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArquivoCliente implements ArquivoSequencial<Cliente> {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private RandomAccessFile randomAccessFile;  // Usado para modificar diretamente o arquivo
    private File file;

    @Override
    public void abrirArquivo(String nomeDoArquivo, String modoDeLeitura, Class<Cliente> classeBase) throws IOException {
        this.file = new File(nomeDoArquivo);

        if (modoDeLeitura.equals("leitura")) {
            if (file.exists()) {
                inputStream = new ObjectInputStream(new FileInputStream(file));
            } else {
                throw new FileNotFoundException("Arquivo não encontrado.");
            }
        } else if (modoDeLeitura.equals("escrita")) {
            outputStream = new ObjectOutputStream(new FileOutputStream(file));
        } else if (modoDeLeitura.equals("leitura/escrita")) {
            // Para leitura/escrita, abrir ambos os streams
            if (file.exists()) {
                inputStream = new ObjectInputStream(new FileInputStream(file));
            }
            outputStream = new ObjectOutputStream(new FileOutputStream(file, true)); // Modo append
        } else {
            throw new IllegalArgumentException("Modo de leitura inválido.");
        }
    }

    @Override
    public List<Cliente> leiaDoArquivo(int numeroDeRegistros) throws IOException, ClassNotFoundException {
        List<Cliente> registros = new ArrayList<>();

        try {
            for (int i = 0; i < numeroDeRegistros; i++) {
                Cliente cliente = (Cliente) inputStream.readObject();
                registros.add(cliente);
            }
        } catch (EOFException e) {
            // Final do arquivo atingido, retornando o que foi lido até agora
        }

        return registros;
    }

    @Override
    public void escreveNoArquivo(List<Cliente> dados) throws IOException {
        for (Cliente cliente : dados) {
            outputStream.writeObject(cliente);
        }
    }

    @Override
    public void fechaArquivo() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
    }

    // Método para remover um cliente com base no nome
    public boolean removerCliente(String nome) throws IOException, ClassNotFoundException {
        File tempFile = new File("tempfile.dat");
        try (ObjectInputStream tempInput = new ObjectInputStream(new FileInputStream(file));
             ObjectOutputStream tempOutput = new ObjectOutputStream(new FileOutputStream(tempFile))) {

            boolean removed = false;
            Cliente cliente;

            while (true) {
                try {
                    cliente = (Cliente) tempInput.readObject();
                    if (!cliente.getNome().equals(nome)) {
                        tempOutput.writeObject(cliente); // Escreve no arquivo temporário se o nome não corresponder
                    } else {
                        removed = true; // Marcamos como removido
                    }
                } catch (EOFException e) {
                    break; // Final do arquivo
                }
            }

            if (removed) {
                // Se algum cliente foi removido, substituímos o arquivo original pelo temporário
                if (file.delete()) {
                    tempFile.renameTo(file); // Substitui o arquivo original
                } else {
                    throw new IOException("Falha ao substituir o arquivo.");
                }
            }

            return removed;
        }
    }
    @Override
    public boolean removerRegistro(String identificador) throws IOException, ClassNotFoundException {
        File tempFile = new File("tempfile.dat");
        try (ObjectInputStream tempInput = new ObjectInputStream(new FileInputStream(file));
             ObjectOutputStream tempOutput = new ObjectOutputStream(new FileOutputStream(tempFile))) {

            boolean removed = false;
            Cliente cliente;

            while (true) {
                try {
                    cliente = (Cliente) tempInput.readObject();
                    if (!cliente.getNome().equals(identificador)) {
                        tempOutput.writeObject(cliente); // Escreve o cliente no arquivo temporário se não for o correto
                    } else {
                        removed = true; // Marca como removido
                    }
                } catch (EOFException e) {
                    break; // Final do arquivo
                }
            }

            if (removed) {
                // Substitui o arquivo original pelo temporário
                if (file.delete()) {
                    tempFile.renameTo(file); // Substitui o arquivo original
                } else {
                    throw new IOException("Falha ao substituir o arquivo.");
                }
            }

            return removed;
        }
    }
    @Override
    public boolean atualizarRegistro(Cliente registroAtualizado) throws IOException, ClassNotFoundException {
        File tempFile = new File("tempfile.dat");
        try (ObjectInputStream tempInput = new ObjectInputStream(new FileInputStream(file));
             ObjectOutputStream tempOutput = new ObjectOutputStream(new FileOutputStream(tempFile))) {

            boolean updated = false;
            Cliente cliente;

            while (true) {
                try {
                    cliente = (Cliente) tempInput.readObject();
                    if (cliente.getNome().equals(registroAtualizado.getNome())) {
                        tempOutput.writeObject(registroAtualizado); // Substitui o cliente no arquivo temporário
                        updated = true;
                    } else {
                        tempOutput.writeObject(cliente); // Copia o cliente sem alterações
                    }
                } catch (EOFException e) {
                    break; // Final do arquivo
                }
            }

            if (updated) {
                // Substitui o arquivo original pelo temporário
                if (file.delete()) {
                    tempFile.renameTo(file); // Substitui o arquivo original
                } else {
                    throw new IOException("Falha ao substituir o arquivo.");
                }
            }

            return updated;
        }
    }



    // Método para atualizar um cliente no arquivo
    public boolean atualizarCliente(Cliente clienteAtualizado) throws IOException, ClassNotFoundException {
        File tempFile = new File("tempfile.dat");
        try (ObjectInputStream tempInput = new ObjectInputStream(new FileInputStream(file));
             ObjectOutputStream tempOutput = new ObjectOutputStream(new FileOutputStream(tempFile))) {

            boolean updated = false;
            Cliente cliente;

            while (true) {
                try {
                    cliente = (Cliente) tempInput.readObject();
                    if (cliente.getNome().equals(clienteAtualizado.getNome())) {
                        tempOutput.writeObject(clienteAtualizado); // Substitui o cliente no arquivo temporário
                        updated = true;
                    } else {
                        tempOutput.writeObject(cliente); // Copia o cliente sem alterações
                    }
                } catch (EOFException e) {
                    break; // Final do arquivo
                }
            }

            if (updated) {
                // Se o cliente foi atualizado, substituímos o arquivo original pelo temporário
                if (file.delete()) {
                    tempFile.renameTo(file); // Substitui o arquivo original
                } else {
                    throw new IOException("Falha ao substituir o arquivo.");
                }
            }

            return updated;
        }
    }
}
