package com.barao;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cadastro {
    String conta;
    String senha;
    String cpf;
    String nome;
    String endereco;
    String telefone;
}
