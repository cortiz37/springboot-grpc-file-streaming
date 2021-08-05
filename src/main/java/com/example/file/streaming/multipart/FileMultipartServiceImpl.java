package com.example.file.streaming.multipart;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@GRpcService
@Slf4j
public class FileMultipartServiceImpl extends FileMultipartServiceGrpc.FileMultipartServiceImplBase {

    private final String workingDirectory;

    public FileMultipartServiceImpl(@Value("${files.working.directory}") String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public StreamObserver<MultipartRequest> upload(final StreamObserver<MultipartResponse> responseObserver) {
        return new StreamObserver<>() {
            final Map<String, BufferedOutputStream> outputStreams = new HashMap<>();

            @Override
            public void onNext(MultipartRequest request) {
                if (request.hasForm()) {
                    final Form form = request.getForm();
                    log.info("\nprocessing form data: [" + form.getId() + "," + form.getValue() + "," + form.getMessage() + "]\n");
                } else {
                    final File file = request.getFile();
                    byte[] data = file.getData().toByteArray();
                    String name = file.getFilename();
                    try {
                        if (!outputStreams.containsKey(name)) {
                            outputStreams.put(name, new BufferedOutputStream(new FileOutputStream(workingDirectory + '/' + name)));
                        }

                        log.info("CURRENT THREAD: " + Thread.currentThread().getId() + " => " + outputStreams.get(name).toString() + ":" + name);

                        outputStreams.get(name).write(data);
                        outputStreams.get(name).flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(MultipartResponse.newBuilder().setStatus(200).setMessage("multipart completed!").build());
                responseObserver.onCompleted();
                if (!outputStreams.isEmpty()) {
                    try {
                        for (BufferedOutputStream outputStream : outputStreams.values()) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } finally {
                        outputStreams.clear();
                    }
                }
            }
        };
    }
}