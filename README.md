# Playwright vs Selenium Benchmark

This repository is a structured benchmark comparing test automation performance between:

- **Playwright with TypeScript**
- **Selenium with Java**

## Sobre este projeto

Este projeto compara duas abordagens de automação de testes web usando o mesmo cenário funcional no site Sauce Demo. Ele mede tempo de execução, consumo de CPU e memória, além de mostrar como cada ferramenta se comporta em testes de login, fluxo de compra, espera por elementos dinâmicos e interações com múltiplos elementos.

## Goals

- Compare test execution times
- Compare cold-start setup time and stability
- Keep a consistent scenario across both implementations
- Measure with repeatable runs and captured results

## Test Scenarios

Using the Sauce Demo site (https://www.saucedemo.com), the benchmark includes:

🔹 **CT01 — Login válido**
- Valid authentication with standard_user/secret_sauce
- Expected: Redirect to inventory page

🔹 **CT02 — Login inválido**
- Invalid credentials
- Expected: Error message displayed

🔹 **CT03 — Fluxo completo (E2E)**
- Login → Add to cart → Checkout → Complete purchase
- Expected: Order confirmation

🔹 **CT04 — Elemento dinâmico (espera)**
- Wait for dynamic inventory loading
- Expected: 6 products loaded

🔹 **CT05 — Interação com múltiplos elementos**
- Filter/sort products, add multiple items
- Expected: Cart with 2 items

## Execution Configuration

**🎭 Playwright:**
- Each scenario runs **5 times** (`--repeat-each=5`)
- Total: **25 test executions**
- Parallel execution with 4 workers

**🤖 Selenium:**
- Each scenario runs **5 times** (`@RepeatedTest(5)`)
- Total: **25 test executions**
- Sequential execution per test method

## Project structure

- `playwright-ts/` — Playwright test project in TypeScript
- `selenium-java/` — Selenium test project in Java
- `scripts/` — helper scripts to run both benchmarks and collect output
- `results/` — benchmark run outputs

## How to run

From the repository root:

```bash
./scripts/run-benchmarks.sh
```

Then inspect `results/playwright-results.txt` and `results/selenium-results.txt`.

## Expected Results

The script will show:
- Individual execution times for each framework
- Total execution time comparison
- CPU and memory usage analysis
- Performance ratio (which framework is faster)
- Detailed logs in result files

## Resource Metrics

Benchmark results now include resource consumption:
- CPU utilization percent for each benchmark run
- Maximum resident memory usage for each framework

These metrics help compare not only speed, but also how efficiently each tool uses system resources.

## Notes

- Both frameworks execute **25 tests total** for fair comparison
- Playwright uses parallel workers, Selenium runs sequentially
- Results include setup time, browser launch, and test execution
- Compare both raw performance and developer experience
