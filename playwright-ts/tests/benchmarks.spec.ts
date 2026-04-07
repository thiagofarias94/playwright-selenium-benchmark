import { test, expect } from '@playwright/test';

test.describe('Sauce Demo Benchmark Scenarios', () => {
  test('CT01 — Login válido', async ({ page }) => {
    await page.goto('https://www.saucedemo.com');
    await page.fill('#user-name', 'standard_user');
    await page.fill('#password', 'secret_sauce');
    await page.click('#login-button');
    await expect(page).toHaveURL('https://www.saucedemo.com/inventory.html');
  });

  test('CT02 — Login inválido', async ({ page }) => {
    await page.goto('https://www.saucedemo.com');
    await page.fill('#user-name', 'invalid_user');
    await page.fill('#password', 'wrong_password');
    await page.click('#login-button');
    await expect(page.locator('[data-test="error"]')).toBeVisible();
  });

  test('CT03 — Fluxo completo (E2E)', async ({ page }) => {
    // Login
    await page.goto('https://www.saucedemo.com');
    await page.fill('#user-name', 'standard_user');
    await page.fill('#password', 'secret_sauce');
    await page.click('#login-button');

    // Add product to cart
    await page.click('[data-test="add-to-cart-sauce-labs-backpack"]');

    // Go to cart
    await page.click('.shopping_cart_link');

    // Checkout
    await page.click('[data-test="checkout"]');

    // Fill checkout form
    await page.fill('[data-test="firstName"]', 'John');
    await page.fill('[data-test="lastName"]', 'Doe');
    await page.fill('[data-test="postalCode"]', '12345');
    await page.click('[data-test="continue"]');

    // Complete purchase
    await page.click('[data-test="finish"]');
    await expect(page.locator('.complete-header')).toHaveText('Thank you for your order!');
  });

  test('CT04 — Elemento dinâmico (espera)', async ({ page }) => {
    await page.goto('https://www.saucedemo.com');
    await page.fill('#user-name', 'standard_user');
    await page.fill('#password', 'secret_sauce');
    await page.click('#login-button');

    // Wait for inventory to load dynamically
    await page.waitForSelector('.inventory_item', { timeout: 10000 });
    await expect(page.locator('.inventory_item')).toHaveCount(6);
  });

  test('CT05 — Interação com múltiplos elementos', async ({ page }) => {
    await page.goto('https://www.saucedemo.com');
    await page.fill('#user-name', 'standard_user');
    await page.fill('#password', 'secret_sauce');
    await page.click('#login-button');

    // Filter products by price low to high
    await page.selectOption('.product_sort_container', 'lohi');

    // Add multiple products to cart
    await page.click('[data-test="add-to-cart-sauce-labs-backpack"]');
    await page.click('[data-test="add-to-cart-sauce-labs-bike-light"]');

    // Check cart badge
    await expect(page.locator('.shopping_cart_badge')).toHaveText('2');
  });
});
