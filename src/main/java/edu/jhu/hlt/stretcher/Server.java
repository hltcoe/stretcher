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
import edu.jhu.hlt.stretcher.file.SimpleFileEngine;
import edu.jhu.hlt.stretcher.manager.LockingManager;


public class Server {
  private static Logger LOGGER = LoggerFactory.getLogger(Server.class);

  private FetchImpl fetchImpl;
  private final int fetchPort;
  private final Path baseDir;

  public Server(int fetchPort, String baseDir) throws IOException {
    this.fetchPort = fetchPort;
    this.baseDir = Paths.get(baseDir);
    SimpleFileEngine engine = new SimpleFileEngine(this.baseDir);
    this.fetchImpl = new FetchImpl(new LockingManager(engine, engine));
  }

  public void start() throws TException {
    FetchServiceWrapper fetchServer = new FetchServiceWrapper(fetchImpl, fetchPort);
    LOGGER.info("Fetch service is started on port " + fetchPort);
    new Thread(fetchServer).start();
  }

  private static class Opts {
    @Parameter(names = {"--port", "-p"}, description = "The port for fetch.")
    int port = 9090;

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

    Server server = null;
    try {
      server = new Server(opts.port, opts.baseDir);
    } catch (IOException e) {
      System.err.println("Error initializing the server: " + e.getMessage());
      System.exit(-1);
    }

    try {
      server.start();
    } catch (TException e) {
      System.err.println("Unable to start the fetch server: " + e.getMessage());
      System.exit(-1);
    }
  }
}
