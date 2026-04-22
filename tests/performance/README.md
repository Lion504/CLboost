# Performance Testing

This directory contains Apache JMeter test plans for load testing the CL Booster application.

## Test Plan

- **File:** `clboost_performance.jmx`
- **Tool:** Apache JMeter 5.6.3+
- **Scenarios:** Simulates 10 concurrent users navigating through key pages of the app (public and protected).

## Quick Start

1. Ensure the application is running (default `http://localhost:8080` or configure via `-Jport`).
2. Run:

```bash
jmeter -n -t clboost_performance.jmx -l results.jtl -Jport=8080 -e -o report/
```

- `-n` — non-GUI mode
- `-t` — test plan file
- `-l` — raw results log (`.jtl`)
- `-e` — generate HTML dashboard report
- `-o` — output directory for the HTML report

3. Open `report/index.html` in a browser.

## Test Parameters

| Property | Default | Description |
|----------|---------|-------------|
| `host`   | localhost | Target hostname |
| `port`   | 8080 | Target port |

Override via `-Jhost=xxx -Jport=yyy`.

## Test Configuration

- **Threads:** 10 users
- **Ramp-up:** 30 seconds
- **Loop Count:** 5 iterations per user
- **Think Time:** 2000 ms (2 seconds) between requests

## Metrics to Watch

- **Average (Avg):** Desired < 2000 ms
- **Error %:** Should be ≤ 5%
- **Throughput:** requests/second; higher is better

## Limitations

- JMeter tests HTTP endpoints only; does not execute client-side JavaScript. Page load times measured are for the initial server response (HTML bootstrap), not full rendered page with static resources.
- For comprehensive browser-based performance, consider Vaadin TestBench with Selenium Grid.

## CI Integration

The Jenkinsfile defines a **Performance Test** stage that executes this JMeter plan automatically on every build. Results are archived as Jenkins artifacts.

## Troubleshooting

- **"Connection refused":** Ensure the application is running and reachable on the specified host/port.
- **High error rate:** Check for 4xx/5xx responses; verify that static resources exist and routes are accessible.
- **Report generation fails:** Delete existing `report/` directory before re-running with `-e`.
