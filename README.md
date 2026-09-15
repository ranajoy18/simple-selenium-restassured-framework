# Selenium + REST Assured Automation Framework

A Java test automation framework combining UI (Selenium) and API (REST Assured)
testing under one Maven/TestNG project, built against a demo banking
application ([testerrank.com/banking](https://www.testerrank.com/banking)).

## Tech stack

| Layer         | Tool                              |
|---------------|------------------------------------|
| UI automation | Selenium 4                        |
| API automation| REST Assured + Jackson (POJOs)    |
| Test runner   | TestNG (parallel execution)       |
| Reporting     | Allure                            |
| Logging       | Log4j2                            |
| Build/CI      | Maven, GitHub Actions             |

## Architecture

- **Page Object Model** (`pages/`) — each page exposes a fluent API
  (`loginPage.navigateToLoginPage().enterUsername().enterPassword().confirmSignIn()`),
  keeping locators and page interactions out of test classes. `BasePage`
  centralizes waits, JS-based clicks (avoids clicks landing behind a sticky
  header on longer pages), typing (clears the field first), and driving
  React-controlled range sliders (`setSliderValue`, used by the loan
  calculator) so every page object gets these for free.
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
- **API client** (`api/BankingApiClient.java`) — a thin REST Assured
  wrapper around the Banking API (login, authenticated requests), reused
  across the API test classes so the base URI/headers/auth-token wiring
  live in one place. Request/response POJOs live in `api/model/`.

## Project structure

```
src/main/java/com/automation/
  config/     ConfigReader        — properties + system-property overrides
  driver/     DriverFactory       — thread-safe WebDriver lifecycle
  listeners/  TestListener        — TestNG listener (logging)
  pages/      BasePage, LoginPage, DashboardPage, FundTransferPage,
              BeneficiaryPage, BillPaymentPage, FixedDepositPage,
              LoanCalculatorPage
  api/        BankingApiClient    — REST Assured client (auth, accounts)
  api/model/  LoginRequest, Account

src/test/java/com/automation/
  ui/         LoginTest, DashboardTest, FundTransferTest, BeneficiaryTest,
              BillPaymentTest, FixedDepositTest, LoanCalculatorTest, BaseTest
  api/        AuthApiTest, AccountsApiTest
```

### UI coverage

Login (valid/invalid), dashboard balance, the full fund-transfer wizard
(type → beneficiary → amount → review → OTP, including invalid-OTP
rejection) with a real balance-delta assertion, adding a beneficiary
(including the IFSC → bank-name auto-fill), an electricity bill payment,
opening a fixed deposit with its maturity amount verified against the
app's actual quarterly-compounding formula, and the loan EMI calculator
verified against the standard reducing-balance formula across several
input combinations (driven via range sliders).

### API coverage

Built against the real Banking API at `https://www.testerrank.com/api/practice/banking`
(verified directly, not from its docs — see note below):

- **Auth** (`AuthApiTest`) — valid login, and 8 negative scenarios (wrong
  email/password, empty/missing fields, empty body), asserting the real
  status codes and error messages.
- **Accounts** (`AccountsApiTest`) — valid/no-auth/invalid-token access,
  field-level validation (balance as `BigDecimal`), and response
  deserialization into an `Account` POJO.

> The API's own docs (`/api-testing-guide`) describe a `{"username":...}`
> request and a differently-shaped response; the live API actually expects
> `{"email":...}` and returns `{"success","data":{...}}` with no `role`
> field. The client and tests are built against the verified live
> behavior, not the docs.

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
runs the suite on `workflow_dispatch` and publishes the generated Allure
report two ways, whether the run passes or fails:

- as a downloadable workflow artifact, and
- as a live report on GitHub Pages:
  **https://ranajoy18.github.io/simple-selenium-restassured-framework/**
  (updated on every run — no download/serve step needed).

> One-time setup: in the repo's **Settings → Pages**, set "Build and
> deployment" → **Source** to **GitHub Actions**. Until that's set, the
> `deploy` job will fail even though tests and report generation succeed.

## Roadmap

API layer, in progress:

- [x] Auth (`AuthApiTest`)
- [x] Accounts (`AccountsApiTest`)
- [ ] Fund transfer (`POST /transactions` with `type: "transfer"` — there's
      no separate `/transfer` endpoint; the source account is derived from
      the JWT, not a request field)
- [ ] End-to-end transfer + balance-update assertion (`BigDecimal`)
- [ ] Transaction history (`GET /transactions`)
- [ ] Full login → transfer → balance → history workflow test
