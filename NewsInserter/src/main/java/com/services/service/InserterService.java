package com.services.service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

//@WebService
//@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface InserterService {
    // @WebMethod
    public String insert(String newsJson);
}
