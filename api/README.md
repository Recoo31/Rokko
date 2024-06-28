# Roxio Api

Api to be used for plugin and test.

## Implementation

To use the **Api** library, add the following dependency to your project:

```kt
dependencies {
    implementation("com.github.Recoo31:Rokko:-SNAPSHOT")
}
```

## PluginTester Usage
```kt
fun main() {
    runBlocking {
        val tester = ProviderTester(YourPlugin())

        tester.testAll() // test all methods

        tester.testGetHomeScreenItems() // test home screen
    }
}
```