/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import edu.eci.arsw.threads.BlackListThread;

public class HostBlackListsValidatorThread {
    private static final int BLACK_LIST_ALARM_COUNT=5;

    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is parallelized across N threads for improved performance.
     * The search stops as soon as BLACK_LIST_ALARM_COUNT is reached.
     * @param ipaddress suspicious host's IP address.
     * @param numThreads number of threads to use for parallel search.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int numThreads) {
        LinkedList<Integer> blackListOcurrences = new LinkedList<>();
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        int registeredServersCount = skds.getRegisteredServersCount();

        AtomicInteger globalOcurrences = new AtomicInteger(0);
        AtomicInteger checkedListsCount = new AtomicInteger(0);
        BlackListThread[] threads = new BlackListThread[numThreads];
        int segmentSize = registeredServersCount / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int initIndex = i * segmentSize;
            int endIndex = (i == numThreads - 1) ? registeredServersCount : (i + 1) * segmentSize;
            threads[i] = new BlackListThread(initIndex, endIndex, ipaddress, globalOcurrences, checkedListsCount, BLACK_LIST_ALARM_COUNT);
            threads[i].start();
        }

        try {
            boolean alarmReached = false;
            while (!alarmReached) {
                for (BlackListThread t : threads) {
                    if (globalOcurrences.get() >= BLACK_LIST_ALARM_COUNT) {
                        alarmReached = true;
                        break;
                    }
                }
                if (!alarmReached) Thread.sleep(5);
            }
            for (BlackListThread t : threads) {
                t.requestStop();
            }
            for (BlackListThread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, "Thread interrupted while checking blacklists", e);
        }

        int totalOcurrences = globalOcurrences.get();
        for (BlackListThread t : threads) {
            blackListOcurrences.addAll(t.getBlackListOcurrences());
        }

        if (totalOcurrences >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount.get(), registeredServersCount});
        return blackListOcurrences;
    }
    
    /**
     * Check the given host's IP address in all the available black lists.
     * This method uses a default number of threads based on available processors.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress) {
        return checkHost(ipaddress, Runtime.getRuntime().availableProcessors());
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
