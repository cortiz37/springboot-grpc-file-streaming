package com.example.file.streaming.multipart;

import com.example.file.util.FileUtils;
import com.example.file.util.GrpcUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class FileMultipartServiceClient {

    public static void main(final String[] args) throws InterruptedException {
        if(args == null || args.length == 0) {
            throw new IllegalArgumentException("provide (at least 1) filepath as argument");
        }

        final ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6065).usePlaintext().build();
        final FileMultipartServiceGrpc.FileMultipartServiceStub stub = FileMultipartServiceGrpc.newStub(managedChannel);

        Map<String, Object> form = new HashMap<>();
        form.put("id", 2);
        form.put("message", "sample with id = 2");
        form.put("value", "message with 2 attachments");
        uploadFile(
            stub,
            form,
            new ArrayList<>(Arrays.asList(args))
        );

        managedChannel.awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void uploadFile(final FileMultipartServiceGrpc.FileMultipartServiceStub stub, final Map<String, Object> form, final List<String> filepaths) {
        StreamObserver<MultipartRequest> requestObserver = stub.upload(GrpcUtils.getDefaultStreamObserver());

        try {
            MultipartRequest formPart = MultipartRequest.newBuilder()
                .setForm(
                    Form.newBuilder()
                        .setId(Integer.parseInt(form.getOrDefault("id", 0).toString()))
                        .setMessage(form.getOrDefault("message", "_no_message").toString())
                        .setValue(form.getOrDefault("value", "_no_value").toString())
                        .build()
                ).build();

            requestObserver.onNext(formPart);

            while (!filepaths.isEmpty()) {
                final String filepath = filepaths.remove(0);

                FileUtils.streamFile(filepath, requestObserver, (filename, byteString) -> MultipartRequest.newBuilder()
                    .setFile(
                        File.newBuilder()
                            .setFilename(filename)
                            .setData(byteString)
                            .build()
                    ).build()
                );
            }
        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
        requestObserver.onCompleted();
    }
}