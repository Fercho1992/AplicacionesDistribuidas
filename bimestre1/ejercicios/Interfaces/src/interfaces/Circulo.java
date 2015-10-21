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
public abstract class Circulo implements InterfazFiguras {
    
    private Double radio;
    
    public Circulo(){
        
    }

    public Circulo(Double radio) {

        this.radio = radio;
    }

    public Double getRadio() {
        return radio;
    }

    public void setRadio(Double radio) {
        this.radio = radio;
    }
    
    @Override
    public Double getArea(){
        double resultado = 0.0;
        
        resultado = Math.PI * Math.pow(radio, 2);
        
        return resultado;
    }
    
    public Double getPerimetro(){
        double resultado = 0.0;
        
        resultado = Math.PI * 2 *radio;
        
        return resultado;
    }
    
}
