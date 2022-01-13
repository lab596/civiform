# File Storage Backend

Civiform currently supports file uploads using AWS S3. There is an effort in progress to add support for Azure Blob Storage. Support for other storage services like Google Cloud Storage is planned. This document will provide an overview of the classes and interfaces that are used to implement support for new storage providers. 


## `StorageClient` interface
The `StorageClient` interface is used to decouple classes for interacting with specific storage providers from the rest of the codebase. New controllers and views should depend on the StorageClient interface rather than one of its implementations. In order to determine which `StorageClient` implementation to use at runtime, we use Guice for dependency injection. The `CloudStorageModule` Guice module reads in the `cloud.storage` property set in `application.conf`, and binds the corresponding implementation to the implementation. For more info on how this works, see the [Guice documentation on bindings](https://github.com/google/guice/wiki/Bindings). 

Each implementation of StorageClient uses an implementation of an inner `Client` interface depending on the environment the application is running in (dev, test, or prod). The inner `Client` implementations for new classes implementing `StorageClient` should have one client that interacts with the emulator, one stub (for unit tests), and one client that interacts with a real instance of the storage backend.

![Class Diagram for StorageClient interface](https://lucid.app/publicSegments/view/d6a2ace0-6669-458b-a21b-a6ee6856b01d/image.png)

## `StorageUploadRequest` interface

Implementations of the `StorageUploadRequest` class hold all the information necessary to upload a file from the browser. Implementations of `StorageClient` are used to generate instances of `StorageUploadRequest` implementations (`BlobStorageUploadRequest`,`SignedS3UploadRequest`) and the generated instances are passed to the render methods in view classes.

![Class diagram for StorageUploadRequest interface](https://lucid.app/publicSegments/view/0bc996ee-f29a-461f-9fa7-46cda44a3261/image.png)

## Strategy Pattern

The controller and view classes that interact with the interfaces above might need to change the behavior of a method (for example, render a different template) depending on which storage provider is being used. To accomplish this, we make use of the [Strategy Pattern](https://en.wikipedia.org/wiki/Strategy_pattern). Each class where the strategy pattern is being used has its own corresponding strategy interface. Implementations are bound to the interface in the `CloudStorageModule` file. Below is an example of how the strategy pattern is used:

![Strategy pattern example](https://lucid.app/publicSegments/view/c4595583-6aba-497f-b71f-cf5a1bd34425/image.png)