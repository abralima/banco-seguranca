package com.barao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conta implements Serializable {
    private String conta;
    private Double saldo;
    private Double investimento;

    public Conta (String conta, Double saldo) {
        this.conta = conta;
        this.saldo = saldo;
        this.investimento = 0.0;
    }
}
