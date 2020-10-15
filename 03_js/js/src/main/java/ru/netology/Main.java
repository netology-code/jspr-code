package ru.netology;

import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.EmptyCompletionHandler;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.multipart.MultipartEntry;
import org.glassfish.grizzly.http.multipart.MultipartEntryHandler;
import org.glassfish.grizzly.http.multipart.MultipartScanner;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
  private static final Logger LOGGER = Grizzly.logger(Main.class);

  public static void main(String[] args) throws IOException, InterruptedException {
    final var server = HttpServer.createSimpleServer("static", 9999);
    server.getServerConfiguration().addHttpHandler(new HttpHandler() {
      @Override
      public void service(Request request, Response response) throws Exception {
        response.getWriter().write("ok");
        //        response.suspend();
        //        MultipartScanner.scan(request, multipartEntry -> {
        //          LOGGER.info(multipartEntry.getContentDisposition().getDispositionParam("name"));
        //          // TODO: handle entry
        //        }, new EmptyCompletionHandler<>() {
        //          @Override
        //          public void completed(Request result) {
        //            response.resume();
        //            try {
        //              response.getWriter().write("ok");
        //            } catch (IOException e) {
        //              e.printStackTrace();
        //            }
        //          }
        //          @Override
        //          public void failed(Throwable throwable) {
        //            response.resume();
        //          }
        //        });
      }
    }, "/api");

    server.start();
    Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
    Thread.currentThread().join();
  }
}

