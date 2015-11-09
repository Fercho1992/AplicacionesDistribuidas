/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

/**
 *
 * @author 22B
 */
public abstract class Triangulo implements InterfazFiguras {
    
    private Double base;
    
    private Double altura;

    public Triangulo(Double lado, Double altura) {
        this.base = lado;
        this.altura = altura;
    }

    public Double getLado() {
        return base;
    }

    public void setLado(Double lado) {
        this.base = lado;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }
    
    
    public Double getArea(){
        double resultado = 0.0;
        resultado = (base * altura)/2;
        return resultado;
    }
    
    public Double getPerimetro(){
        double resultado = 0.0;
        resultado = (base * altura)/2;
        return resultado;
    }
    
}
