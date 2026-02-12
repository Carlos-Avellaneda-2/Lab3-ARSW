/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

public class CountThread extends Thread {
    private int A;
    private int B;
    private String name;

    public CountThread(int A, int B, String name){
        this.A = A;
        this.B = B;
        this.name = name;
    }

    @Override
    public void run(){
        System.out.println(name + " iniciado (estado: RUNNABLE)");
        for(int i = A; i <= B; i++){
            System.out.println(name + ": " + i);
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                System.out.println(name + " Interrumpido");
                return;
            }
        }
        System.out.println(name + " terminado");
    }
}
