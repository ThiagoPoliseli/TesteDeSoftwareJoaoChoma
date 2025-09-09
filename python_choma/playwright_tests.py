from playwright.sync_api import sync_playwright

# Definição dos sites, usuários e seletores
SITES = [
    {
        "name": "SauceDemo",
        "url": "https://www.saucedemo.com",
        "username": "standard_user",
        "password": "secret_sauce",
        "selectors": {
            "user": "#user-name",
            "pass": "#password",
            "login": "#login-button",
            "success": ".inventory_list",
            "menu": "#react-burger-menu-btn",
            "logout": "#logout_sidebar_link",
            "error": "h3[data-test='error']"
        }
    },
    {
        "name": "Herokuapp",
        "url": "https://the-internet.herokuapp.com/login",
        "username": "tomsmith",
        "password": "SuperSecretPassword!",
        "selectors": {
            "user": "#username",
            "pass": "#password",
            "login": "button[type='submit']",
            "success": "h2",
            "logout": "a[href='/logout']",
            "error": "#flash"
        }
    },
    {
        "name": "PracticeTest",
        "url": "https://practicetestautomation.com/practice-test-login/",
        "username": "student",
        "password": "Password123",
        "selectors": {
            "user": "#username",
            "pass": "#password",
            "login": "#submit",
            "success": ".post-title",
            "logout": None,
            "error": "#error"
        }
    },
    {
        "name": "OrangeHRM",
        "url": "https://opensource-demo.orangehrmlive.com/",
        "username": "Admin",
        "password": "admin123",
        "selectors": {
            "user": "input[name='username']",
            "pass": "input[name='password']",
            "login": "button[type='submit']",
            "success": "header .oxd-topbar-header",
            "menu": ".oxd-userdropdown-name",
            "logout": "a[href*='logout']",
            "error": ".oxd-alert-content"
        }
    }
]

def run_tests():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)
        page = browser.new_page()

        for site in SITES:
            print(f"\n=== Testando {site['name']} ===")

            page.goto(site["url"])
            page.fill(site["selectors"]["user"], site["username"])
            page.fill(site["selectors"]["pass"], site["password"])
            page.click(site["selectors"]["login"])
            try:
                page.wait_for_selector(site["selectors"]["success"], timeout=5000)
                print(f"✅ {site['name']} - Login válido funcionou")
            except:
                print(f"❌ {site['name']} - Falha no login válido")

            
            if site["selectors"].get("logout"):
                try:
                    if site["selectors"].get("menu"):
                        page.click(site["selectors"]["menu"])
                    page.click(site["selectors"]["logout"])
                    page.wait_for_selector(site["selectors"]["login"], timeout=5000)
                    print(f"✅ {site['name']} - Logout funcionou")
                except:
                    print(f"⚠️ {site['name']} - Logout não disponível")

            
            page.goto(site["url"])
            page.fill(site["selectors"]["user"], "usuario_errado")
            page.fill(site["selectors"]["pass"], "senha_errada")
            page.click(site["selectors"]["login"])
            try:
                page.wait_for_selector(site["selectors"]["error"], timeout=5000)
                print(f"✅ {site['name']} - Mensagem de erro exibida no login inválido")
            except:
                print(f"❌ {site['name']} - Não exibiu erro no login inválido")

        browser.close()

if __name__ == "__main__":
    run_tests()
