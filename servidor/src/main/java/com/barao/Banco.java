package com.barao;

import java.util.ArrayList;
import java.util.List;

public class Banco {
    public static List<Cadastro> cadastros = new ArrayList<>();
    public static List<Conta> contas = new ArrayList<>();

    public void addCadastro(Cadastro cadastro) {
        cadastros.add(cadastro);
    }

    public void addConta(Conta conta) {
        contas.add(conta);
    }

    public String checkCadastro(String conta, String senha) {
        for (Cadastro c : cadastros) {
            if (c.getConta().equals(conta)) {
                if(c.getSenha().equals(senha)){
                    return "autenticado";
                }else{
                    return "senha incorreta";
                }
            }
        }
        return "conta não encontrada";
    }

    public boolean sacar(String conta, Double valor) {
        for (Conta c : contas) {
            if (c.getConta().equals(conta)) {
                if (c.getSaldo() >= valor) {
                    c.setSaldo(c.getSaldo() - valor);
                    return true;
                }else {
                    break;
                }
            }
        }
        return false;
    }

    public boolean depositar(String conta, Double valor) {
        for (Conta c : contas) {
            if (c.getConta().equals(conta)) {
                c.setSaldo(c.getSaldo() + valor);
                return true;
            }
        }
        return false;
    }

    public boolean transferir(String contaOrigem, String contaDestino, Double valor) {
        for (Conta c : contas) {
            if (c.getConta().equals(contaOrigem)) {
                if (c.getSaldo() >= valor) {
                    c.setSaldo(c.getSaldo() - valor);
                    for (Conta c2 : contas) {
                        if (c2.getConta().equals(contaDestino)) {
                            c2.setSaldo(c2.getSaldo() + valor);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean investir(String conta, Double valor) {
        System.out.println("Conta" + conta);
        for (Conta c : contas) {
            System.out.println("Conta2" + c.getConta());
            if (c.getConta().equals(conta)) {
                System.out.println("Aqui");
                if (c.getSaldo() >= valor) {
                    System.out.println("Aqui2");
                    c.setSaldo(c.getSaldo() - valor);
                    c.setInvestimento(c.getInvestimento() + valor);
                    return true;
                }
            }
        }
        return false;
    }

    public Double saldo(String conta) {
        for (Conta c : contas) {
            if (c.getConta().equals(conta)) {
                return c.getSaldo();
            }
        }
        return 0.0;
    }

    public void listaConta() {
        for (Conta c : contas) {
            System.out.println(c.getConta() + " " + c.getSaldo());
        }
    }

    public void listaCadastro() {
        for (Cadastro c : cadastros) {
            System.out.println(c.getConta() + " " + c.getNome());
        }
    }

}
