# Reposilite Webhooks

A Reposilite plugin for sending webhooks from our internal instance. Currently, only Discord is supported. Support for other systems may be added as needed.

## Compiling

This project uses **Gradle** and requires **JDK 17** or newer (the compiled JARs require JDK 11 or newer to run). To build it, clone the repository and run:

```bash
./gradlew clean build
```

On Windows, use:

```bat
gradlew.bat clean build
```

The compiled JAR file will be located in the `build/libs` directory.

## License

This project is licensed under the **Apache License 2.0** License - see the [LICENSE](LICENSE) file for details.