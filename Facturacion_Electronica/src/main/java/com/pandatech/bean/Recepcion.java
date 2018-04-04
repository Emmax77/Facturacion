package com.pandatech.bean;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Emmanuel GR
 */
public class Recepcion implements Serializable {
    private String clave;
    private String fecha;
    private Object emisor;
    private Object receptor;
    private String comprobanteXml;
    
    
    public Recepcion() {
    }
    
    public String getClave(){
        return clave;
    }
    
    public void setClave(String n){
        this.clave = n;
    }
    
    
    
    public String getFecha(){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        fecha = f.format(new Date());
        return fecha;
    }
    
    
    public void setFecha(){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        fecha = f.format(new Date());
    }
    /*
    public void setFecha(String n){
        this.fecha = n;
    }
    */
    
    public Object getIdentificacionEmisor(){
        return emisor;
    }
    
    public void setIdentificacionEmisor(IdentificacionEmisor n){
        this.emisor = n;
    }
    
    public Object getIdentificacionReceptor(){
        return receptor;
    }
    
    public void setIdentificacionReceptor(IdentificacionReceptor n){
        this.receptor = n;
    }
    
    public String getComprobanteXml(){
        return comprobanteXml;
    }
    
    public void setComprobanteXml(String xml){
        this.comprobanteXml = xml;
    }
    
}
