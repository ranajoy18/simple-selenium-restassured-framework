# Selenium + REST Assured Automation Framework

A Java test automation framework combining UI (Selenium) and API (REST Assured)
testing under one Maven/TestNG project, built against a demo banking
application ([testerrank.com/banking](https://www.testerrank.com/banking)).

## Tech stack

| Layer         | Tool                              |
|---------------|------------------------------------|
| UI automation | Selenium 4                        |
| API automation| REST Assured                      |
| Test runner   | TestNG (parallel execution)       |
| Reporting     | Allure                            |
| Logging       | Log4j2                            |
| Build/CI      | Maven, GitHub Actions             |

## Architecture

- **Page Object Model** (`pages/`) — each page exposes a fluent API
  (`loginPage.navigateToLoginPage().enterUsername().enterPassword().confirmSignIn()`),
  keeping locators and page interactions out of test classes.
- **Thread-safe driver management** (`driver/DriverFactory.java`) — the
  `WebDriver` lives in a `ThreadLocal`, so `parallel="methods"` in
  `testng.xml` runs tests concurrently without cross-test driver bleed.
  Browser choice (Chrome/Firefox) is driven by `config.properties`.
- **Externalized config** (`config/ConfigReader.java`) — reads from
  `config.properties`, with any value overridable via `-D<key>=<value>` on
  the command line or in CI, without editing the file.
- **Reporting** (`listeners/TestListener.java` + Allure) — a TestNG
  listener logs pass/fail/skip; a screenshot is automatically attached to
  the Allure report for any UI test that fails.

## Project structure

```
src/main/java/com/automation/
  config/     ConfigReader        — properties + system-property overrides
  driver/     DriverFactory       — thread-safe WebDriver lifecycle
  listeners/  TestListener        — TestNG listener (logging)
  pages/      LoginPage, DashboardPage, FundTransferPage, BasePage

src/test/java/com/automation/
  ui/         LoginTest, DashboardTest, BaseTest
  api/        UserApiTest         — REST Assured suite (in progress)
```

## Running the tests

```bash
mvn test                          # headless Chrome (default)
mvn test -Dbrowser=firefox        # run against Firefox instead
mvn test -Dusername=... -Dpassword=...   # override credentials without editing config.properties
```

Tests run in parallel (`parallel="methods"`, 3 threads) as configured in
[testng.xml](testng.xml).

## Reporting

Test results are written to `target/allure-results`. Generate and view the
HTML report locally with:

```bash
mvn io.qameta.allure:allure-maven:report   # generates target/site/allure-maven-plugin
mvn io.qameta.allure:allure-maven:serve    # generate + open in browser
```

Failed UI tests carry an attached screenshot under the test's tear-down
step in the report.

## CI

[.github/workflows/automation-tests.yaml](.github/workflows/automation-tests.yaml)
runs the suite on `workflow_dispatch` and uploads the generated Allure
report as a downloadable workflow artifact, whether the run passes or
fails.

## Roadmap

The API layer (`UserApiTest`) is being built out next: REST Assured request/response
specs, POJO (de)serialization, and JSON schema validation against a dedicated
demo API.
