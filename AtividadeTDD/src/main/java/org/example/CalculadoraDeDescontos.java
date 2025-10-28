package org.example;

public class CalculadoraDeDescontos {

    public double calcular(double valorCompra) {
        if (valorCompra < 0) {
            throw new IllegalArgumentException("Valor da compra não pode ser negativo.");
        }

        if (valorCompra < 100.0) {
            return 0.0;
        } else if (valorCompra <= 500.0) {
            return valorCompra * 0.05;
        } else {
            return valorCompra * 0.10;
        }
    }
}