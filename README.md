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
  `WebDriver` lives in a `ThreadLocal`, so the UI suite runs
  `parallel="methods"` (3 threads) without cross-test driver bleed, since
  each thread drives its own independent browser session. Browser choice
  (Chrome/Firefox) is driven by `config.properties`.
- **API tests run sequentially, deliberately** — every API test class
  shares one demo account on a Vercel-hosted mock backend whose state isn't
  reliably consistent under concurrent requests (observed stale balance
  reads under parallel load — likely per-serverless-instance in-memory
  state rather than a real shared datastore). `testng.xml` scopes
  `parallel="methods"` to the UI `<test>` block only; the API `<test>`
  block runs single-threaded, and `TransferApiTest` additionally sets
  `singleThreaded = true` so its own balance-mutating methods can't race
  each other either.
- **Externalized config** (`config/ConfigReader.java`) — reads from
  `config.properties`, with any value overridable via `-D<key>=<value>` on
  the command line or in CI, without editing the file.
- **Reporting** (`listeners/TestListener.java` + Allure) — a TestNG
  listener logs pass/fail/skip; a screenshot is automatically attached to
  the Allure report for any UI test that fails.
- **API client** (`api/BankingApiClient.java`) — a thin REST Assured
  wrapper around the Banking API (login, accounts, transfers, transaction
  history), reused across the API test classes so the base URI/headers/
  auth-token wiring live in one place. Request/response POJOs live in
  `api/model/`. Every call is filtered through `allure-rest-assured`, so
  the full HTTP request/response is attached to Allure automatically —
  no manual logging needed to debug a failing API test from the report.

## Project structure

```
src/main/java/com/automation/
  config/     ConfigReader        — properties + system-property overrides
  driver/     DriverFactory       — thread-safe WebDriver lifecycle
  listeners/  TestListener        — TestNG listener (logging)
  pages/      BasePage, LoginPage, DashboardPage, FundTransferPage,
              BeneficiaryPage, BillPaymentPage, FixedDepositPage,
              LoanCalculatorPage, TransactionsPage
  api/        BankingApiClient    — REST Assured client (auth, accounts,
              transfers, transaction history)
  api/model/  LoginRequest, Account, TransferRequest, Transaction

src/test/java/com/automation/
  ui/         LoginTest, DashboardTest, FundTransferTest, BeneficiaryTest,
              BillPaymentTest, FixedDepositTest, LoanCalculatorTest,
              TransactionHistoryTest, BaseTest
  api/        AuthApiTest, AccountsApiTest, TransferApiTest,
              TransactionHistoryApiTest
```

### UI coverage

Login (valid/invalid), dashboard balance, the full fund-transfer wizard
(type → beneficiary → amount → review → OTP, including invalid-OTP
rejection) with a real balance-delta assertion, adding a beneficiary
(including the IFSC → bank-name auto-fill), an electricity bill payment,
opening a fixed deposit with its maturity amount verified against the
app's actual quarterly-compounding formula, the loan EMI calculator
verified against the standard reducing-balance formula across several
input combinations (driven via range sliders), and an end-to-end test
confirming a completed transfer actually shows up in the Transactions page.

### API coverage

Built against the real Banking API at `https://www.testerrank.com/api/practice/banking`
(verified directly, not from its docs — see note below):

- **Auth** (`AuthApiTest`) — valid login, and 8 negative scenarios (wrong
  email/password, empty/missing fields, empty body), asserting the real
  status codes and error messages.
- **Accounts** (`AccountsApiTest`) — valid/no-auth/invalid-token access,
  field-level validation (balance as `BigDecimal`), and response
  deserialization into an `Account` POJO.
- **Fund transfer** (`TransferApiTest`) — a successful transfer with a real
  balance-delta assertion, negative cases (zero/negative amount, over-balance,
  missing fields, no auth) against the real error messages, the app's
  actual (lenient) handling of a nonexistent destination account, a check
  that a rejected transfer creates no transaction record, and a full
  login → transfer → balance → history end-to-end workflow test.
- **Transaction history** (`TransactionHistoryApiTest`) — read-only checks:
  valid/no-auth access and response deserialization into a `List<Transaction>`.

> The API's own docs (`/api-testing-guide`) describe a `{"username":...}`
> request and a differently-shaped response; the live API actually expects
> `{"email":...}` and returns `{"success","data":{...}}` with no `role`
> field. There's also no separate `/transfer` endpoint or source-account
> request field — transfers go through `POST /transactions`
> (`type: "transfer"`) and the source account is always the authenticated
> user's own. The client and tests are built against this verified live
> behavior, not the docs.

## Running the tests

```bash
mvn test                          # headless Chrome (default)
mvn test -Dbrowser=firefox        # run against Firefox instead
mvn test -Dusername=... -Dpassword=...   # override credentials without editing config.properties
```

The UI suite runs in parallel (`parallel="methods"`, 3 threads); the API
suite runs sequentially. See [testng.xml](testng.xml) and the note above
on why API tests aren't parallelized.

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

- [x] Auth API (`AuthApiTest`)
- [x] Accounts API (`AccountsApiTest`)
- [x] Fund transfer API, incl. balance-update and history assertions (`TransferApiTest`)
- [x] Transaction history API (`TransactionHistoryApiTest`)
- [x] UI end-to-end: transfer → transaction history (`TransactionHistoryTest`)
- [ ] Remaining granular negative-transfer cases from the original spec not
      yet covered individually (a representative subset is already tested)
