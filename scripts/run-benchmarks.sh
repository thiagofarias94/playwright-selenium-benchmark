#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
RESULT_DIR="$ROOT_DIR/results"

mkdir -p "$RESULT_DIR"

echo "🚀 Iniciando Benchmark: Playwright vs Selenium"
echo "=============================================="

# Function to run command with time measurement
run_with_time() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        time "$@"
    elif /usr/bin/time -l true 2>/dev/null; then
        LC_ALL=C /usr/bin/time -l "$@"
    else
        time -p "$@"
    fi
}

# Capturar tempo inicial
START_TIME=$(date +%s)

echo "⏱️  Executando Playwright TypeScript benchmark..."
cd "$ROOT_DIR/playwright-ts"
npm install > /dev/null 2>&1
PLAYWRIGHT_START=$(date +%s)
run_with_time npm run benchmark 2>&1 | tee "$RESULT_DIR/playwright-results.txt"
PLAYWRIGHT_END=$(date +%s)
PLAYWRIGHT_DURATION=$((PLAYWRIGHT_END - PLAYWRIGHT_START))

echo ""
echo "⏱️  Executando Selenium Java benchmark..."
cd "$ROOT_DIR/selenium-java"
SELENIUM_START=$(date +%s)
run_with_time mvn test -q 2>&1 | tee "$RESULT_DIR/selenium-results.txt"
SELENIUM_END=$(date +%s)
SELENIUM_DURATION=$((SELENIUM_END - SELENIUM_START))

# Capturar tempo final
END_TIME=$(date +%s)
TOTAL_DURATION=$((END_TIME - START_TIME))

echo ""
echo "📊 RESULTADOS FINAIS"
echo "===================="

# Extrair métricas do Playwright
PLAYWRIGHT_TESTS=$(grep "passed" "$RESULT_DIR/playwright-results.txt" | tail -1 | sed 's/.* \([0-9]*\) passed.*/\1/' || echo "N/A")
PLAYWRIGHT_TIME=$(grep "passed" "$RESULT_DIR/playwright-results.txt" | tail -1 | sed 's/.*(\([0-9.]*\)s).*/\1/' || echo "N/A")

# Extrair métricas de sistema (disponível apenas no Linux)
if [[ "$OSTYPE" != "darwin"* ]]; then
    PLAYWRIGHT_CPU=$(grep "percent of CPU this job got" "$RESULT_DIR/playwright-results.txt" | tail -1 | sed 's/.*: //' || echo "N/A")
    PLAYWRIGHT_MEM=$(grep -E "maximum resident set size|max resident set size" "$RESULT_DIR/playwright-results.txt" | tail -1 | sed 's/.*: //' || echo "N/A")
else
    PLAYWRIGHT_CPU="N/A (macOS)"
    PLAYWRIGHT_MEM="N/A (macOS)"
fi

# Extrair métricas do Selenium
SELENIUM_TESTS=$(grep "Tests run:" "$RESULT_DIR/selenium-results.txt" | sed 's/.*Tests run: \([0-9]*\).*/\1/' || echo "N/A")
SELENIUM_TIME=$(grep "Time elapsed:" "$RESULT_DIR/selenium-results.txt" | sed 's/.*Time elapsed: \([0-9.]*\) s.*/\1/' || echo "N/A")

# Extrair métricas de sistema (disponível apenas no Linux)
if [[ "$OSTYPE" != "darwin"* ]]; then
    SELENIUM_CPU=$(grep "percent of CPU this job got" "$RESULT_DIR/selenium-results.txt" | tail -1 | sed 's/.*: //' || echo "N/A")
    SELENIUM_MEM=$(grep -E "maximum resident set size|max resident set size" "$RESULT_DIR/selenium-results.txt" | tail -1 | sed 's/.*: //' || echo "N/A")
else
    SELENIUM_CPU="N/A (macOS)"
    SELENIUM_MEM="N/A (macOS)"
fi

echo "🎭 Playwright: $PLAYWRIGHT_TESTS testes em ${PLAYWRIGHT_TIME}s"
echo "    CPU: ${PLAYWRIGHT_CPU}"
echo "    Memória máxima: ${PLAYWRIGHT_MEM} KB"
echo "🤖 Selenium:   $SELENIUM_TESTS testes em ${SELENIUM_TIME}s"
echo "    CPU: ${SELENIUM_CPU}"
echo "    Memória máxima: ${SELENIUM_MEM} KB"
echo "⏱️  Tempo total: ${TOTAL_DURATION}s"

echo ""
if (( $(echo "$PLAYWRIGHT_TIME < $SELENIUM_TIME" | bc -l) )); then
    SPEEDUP=$(echo "scale=2; $SELENIUM_TIME / $PLAYWRIGHT_TIME" | bc)
    echo "🏆 Playwright foi ${SPEEDUP}x mais rápido!"
else
    SPEEDUP=$(echo "scale=2; $PLAYWRIGHT_TIME / $SELENIUM_TIME" | bc)
    echo "🏆 Selenium foi ${SPEEDUP}x mais rápido!"
fi

echo ""
echo "✅ Benchmark concluído! Resultados salvos em $RESULT_DIR"
