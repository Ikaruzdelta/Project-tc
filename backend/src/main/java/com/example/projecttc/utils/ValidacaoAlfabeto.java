package com.example.projecttc.utils;

import java.util.ArrayList;
import java.util.Set;

public class ValidacaoAlfabeto {

     public static boolean compararAlfabeto(Set<String> set, Set<String> set2){
        ArrayList<String> auxiliar = new ArrayList<String>();
        for(String simbolo : set){
            auxiliar.add(simbolo);
        }
        for(String simbolo : set2){
            if(auxiliar.contains(simbolo)){
                System.out.println("Igual.");
            }else{
                return false;
            }
        }
        return true;
    }
    
}
