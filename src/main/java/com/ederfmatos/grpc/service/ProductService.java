package com.ederfmatos.grpc.service;

import generated.CreateProductRequest;
import generated.CreateProductResponse;
import generated.Empty;
import generated.InactiveProductRequest;
import generated.ProductListItem;
import generated.ProductListResponse;
import generated.ProductServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final List<Product> products = new ArrayList<>();

    record Product(String id, String name, Integer amount, String category) {
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<CreateProductResponse> responseObserver) {
        System.out.println("Initializing creation for product " + request);
        Product product = new Product(
                UUID.randomUUID().toString(),
                request.getName(),
                request.getAmount(),
                request.getCategory()
        );

        products.add(product);

        CreateProductResponse createProductResponse = CreateProductResponse.newBuilder()
                .setId(product.id)
                .setName(product.name)
                .setAmount(product.amount)
                .setCategory(product.category)
                .build();

        responseObserver.onNext(createProductResponse);
        responseObserver.onCompleted();

        System.out.println("Finished creation for product " + request);
    }

    @Override
    public void listAllProducts(Empty request, StreamObserver<ProductListResponse> responseObserver) {
        System.out.println("Initializing listing for products");
        ProductListResponse.Builder builder = ProductListResponse.newBuilder();

        products.forEach(product -> builder.addProducts(ProductListItem.newBuilder()
                .setId(product.id)
                .setName(product.name)
                .setAmount(product.amount)
                .setCategory(product.category)
                .build())
        );

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        System.out.println("Finished listing for products");
    }

    @Override
    public void inactiveProduct(InactiveProductRequest request, StreamObserver<Empty> responseObserver) {
        System.out.println("Initializing inactivation for product " + request);
        String productId = request.getProductId();

        products.stream()
                .filter(product -> product.id.equals(productId))
                .findFirst()
                .ifPresentOrElse(o -> {
                    products.remove(o);
                    responseObserver.onNext(Empty.newBuilder().build());
                }, () -> responseObserver.onError(new StatusException(Status.fromCodeValue(Status.Code.INVALID_ARGUMENT.value()).withDescription("Product not found"))));

        responseObserver.onCompleted();
        System.out.println("Finished inactivation for product " + request);
    }

}
