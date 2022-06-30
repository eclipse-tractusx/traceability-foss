# Product traceability foss backend

## Running tests

### Unit tests

```
./gradlew test
```

### Integration tests

```
./gradlew integrationTest
```

## Generating OpenAPI clients
OpenAPI clients are generated based on openapi specification see [dir](./openapi) and correlated gradle tasks see [configuration](build.gradle.kts).

OpenAPI tasks have dependency on JavaCompile task that is OpenAPI client classes are automatically generated once you build the project
and there should not be a need to generate them manually, however following command can be used for such purpose:
```
./gradlew generateBpnApi
```

## Coding styles

To maintain coding styles we utilize [EditorConfig](https://editorconfig.org/) tool, see [configuration](.editorconfig)
file for the details.

### IDE plugins

* IntelliJ IDEA ships with built-in support for .editorconfig files
* [Eclipse plugin](https://github.com/ncjones/editorconfig-eclipse#readme)
* [Visual studio code plugin](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)
