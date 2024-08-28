package org.acme;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.core.Vertx;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Path("/api")
public class ReactiveGreetingResource {
    @Inject
    Vertx vertex;

    @GET
    @Path("/blocking-hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String blockingGreet() {
        return "Hello reactive World!" + Thread.currentThread().getName();
    }

    @GET
    @Path("/non-blocking-hello")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> nonBlockingGreet() {
        return Uni.createFrom().item(() -> "Hello reactive World!" + Thread.currentThread().getName());
    }

    @GET
    @Path("/lorem")
    public Uni<String> readFile() {
        return vertex.fileSystem().readFile("lorem.txt")
                .onItem().transform(Unchecked.function(buffer -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return buffer.toString("UTF-8");
                }))
                .onFailure().recoverWithItem("Oops")
                .ifNoItem().after(Duration.of(1, ChronoUnit.SECONDS)).fail();
    }
}
