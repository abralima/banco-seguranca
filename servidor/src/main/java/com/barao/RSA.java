package com.barao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RSA {
    final int p;
    final int q;

    int n, phi, e, d;

    public void gerarChaves() {
        n = p * q;
        phi = (p - 1) * (q - 1);
//        System.out.println("the value of n = " + n);
//        System.out.println("the value of phi = " + phi);
        for (e = 2; e < phi; e++) {

            // e is for public key exponent
            if (gcd(e, phi) == 1) {
                break;
            }
        }
//        System.out.println("Valor de e = " + e);
        var i = 1;
        while (true){
            if (e * i % phi == 1){
//                System.out.println("d é: "+i);
                d = i;
                break;
            }
            i++;
        }
//        System.out.println("Valor de d = " + d);
    }

    public long[] criptografar(String mensagem) {
        long[] mensagemCriptografada = new long[mensagem.length()];
        for (int i = 0; i < mensagem.length(); i++) {
//            System.out.println("Char: " + mensagem.charAt(i));
//            System.out.println("Char Value: " + (int) mensagem.charAt(i));
            mensagemCriptografada[i] = (long) Math.pow(mensagem.charAt(i), e) % n;
//            System.out.println("Char criptografado: " + mensagemCriptografada[i]);
        }
        return mensagemCriptografada;
    }

    public String descriptografar(long[] mensagemCriptografada) {
        StringBuilder mensagemDescriptografada = new StringBuilder();
        for (int i = 0; i < mensagemCriptografada.length; i++) {
            BigInteger bigInteger = new BigInteger(String.valueOf(mensagemCriptografada[i]));
            BigInteger bigInteger1 = bigInteger.pow(d);
            BigInteger bigInteger2 = bigInteger1.mod(BigInteger.valueOf(n));
            int valor = bigInteger2.intValue();
            mensagemDescriptografada.append((char) valor);
        }
        return mensagemDescriptografada.toString();
    }

    public String descriptografarComChaveRecebida(long[] mensagemCriptografada, Chave chave) {
        StringBuilder mensagemDescriptografada = new StringBuilder();
        for (int i = 0; i < mensagemCriptografada.length; i++) {
            BigInteger bigInteger = new BigInteger(String.valueOf(mensagemCriptografada[i]));
            BigInteger bigInteger1 = bigInteger.pow(chave.e);
            BigInteger bigInteger2 = bigInteger1.mod(BigInteger.valueOf(chave.n));
            int valor = bigInteger2.intValue();
            mensagemDescriptografada.append((char) valor);
        }
        return mensagemDescriptografada.toString();
    }

    public static int gcd(int e, int phi) {
        if (e == 0) {
            return phi;
        } else {
            return gcd(phi % e, e);
        }
    }

    public Chave getChavePublica(){
        return new Chave(e, n);
    }

    public Chave getChavePrivada(){
        return new Chave(d, n);
    }

    public String mensagemEncriptadaToString(long[] mensagemCriptografada){
        StringBuilder mensagem = new StringBuilder();
        for (int i = 0; i < mensagemCriptografada.length; i++) {
            mensagem.append(mensagemCriptografada[i]).append(" ");
        }
        return mensagem.toString();
    }
}
