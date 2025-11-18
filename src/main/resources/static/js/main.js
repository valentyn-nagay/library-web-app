document.addEventListener("DOMContentLoaded", () => {
    // 1. Подтверждение для "опасных" действий (logout, возврат книги и т.п.)
    document.querySelectorAll("form[data-confirm]").forEach(form => {
        form.addEventListener("submit", (e) => {
            const message = form.dataset.confirm || "Вы уверены?";
            if (!window.confirm(message)) {
                e.preventDefault();
            }
        });
    });

    // 2. Поиск по таблицам
    document.querySelectorAll("input[data-table-filter]").forEach(input => {
        const selector = input.dataset.tableFilter;
        const table = document.querySelector(selector);
        if (!table) {
            return;
        }
        const tbody = table.querySelector("tbody");
        if (!tbody) {
            return;
        }

        input.addEventListener("input", () => {
            const term = input.value.trim().toLowerCase();
            const rows = Array.from(tbody.querySelectorAll("tr"));

            rows.forEach(row => {
                const text = row.innerText.toLowerCase();
                row.style.display = term && !text.includes(term) ? "none" : "";
            });
        });
    });

    // 3. Показ/скрытие пароля
    document.querySelectorAll("[data-toggle='password']").forEach(button => {
        const targetSelector = button.dataset.target;
        const input = document.querySelector(targetSelector);
        if (!input) {
            return;
        }

        button.addEventListener("click", () => {
            const isPassword = input.type === "password";
            input.type = isPassword ? "text" : "password";
            button.textContent = isPassword ? "Скрыть" : "Показать";
        });
    });

    // 4. Авто-скрытие уведомлений
    document.querySelectorAll(".alert[data-auto-hide]").forEach(alert => {
        const timeout = Number(alert.dataset.autoHide) || 4000;
        setTimeout(() => {
            alert.classList.add("alert-hidden");
        }, timeout);
    });

    // 5. Кнопка "наверх"
    const backToTop = document.getElementById("backToTop");
    if (backToTop) {
        window.addEventListener("scroll", () => {
            const show = window.scrollY > 200;
            backToTop.classList.toggle("show", show);
        });

        backToTop.addEventListener("click", () => {
            window.scrollTo({ top: 0, behavior: "smooth" });
        });
    }
});
