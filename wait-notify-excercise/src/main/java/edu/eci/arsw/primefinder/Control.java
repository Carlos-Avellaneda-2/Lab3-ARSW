/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;
import java.util.LinkedList;

/**
 *
 */
public class Control extends Thread {
    
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;
    private final PauseController pauseController = new PauseController();

    private final int NDATA = MAXVALUE / NTHREADS;
    private final int QUEUE_LIMIT = 100; // Bounded queue size
    private final LinkedList<Integer> queue = new LinkedList<>();

    private PrimeFinderThread[] pft;
    private static Control instance;

    private Control() {
        super();
        instance = this;
        this.pft = new PrimeFinderThread[NTHREADS];
        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA, pauseController);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1, pauseController);
    }

    public static Control getInstance() {
        return instance;
    }

    public void produce(int prime) throws InterruptedException {
        synchronized (queue) {
            while (queue.size() >= QUEUE_LIMIT) {
                queue.wait(); 
            }
            queue.add(prime);
            queue.notifyAll();
        }
    }

    public int consume() throws InterruptedException {
        synchronized (queue) {
            while (queue.isEmpty()) {
                queue.wait(); 
            }
            int prime = queue.removeFirst();
            queue.notifyAll();
            return prime;
        }
    }
    
    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {
        for (int i = 0; i < NTHREADS; i++) {
            pft[i].start();
        }

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    int prime = consume();
                    System.out.println(prime);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
    }
    
}
