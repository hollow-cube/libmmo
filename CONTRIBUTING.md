# Contributing Guidelines
todo


## Code Style
There is an attached `.editorconfig` file for basic formatting rules.

We also make use of [Error Prone](https://errorprone.info) and [NullAway](https://github.com/uber/NullAway),
these warnings should be respected or explicitly ignored if there is a good reason. Parameters and return types
should have nullability annotations using Jetbrains Annotations. Other annotations should be used where they
make sense.


## Testing
All contributions are expected to be reasonably tested. Unit test classes should be named `TestX`, integration
tests should be named `TestXIntegrataion`. Any test using the Minestom test framework (any test annotated with
`@EnvTest`), the test should be appropriately marked as an integration test.

We use [Truth](https://truth.dev) for assertions. Subjects should be added in repeated cases where an error
would become more clear. There is a guide on this [here](https://truth.dev/extension).
