document.addEventListener("DOMContentLoaded", () => {
    /* ---------- 1. Подтверждение опасных действий ---------- */

    document.querySelectorAll("form[data-confirm]").forEach(form => {
        form.addEventListener("submit", (e) => {
            const message = form.dataset.confirm || "Вы уверены?";
            if (!window.confirm(message)) {
                e.preventDefault();
            }
        });
    });

    /* ---------- 2. Таблицы: поиск + пагинация ---------- */

    const tableStates = new Map();

    function ensureTableState(table) {
        if (tableStates.has(table)) {
            return tableStates.get(table);
        }
        const tbody = table.querySelector("tbody");
        if (!tbody) {
            return null;
        }
        const allRows = Array.from(tbody.querySelectorAll("tr"));
        const state = {
            table,
            tbody,
            allRows,
            filterTerm: "",
            pageSize: allRows.length, // по умолчанию без пагинации
            currentPage: 1,
            paginationContainer: null
        };
        tableStates.set(table, state);
        return state;
    }

    function applyTableState(table) {
        const state = ensureTableState(table);
        if (!state) {
            return;
        }

        const term = state.filterTerm.trim().toLowerCase();
        const filtered = term
            ? state.allRows.filter(row =>
                row.innerText.toLowerCase().includes(term)
            )
            : state.allRows.slice();

        const pageSize = state.paginationContainer ? state.pageSize : filtered.length;
        const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
        state.currentPage = Math.min(Math.max(1, state.currentPage), totalPages);

        // Прячем всё
        state.allRows.forEach(row => {
            row.style.display = "none";
        });

        // Показываем только нужную страницу
        const startIndex = (state.currentPage - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        filtered.slice(startIndex, endIndex).forEach(row => {
            row.style.display = "";
        });

        // Строим пагинацию
        if (state.paginationContainer) {
            const container = state.paginationContainer;
            container.innerHTML = "";

            if (filtered.length === 0) {
                const info = document.createElement("div");
                info.className = "page-info";
                info.textContent = "Нет записей";
                container.appendChild(info);
                return;
            }

            if (totalPages <= 1) {
                // если страниц одна — просто не показываем кнопки
                return;
            }

            const info = document.createElement("div");
            info.className = "page-info";
            info.textContent = `Страница ${state.currentPage} из ${totalPages}`;
            container.appendChild(info);

            const prevBtn = document.createElement("button");
            prevBtn.type = "button";
            prevBtn.className = "btn btn-secondary btn-sm";
            prevBtn.textContent = "‹";
            prevBtn.disabled = state.currentPage === 1;
            prevBtn.addEventListener("click", () => {
                state.currentPage -= 1;
                applyTableState(table);
            });
            container.appendChild(prevBtn);

            for (let page = 1; page <= totalPages; page++) {
                const pageBtn = document.createElement("button");
                pageBtn.type = "button";
                pageBtn.className = "btn btn-secondary btn-sm";
                if (page === state.currentPage) {
                    pageBtn.classList.add("page-active");
                }
                pageBtn.textContent = String(page);
                pageBtn.addEventListener("click", () => {
                    state.currentPage = page;
                    applyTableState(table);
                });
                container.appendChild(pageBtn);
            }

            const nextBtn = document.createElement("button");
            nextBtn.type = "button";
            nextBtn.className = "btn btn-secondary btn-sm";
            nextBtn.textContent = "›";
            nextBtn.disabled = state.currentPage === totalPages;
            nextBtn.addEventListener("click", () => {
                state.currentPage += 1;
                applyTableState(table);
            });
            container.appendChild(nextBtn);
        }
    }

    // Привязываем поиск
    document.querySelectorAll("input[data-table-filter]").forEach(input => {
        const selector = input.dataset.tableFilter;
        const table = document.querySelector(selector);
        if (!table) {
            return;
        }
        const state = ensureTableState(table);
        if (!state) {
            return;
        }

        input.addEventListener("input", () => {
            state.filterTerm = input.value;
            state.currentPage = 1;
            applyTableState(table);
        });

        // При инициализации просто применим состояние
        applyTableState(table);
    });

    // Привязываем пагинацию
    document.querySelectorAll("[data-pagination]").forEach(container => {
        const selector = container.dataset.pagination;
        const table = document.querySelector(selector);
        if (!table) {
            return;
        }
        const state = ensureTableState(table);
        if (!state) {
            return;
        }

        const sizeAttr = container.dataset.pageSize;
        const pageSize = Number.parseInt(sizeAttr, 10);
        state.pageSize = Number.isFinite(pageSize) && pageSize > 0 ? pageSize : 10;
        state.paginationContainer = container;

        applyTableState(table);
    });

    /* ---------- 3. Показ/скрытие пароля ---------- */

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

    /* ---------- 4. Авто-скрытие уведомлений ---------- */

    document.querySelectorAll(".alert[data-auto-hide]").forEach(alert => {
        const timeout = Number(alert.dataset.autoHide) || 4000;
        setTimeout(() => {
            alert.classList.add("alert-hidden");
        }, timeout);
    });

    /* ---------- 5. Кнопка "наверх" ---------- */

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

    /* ---------- 6. Переключатель темы ---------- */

    function applyTheme(theme) {
        const isLight = theme === "light";
        document.body.classList.toggle("theme-light", isLight);

        const label = isLight ? "Тёмная тема" : "Светлая тема";
        document.querySelectorAll("[data-theme-toggle]").forEach(btn => {
            btn.textContent = label;
        });
    }

    const savedTheme = localStorage.getItem("library-theme");
    applyTheme(savedTheme === "light" ? "light" : "dark");

    document.querySelectorAll("[data-theme-toggle]").forEach(button => {
        button.addEventListener("click", () => {
            const isLight = document.body.classList.contains("theme-light");
            const nextTheme = isLight ? "dark" : "light";
            localStorage.setItem("library-theme", nextTheme);
            applyTheme(nextTheme);
        });
    });
});
