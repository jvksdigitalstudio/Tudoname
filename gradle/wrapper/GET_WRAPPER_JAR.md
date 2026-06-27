# gradle-wrapper.jar

The `gradle-wrapper.jar` is not tracked in git (binary file).

## For local development — run once:
```bash
gradle wrapper --gradle-version=8.7
```

## Or download manually:
```
https://github.com/gradle/gradle/raw/v8.7.0/gradle/wrapper/gradle-wrapper.jar
```
Place it in: `gradle/wrapper/gradle-wrapper.jar`

## GitHub Actions
The CI workflow downloads it automatically via curl — no manual step needed.
