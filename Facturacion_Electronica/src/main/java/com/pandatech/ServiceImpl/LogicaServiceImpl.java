/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandatech.ServiceImpl;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
@WebService
public class LogicaServiceImpl {
    
    @WebMethod
    public String sayHello(String name){
        return "Hello " + name + "!";
    }
    
    
    

    
    
    
    
    
    
    
    
    
    
    
}