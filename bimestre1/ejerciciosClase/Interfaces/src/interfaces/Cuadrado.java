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
public abstract class Cuadrado implements InterfazFiguras {
    
    private Double lado;

    public Cuadrado() {
    }

    public Cuadrado(Double lado) {
        this.lado = lado;
    }

    public Double getLado() {
        return lado;
    }

    public void setLado(Double lado) {
        this.lado = lado;
    }
    
    
    public Double getArea(){
        double resultado = 0.0;
        resultado = lado * lado;
        return resultado;
    }
    
    public Double getPerimetro(){
        double resultado = 0.0;
        resultado = 4*(lado);
        return resultado;
    }
    
}
