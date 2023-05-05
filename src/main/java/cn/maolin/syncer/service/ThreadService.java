// ==========================
// Copyright (c) 2023-04-24 Sioux
// All rights reserved.
// ==========================
package cn.maolin.syncer.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadService {

  private final ExecutorService executor;

  public ThreadService(int size) {
    executor = Executors.newFixedThreadPool(size);
  }

  public void submit(Runnable task) {
    executor.submit(task);
  }
}
