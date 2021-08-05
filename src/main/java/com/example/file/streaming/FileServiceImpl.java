package com.example.file.streaming;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@GRpcService
@Slf4j
public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase {

    private final String workingDirectory;

    public FileServiceImpl(@Value("${files.working.directory}") String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public StreamObserver<FileRequest> upload(final StreamObserver<FileResponse> responseObserver) {
        return new StreamObserver<>() {
            BufferedOutputStream outputStream;

            @Override
            public void onNext(FileRequest request) {
                byte[] data = request.getData().toByteArray();
                String name = request.getFilename();
                try {
                    if (outputStream == null) {
                        outputStream = new BufferedOutputStream(new FileOutputStream(workingDirectory + '/' + name));
                    }

                    log.info("CURRENT THREAD: " + Thread.currentThread().getId() + " => " + outputStream.toString() + ":" + name);

                    outputStream.write(data);
                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(FileResponse.newBuilder().setStatus(200).setMessage("upload completed!").build());
                responseObserver.onCompleted();
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        outputStream = null;
                    }
                }
            }
        };
    }
}