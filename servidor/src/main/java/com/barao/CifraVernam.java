package com.barao;

public class CifraVernam {
    public static String cifrar(String mensagem, String chave) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < mensagem.length(); i++) {
            char caractere = mensagem.charAt(i);

            char chaveChar = chave.charAt(i % chave.length());

            char cifrado = (char) (caractere ^ chaveChar);
            resultado.append(cifrado);
        }
        return resultado.toString();
    }
    public static String decifrar(String mensagemCifrada, String chave) {

        return cifrar(mensagemCifrada, chave);
    }
}
