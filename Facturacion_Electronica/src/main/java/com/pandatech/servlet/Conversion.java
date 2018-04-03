/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandatech.servlet;

//import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.util.Base64;


/**
 *
 * @author Emmanuel Guzman
 */
public class Conversion {

    public String decode(String s) throws UnsupportedEncodingException {
        String respuesta = "error";
        try {
            respuesta = new String(Base64.getDecoder().decode(s.getBytes("UTF8")),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public String encode(String s) {
        String respuesta = "error";
         try {
            respuesta = new String(Base64.getEncoder().encode(s.getBytes("UTF8")),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
         return respuesta;
    }
}
