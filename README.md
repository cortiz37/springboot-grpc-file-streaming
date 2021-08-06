## springboot-grpc-filestreaming

How to stream big files using gRPC + SpringBoot. (without set maxInboundMessageSize property)

#### Build

- unix: `./gradlew build`
- windows: `gradlew.bat build` 

#### Run

- unix: `./gradlew bootRun`
- windows: `gradlew.bat bootRun`

### Config

Check the `application.properties` file to see all the config properties.

Pay attention to this one:
`files.working.directory=/files`
The specified path will be used to 'save' the 'uploads'. Make sure a valid path is being used here.

### Services

- `com.example.file.streaming.FileServiceImpl` from `file_service.proto`:

     Simple file streaming, the client implementation (`com.example.file.streaming.FileServiceClient`) save all files passed as parameters (1 file per request).
     
- `com.example.file.streaming.multipart.FileMultipartServiceImpl` from `file_service_multipart.proto`:
  
     Multipart (data + file) streaming, the client implementation (`com.example.file.streaming.multipart.FileMultipartServiceClient`), process the `data form` and then save all files (1 by 1).
     
**Note** clients are running with the `awaitTermination` call (set to 5 seconds), consider changing this time limit for larger files.
       