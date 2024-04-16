package com.barao;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AES {
    private static SecretKeySpec chave;
    private String mensagem;
    private String mensagemCifrada;
    public AES(String chave) {
        gerarChave(chave);
    }
    public void gerarChave(String chaveRecebida) {
        try {
            byte[] salt = new byte[16];
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(chaveRecebida.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            chave = new SecretKeySpec(tmp.getEncoded(), "AES");
            byte[] secureRandom = new byte[64];

            System.out.println("Chave gerada: "
                    + chave.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
    public String cifrar(String textoAberto){
        byte[] bytesMensagemCifrada;
        Cipher cifrador;

        mensagem = textoAberto;
        try {
            cifrador = Cipher
                    .getInstance("AES/ECB/PKCS5Padding");
            cifrador.init(Cipher.ENCRYPT_MODE, chave);
            bytesMensagemCifrada =
                    cifrador.doFinal(mensagem.getBytes());
            mensagemCifrada =
                    Base64
                            .getEncoder()
                            .encodeToString(bytesMensagemCifrada);
            System.out.println(">> Mensagem cifrada = "
                    + mensagemCifrada);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            e.printStackTrace();
        }
        return mensagemCifrada;
    }
    public String decifrar(String textoCifrado) {
        byte [] bytesMensagemCifrada =
                Base64
                        .getDecoder()
                        .decode(textoCifrado);
        Cipher decriptador;
        try {
            decriptador =
                    Cipher.getInstance("AES/ECB/PKCS5Padding");
            decriptador.init(Cipher.DECRYPT_MODE, chave);
            byte[] bytesMensagemDecifrada =
                    decriptador.doFinal(bytesMensagemCifrada);
            String mensagemDecifrada =
                    new String(bytesMensagemDecifrada);
            mensagem = mensagemDecifrada;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            e.printStackTrace();
        }
        return mensagem;
    }

    public String getChave() {
        return chave.toString();
    }
}
