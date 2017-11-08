/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import edu.jhu.hlt.concrete.services.fetch.FetchServiceWrapper;
import edu.jhu.hlt.concrete.services.store.StoreServiceWrapper;
import edu.jhu.hlt.stretcher.file.SimpleFileEngine;
import edu.jhu.hlt.stretcher.manager.DirectLockingManager;
import edu.jhu.hlt.stretcher.manager.Manager;


public class Server {
  private static Logger LOGGER = LoggerFactory.getLogger(Server.class);

  private static Server server;
  private FetchImpl fetchImpl;
  private FetchServiceWrapper fetchServer;
  private StoreImpl storeImpl;
  private StoreServiceWrapper storeServer;
  private final int fetchPort;
  private final int storePort;
  private final Path baseDir;

  public Server(int fetchPort, int storePort, String baseDir) throws IOException {
    this.fetchPort = fetchPort;
    this.storePort = storePort;
    this.baseDir = Paths.get(baseDir);
    SimpleFileEngine engine = new SimpleFileEngine(this.baseDir);
    Manager manager = new DirectLockingManager(engine, engine);
    this.fetchImpl = new FetchImpl(manager);
    this.storeImpl = new StoreImpl(manager);
  }

  public void start() throws TException {
    fetchServer = new FetchServiceWrapper(fetchImpl, fetchPort);
    LOGGER.info("Fetch service is started on port " + fetchPort);
    new Thread(fetchServer).start();
    storeServer = new StoreServiceWrapper(storeImpl, storePort);
    LOGGER.info("Store service is started on port " + storePort);
    new Thread(storeServer).start();
  }

  public void stop() {
    System.out.println();
    fetchServer.close();
    fetchImpl.close();
    storeServer.close();
    storeImpl.close();
    System.out.println("Shut down is complete");
  }

  private static class Opts {
    @Parameter(names = {"--fp"}, description = "The port for fetch.")
    int fetchPort = 9090;

    @Parameter(names = {"--sp"}, description = "The port for store.")
    int storePort = 9091;

    @Parameter(names = {"--dir", "-d"}, required = true,
                    description = "Path to the directory for the files.")
    String baseDir = "/tmp/";

    @Parameter(names = {"--help", "-h"}, help = true,
                    description = "Print the usage information and exit.")
    boolean help;
  }

  public static void main(String[] args) {
    Opts opts = new Opts();
    JCommander jc = null;
    try {
        jc = new JCommander(opts, args);
    } catch (ParameterException e) {
        System.err.println("Error: " + e.getMessage());
        System.exit(-1);
    }
    jc.setProgramName("./start.sh");
    if (opts.help) {
        jc.usage();
        return;
    }

    // initialize the server
    try {
      server = new Server(opts.fetchPort, opts.storePort, opts.baseDir);
    } catch (IOException e) {
      System.err.println("Error initializing the server: " + e.getMessage());
      System.exit(-1);
    }

    // register shutdown hook so server gracefully shuts down with control-C
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        server.stop();
      }
    });

    // start the services
    try {
      server.start();
    } catch (TException e) {
      System.err.println("Unable to start the fetch server: " + e.getMessage());
      System.exit(-1);
    }
  }
}
