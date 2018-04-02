/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandatech.bean;

/**
 *
 * @author Emmanuel GR
 */
public class IdentificacionReceptor {
    private String tipoIdentificacion;
    private String numeroIdentificacion;
        
    
    public IdentificacionReceptor() {
    }
    
    public String getTipoIdentificacion(){
        return tipoIdentificacion;
    }
    
    public void setTipoIdentificacion(String n){
        this.tipoIdentificacion = n;
    }
    
    public String getNumeroIdentificacion(){
        return numeroIdentificacion;
    }
    
    public void setNumeroIdentificacion(String n){
        this.numeroIdentificacion = n;
    }
}
