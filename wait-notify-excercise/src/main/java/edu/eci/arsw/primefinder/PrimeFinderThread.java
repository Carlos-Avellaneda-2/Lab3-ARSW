package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;


public class PrimeFinderThread extends Thread{
    int a, b;
    private final PauseController controller;
    private final Control control;
    private List<Integer> primes;

    public PrimeFinderThread(int a, int b, PauseController controller) {
        super();
        this.a = a;
        this.b = b;
        this.primes = new LinkedList<>();
        this.controller = controller;
        this.control = Control.getInstance();
    }

    @Override
    public void run() {
        for (int i = a; i < b; i++) {
            try {
                controller.awaitIfPaused(); 
                if (isPrime(i)) {
                    primes.add(i);
                    control.produce(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
	
	boolean isPrime(int n) {
        boolean ans;
        if (n > 2) { 
            ans = n%2 != 0;
            for(int i = 3;ans && i*i <= n; i+=2 ) {
                ans = n % i != 0;
            }
        } else {
            ans = n == 2;
        }
        return ans;
	}

    public List<Integer> getPrimes() {return primes; }
}
