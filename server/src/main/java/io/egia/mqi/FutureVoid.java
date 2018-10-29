package io.egia.mqi;

import java.util.concurrent.CompletableFuture;

public class FutureVoid {

    public static CompletableFuture<Void> get() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Random String");
        CompletableFuture<Void> future = completableFuture.thenRun(System.out::println);
        return future;
    }
}
