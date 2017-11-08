/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.TException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.metadata.AnnotationMetadataFactory;
import edu.jhu.hlt.concrete.services.store.StoreServiceWrapper;
import edu.jhu.hlt.concrete.services.store.StoreTool;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;
import edu.jhu.hlt.stretcher.manager.LockingManager;
import edu.jhu.hlt.stretcher.manager.Manager;
import edu.jhu.hlt.stretcher.storage.PrintingPersister;

public class ConcurrentClientTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentClientTest.class);

  // the point of this test is to demonstrate that multiple concurrent clients can
  // hit store, without the service crashing.
  @Test
  public void concurrentClients() throws InterruptedException {
    int procs = Runtime.getRuntime().availableProcessors();
    if (procs < 2) {
      fail("get better computer");
    }
    LOGGER.info("Num processors: " + procs);

    Runnable backendRunnable = () -> {
      // use PrintingPersister to get access to the stored comms method
      try(PrintingPersister backend = new PrintingPersister();
          Manager mgr = new LockingManager(new NoOpSource(), backend);) {
        StoreCommunicationService.Iface impl = mgr.getStoreImpl();
        try (StoreServiceWrapper wrap = new StoreServiceWrapper(impl, 44444);) {
          LOGGER.info("about to serve");
          wrap.run();
        } catch (TException e) {
          LOGGER.error("ex in server", e);
        }
      } catch (InterruptedException e1) {
        LOGGER.info("Interrupted", e1);
      } catch (Exception e2) {
        LOGGER.error("Exception on close");
        fail();
      }
    };

    Thread server = new Thread(backendRunnable, "ServerThread");
    server.start();
    // hack to give store service a chance to run before hitting it
    Thread.sleep(500);

    AnnotationMetadata amd = AnnotationMetadataFactory.fromCurrentLocalTime("test");
    Communication c = new Communication();
    c.setId("foo1");
    c.setUuid(UUIDFactory.newUUID());
    c.setText("hello");
    c.setMetadata(amd);
    c.setType("no");

    AtomicBoolean failures = new AtomicBoolean(false);
    List<Communication> comms = ImmutableList.of(c, c, c, c);
    int concurrency = procs < 4 ? procs : 4;
    Runnable parallelTool = () -> {
      try(StoreTool tool = new StoreTool("localhost", 44444, Optional.empty());) {
        LOGGER.info("Starting");
        for (Communication cc : comms) {
          LOGGER.info("Storing");
          tool.store(cc);
        }
        LOGGER.info("Done");
      } catch (TException e) {
        LOGGER.info("Failed to hit store service", e);
        failures.set(true);
      }
    };

    List<Thread> clients = new ArrayList<>();
    for (int i = 0; i < concurrency; i++) {
      Thread t = new Thread(parallelTool, "Client" + i);
      t.start();
      clients.add(t);
    }

    for (Thread t : clients)
      t.join();
    server.interrupt();
    server.join();

    assertFalse(failures.get());
  }
}
