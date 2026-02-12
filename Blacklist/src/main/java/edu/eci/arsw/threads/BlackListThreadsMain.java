/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

import java.util.List;
import edu.eci.arsw.blacklistvalidator.HostBlackListsValidatorThread;

/**
 * Main class to test the parallel blacklist validation
 * @author hcadavid
 */
public class BlackListThreadsMain {
    
    public static void main(String[] args) {

        HostBlackListsValidatorThread validator = new HostBlackListsValidatorThread();

        int[] threadCounts = {1, 2, 4, 8};
        
        System.out.println("=== Buscando coincidencias en la ip ===");
        testIP("200.24.34.55", validator, threadCounts);
        
        System.out.println("\n=== Buscando coincidencias en la ip ===");
        testIP("202.24.34.55", validator, threadCounts);
        
        System.out.println("\n=== Buscando coincidencias en la ip ===");
        testIP("212.24.24.55", validator, threadCounts);
    }
    
    private static void testIP(String ipAddress, HostBlackListsValidatorThread validator, int[] threadCounts) {
        for (int numThreads : threadCounts) {
            long startTime = System.currentTimeMillis();
            List<Integer> result = validator.checkHost(ipAddress, numThreads);
            long endTime = System.currentTimeMillis();
            
            System.out.println("Threads: " + numThreads + " | Found in blacklists: " + result + 
                             " | Time: " + (endTime - startTime) + "ms");
        }
    }
}
