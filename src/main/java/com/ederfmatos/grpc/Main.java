package com.ederfmatos.grpc;

import com.ederfmatos.grpc.service.ProductService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new ProductService())
                .build();

        server.start();

        System.out.printf("Serve started on %d", server.getPort());

        server.awaitTermination();
    }

}
