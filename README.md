# Custom Lint Rules

A sample project showcasing the creation of custom Lint rules.

Here we tried 2 approaches:

* Creating a `:rules` module that can be used to our `:app` module, containing a simple rule that creates a Lint error every time the developer names a class with `Dealer` word;

* Creating a library which contains custom Lint rules. Our sample Library is about creating a Certificate providing the hash and an optional expiration date. The developer is given a warning if a certificate is about to be expired, and an error if it expired.

## The Rules module

A basic approach to custom Lint rules, is to have a separate module (in our case, `:rules`) where these rules are created. With this, our `:app` module (or any other android module) can integrate the rules define in the `:rules`module through the following gradle code:

```kotlin
lintChecks project(path: ':rules')
``` 

## The Library

Instead of an app, we could be developing a Library, and still wanting to provide the consumers the ability to receive custom Lint checks when working with the library classes.

For this to work we needed two separate modules: A rules module and a library module. In our example, we created a ´:certificaterule´ module containing our rules, and a `:certificates` module which is our Library to be used.

For this to work, we needed to make sure that our Library was publishing the lint rules from the `:certificaterule` module by adding in the library's `build.gradle`:

```kotlin
lintPublish project(path: ':certificaterule')
```

Also for test purposes, we used Maven Local to export our Library and importing it in our `:app` module.

## Testing

We added a small test to showcase how we can test custom Lint Rules.

For this we used `lint-tests` and `JUnit5`:

```
testImplementation "com.android.tools.lint:lint-tests:$lintVersion"
testImplementation "org.junit.jupiter:junit-jupiter-api:$junit5Version"
testRuntimeOnly "org.junit.jupiter:junit-jupiter-engine:$junit5Version"
```

The `lint-tests` library gives us tools that simplify creating and asserting rules, by extending our test classes with `LintDetectorTest`.

You can find our test example [here](https://github.com/joaocruz04/CustomLintRules/blob/master/rules/src/test/java/com/joaocruz04/customlintrules/rules/DealerDetectorTest.kt)
