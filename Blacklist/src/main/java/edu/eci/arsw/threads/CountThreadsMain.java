/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author Carlos Avellaneda
 */
public class CountThreadsMain {
    
    public static void main(String a[]){
        CountThread Primero = new CountThread(0,99,"primero");
        CountThread Segundo = new CountThread(99,199,"segundo");
        CountThread Tercero = new CountThread(200,299,"tercero");

        System.out.println("=== USANDO run() ===");
        Primero.run();
        Segundo.run();
        Tercero.run();

        System.out.println("=== USANDO start() ===");
        Primero.start();
        Segundo.start();
        Tercero.start();
    }
    
}
