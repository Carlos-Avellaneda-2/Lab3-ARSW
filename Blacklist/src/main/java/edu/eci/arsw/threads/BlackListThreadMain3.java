package edu.eci.arsw.threads;

import edu.eci.arsw.blacklistvalidator.HostBlackListsValidatorThread;
import java.util.List;



public class BlackListThreadMain3 {
    public static void main(String[] args) {

        HostBlackListsValidatorThread validator = new HostBlackListsValidatorThread();

        Runtime runtime = Runtime.getRuntime();
        int nucleos = runtime.availableProcessors();
        int[] threadCounts = {1, nucleos, nucleos*2, 50, 100};

        System.out.println("=== Buscando coincidencias en la ip  ===");
        testIP("202.24.34.55",validator,threadCounts);
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
