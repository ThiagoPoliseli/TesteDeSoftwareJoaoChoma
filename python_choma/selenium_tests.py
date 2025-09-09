from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time


def setup_driver():
    """Configura e retorna o ChromeDriver atualizado"""
    options = webdriver.ChromeOptions()
    options.add_argument("--start-maximized")
    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
    return driver


# ========== 1. SAUCE DEMO ==========
def test_saucedemo(driver):
    print("\n=== Testando SauceDemo ===")
    driver.get("https://www.saucedemo.com")

    # Login válido
    driver.find_element(By.ID, "user-name").send_keys("standard_user")
    driver.find_element(By.ID, "password").send_keys("secret_sauce")
    driver.find_element(By.ID, "login-button").click()
    assert "inventory" in driver.current_url
    print("✅ Login válido OK")

    # Logout
    driver.find_element(By.ID, "react-burger-menu-btn").click()
    time.sleep(1)
    driver.find_element(By.ID, "logout_sidebar_link").click()
    assert "saucedemo.com" in driver.current_url
    print("✅ Logout OK")

    # Login inválido
    driver.find_element(By.ID, "user-name").send_keys("user_errado")
    driver.find_element(By.ID, "password").send_keys("senha_errada")
    driver.find_element(By.ID, "login-button").click()
    error_message = driver.find_element(By.TAG_NAME, "h3").text
    assert "Epic sadface" in error_message
    print("✅ Login inválido OK")


# ========== 2. HEROKUAPP ==========
def test_herokuapp(driver):
    print("\n=== Testando The Internet (Herokuapp) ===")
    driver.get("https://the-internet.herokuapp.com/login")

    # Login válido
    driver.find_element(By.ID, "username").send_keys("tomsmith")
    driver.find_element(By.ID, "password").send_keys("SuperSecretPassword!")
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
    assert "secure" in driver.current_url
    print("✅ Login válido OK")

    # Logout
    driver.find_element(By.CSS_SELECTOR, "a.button").click()
    assert "login" in driver.current_url
    print("✅ Logout OK")

    # Login inválido
    driver.find_element(By.ID, "username").send_keys("errado")
    driver.find_element(By.ID, "password").send_keys("123")
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
    error_message = driver.find_element(By.ID, "flash").text
    assert "Your username is invalid!" in error_message
    print("✅ Login inválido OK")


# ========== 3. PRACTICE TEST AUTOMATION ==========
def test_practice(driver):
    print("\n=== Testando Practice Test Automation ===")
    driver.get("https://practicetestautomation.com/practice-test-login/")

    # Login válido
    driver.find_element(By.ID, "username").send_keys("student")
    driver.find_element(By.ID, "password").send_keys("Password123")
    driver.find_element(By.ID, "submit").click()
    assert "Logged In Successfully" in driver.page_source
    print("✅ Login válido OK")

    # Logout
    driver.find_element(By.LINK_TEXT, "Log out").click()
    assert "practice-test-login" in driver.current_url
    print("✅ Logout OK")

    # Login inválido
    driver.find_element(By.ID, "username").send_keys("errado")
    driver.find_element(By.ID, "password").send_keys("123")
    driver.find_element(By.ID, "submit").click()
    error_message = driver.find_element(By.ID, "error").text
    assert "Your username is invalid!" in error_message or "Your password is invalid!" in error_message
    print("✅ Login inválido OK")


# ========== 4. ORANGE HRM ==========
def test_orangehrm(driver):
    print("\n=== Testando OrangeHRM ===")
    driver.get("https://opensource-demo.orangehrmlive.com/")

    # Login válido
    driver.find_element(By.NAME, "username").send_keys("Admin")
    driver.find_element(By.NAME, "password").send_keys("admin123")
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
    assert "dashboard" in driver.current_url or "orangehrm" in driver.current_url
    print("✅ Login válido OK")

    # Logout
    time.sleep(2)
    driver.find_element(By.CLASS_NAME, "oxd-userdropdown-tab").click()
    time.sleep(1)
    driver.find_element(By.LINK_TEXT, "Logout").click()
    assert "login" in driver.current_url
    print("✅ Logout OK")

    # Login inválido
    driver.find_element(By.NAME, "username").send_keys("user_errado")
    driver.find_element(By.NAME, "password").send_keys("senha_errada")
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
    error_message = driver.find_element(By.CLASS_NAME, "oxd-alert-content-text").text
    assert "Invalid credentials" in error_message
    print("✅ Login inválido OK")


# ========== EXECUÇÃO ==========
def run_tests():
    driver = setup_driver()
    try:
        test_saucedemo(driver)
        test_herokuapp(driver)
        test_practice(driver)
        test_orangehrm(driver)
    finally:
        driver.quit()


if __name__ == "__main__":
    run_tests()
