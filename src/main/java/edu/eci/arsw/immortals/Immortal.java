package edu.eci.arsw.immortals;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import edu.eci.arsw.concurrency.PauseController;

public final class Immortal implements Runnable {
  private final String name;
  private int health;
  private final int damage;
  private final List<Immortal> population;
  private final ScoreBoard scoreBoard;
  private final PauseController controller;
  private volatile boolean running = true;
  private final ReentrantLock healthLock = new ReentrantLock();

  public Immortal(String name, int health, int damage, List<Immortal> population, ScoreBoard scoreBoard, PauseController controller) {
    this.name = Objects.requireNonNull(name);
    this.health = health;
    this.damage = damage;
    this.population = Objects.requireNonNull(population);
    this.scoreBoard = Objects.requireNonNull(scoreBoard);
    this.controller = Objects.requireNonNull(controller);
  }

  public String name() { return name; }
  
  public int getHealth() {
    healthLock.lock();
    try { return health; }
    finally { healthLock.unlock(); }
  }
  
  public boolean isAlive() { return getHealth() > 0 && running; }
  public void stop() { running = false; }

  @Override public void run() {
    try {
      while (running && this.isAlive()) {
        controller.awaitIfPaused();
        if (!running || !this.isAlive()) break;
        var opponent = pickOpponent();
        if (opponent == null || !opponent.isAlive()) continue;
        String mode = System.getProperty("fight", "ordered");
        if ("naive".equalsIgnoreCase(mode)) fightNaive(opponent);
        else fightOrdered(opponent);
        Thread.sleep(2);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }

  private Immortal pickOpponent() {
    if (population.size() <= 1) return null;
    Immortal other;
    do {
      other = population.get(ThreadLocalRandom.current().nextInt(population.size()));
    } while (other == this);
    return other;
  }

  private void fightNaive(Immortal other) {
    other.healthLock.lock();
    try {
      this.healthLock.lock();
      try {
        if (this.health <= 0 || other.health <= 0) return;
        other.health -= this.damage;
        this.health += this.damage / 2;
        scoreBoard.recordFight();
      } finally {
        this.healthLock.unlock();
      }
    } finally {
      other.healthLock.unlock();
    }
  }

  private void fightOrdered(Immortal other) {
    Immortal first, second;
    if (this.name.compareTo(other.name) < 0) {
      first = this;
      second = other;
    } else {
      first = other;
      second = this;
    }
    
    first.healthLock.lock();
    try {
      second.healthLock.lock();
      try {
        if (this.health <= 0 || other.health <= 0) return;
        other.health -= this.damage;
        this.health += this.damage / 2;
        scoreBoard.recordFight();
      } finally {
        second.healthLock.unlock();
      }
    } finally {
      first.healthLock.unlock();
    }
  }
}
