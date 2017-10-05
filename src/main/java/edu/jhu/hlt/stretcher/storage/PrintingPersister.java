package edu.jhu.hlt.stretcher.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.transport.TMemoryBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import edu.jhu.hlt.concrete.Communication;

public class PrintingPersister implements Persister {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrintingPersister.class);

  private final ListeningExecutorService srv;
  private int nCommsStored = 0;

  public PrintingPersister() {
    this.srv = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
  }

  public int getCommsStored() {
    return nCommsStored;
  }

  @Override
  public void close() throws InterruptedException {
    this.srv.shutdown();
    LOGGER.info("shutdown triggered, awaiting task termination");
    this.srv.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    LOGGER.info("shutdown complete; persisted {} comms", this.nCommsStored);
  }

  @Override
  public void store(Communication c) {
    this.srv.submit(() -> {
      MDC.put("id", c.getId());
      TMemoryBuffer buf = new TMemoryBuffer(1024 * 8);
      TJSONProtocol jp = new TJSONProtocol(buf);
      try {
        c.write(jp);
        byte[] json = buf.getArray();
        try(ByteArrayInputStream bin = new ByteArrayInputStream(json);
            InputStreamReader rdr = new InputStreamReader(bin, StandardCharsets.UTF_8);) {
          String jsonStr = IOUtils.toString(rdr);
          System.out.println(jsonStr);
        } catch (IOException e) {
          LOGGER.warn("IOException during processing", e);
        }
      } catch (TException e) {
        LOGGER.warn("Comm was messed up", e);
      }

      LOGGER.info("Stored comm");
      nCommsStored++;
    });
  }
}
