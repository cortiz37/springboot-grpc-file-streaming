package com.example.file.streaming;

import com.example.file.util.FileUtils;
import com.example.file.util.GrpcUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class FileServiceClient {

    public static void main(final String[] args) throws InterruptedException {
        if(args == null || args.length == 0) {
            throw new IllegalArgumentException("provide (at least 1) filepath as argument");
        }

        final ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6065).usePlaintext().build();
        final FileServiceGrpc.FileServiceStub stub = FileServiceGrpc.newStub(managedChannel);

        for (String arg : args) {
            uploadFile(stub, arg);
        }

        managedChannel.awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void uploadFile(final FileServiceGrpc.FileServiceStub stub, final String filepath) {
        StreamObserver<FileRequest> requestObserver = stub.upload(GrpcUtils.getDefaultStreamObserver());
        try {
            FileUtils.streamFile(filepath, requestObserver, (filename, byteString) -> FileRequest.newBuilder()
                .setFilename(filename)
                .setData(byteString)
                .build()
            );
        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
        requestObserver.onCompleted();
    }
}