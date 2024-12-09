package org.example;

import java.io.Serializable;


public class Cliente implements Serializable{
    private String nome;
    private String sobrenome;
    private String endereco;
    private String telefone;

    private int creditScore;


    public Cliente(String nome, String sobrenome, String endereco, String telefone, int creditScore) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.creditScore = creditScore;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTelefone() {
        return telefone;
    }


    public String getSobrenome() {
        return sobrenome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }


    public void setCreditScore(int creditScore) {
        if (creditScore < 0 || creditScore > 100) {
            throw new IllegalArgumentException("Credit score deve ser entre 0 e 100.");
        }
        this.creditScore = creditScore;
    }

    // Exibir as informações do cliente

    public void printCliente(){
        System.out.println("Nome: " + getNome());
        System.out.println("Sobrenome: " + getSobrenome());
        System.out.println("Endereco: " + getEndereco());
        System.out.println("Telefone: " + getTelefone());
        System.out.println("CreditScore: " + getCreditScore());
    }

}
