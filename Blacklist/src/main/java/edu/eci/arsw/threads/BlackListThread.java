package edu.eci.arsw.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class BlackListThread extends Thread {
    private int initIndex;
    private int endIndex;
    private String ipaddress;
    private List<Integer> blackListOcurrences;
    private AtomicInteger globalOcurrences;
    private AtomicInteger checkedListsCount;
    private int ocurrencesCount;
    private int alarmCount;
    private volatile boolean stopRequested = false;

    /**
     * Constructor for BlackListThread
     * @param initIndex Starting index of the blacklist segment to check
     * @param endIndex Ending index of the blacklist segment to check
     * @param ipaddress IP address to validate
     * @param globalOcurrences AtomicInteger shared among threads
     * @param alarmCount Threshold to stop search
     */
    public BlackListThread(int initIndex, int endIndex, String ipaddress, AtomicInteger globalOcurrences, AtomicInteger checkedListsCount, int alarmCount) {
        this.initIndex = initIndex;
        this.endIndex = endIndex;
        this.ipaddress = ipaddress;
        this.blackListOcurrences = new LinkedList<>();
        this.globalOcurrences = globalOcurrences;
        this.checkedListsCount = checkedListsCount;
        this.ocurrencesCount = 0;
        this.alarmCount = alarmCount;
    }

    public void requestStop() {
        stopRequested = true;
    }

    @Override
    public void run() {
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        for (int i = initIndex; i < endIndex && !stopRequested; i++) {
            if (globalOcurrences.get() >= alarmCount) {
                break;
            }
            checkedListsCount.incrementAndGet();
            if (skds.isInBlackListServer(i, ipaddress)) {
                blackListOcurrences.add(i);
                ocurrencesCount++;
                if (globalOcurrences.incrementAndGet() >= alarmCount) {
                    break;
                }
            }
        }
    }

    public int getOcurrencesCount() {
        return ocurrencesCount;
    }

    public List<Integer> getBlackListOcurrences() {
        return blackListOcurrences;
    }
}
