(() => {
  const menu = document.getElementById("menuToggle");
  const sidebar = document.getElementById("sidebar");
  menu?.addEventListener("click", () => sidebar?.classList.toggle("open"));

  document.querySelectorAll("[data-modal-open]").forEach(button => {
    button.addEventListener("click", () => {
      const modal = document.getElementById(button.dataset.modalOpen);
      modal?.classList.add("open");
    });
  });
  document.querySelectorAll("[data-modal-close]").forEach(button => {
    button.addEventListener("click", () => button.closest(".modal-backdrop")?.classList.remove("open"));
  });
  document.querySelectorAll(".modal-backdrop").forEach(modal => {
    modal.addEventListener("click", event => {
      if (event.target === modal) modal.classList.remove("open");
    });
  });
  document.querySelectorAll("form[data-confirm]").forEach(form => {
    form.addEventListener("submit", event => {
      if (!window.confirm(form.dataset.confirm)) event.preventDefault();
    });
  });
  document.querySelectorAll(".alert-close").forEach(button => {
    button.addEventListener("click", () => button.closest(".alert")?.remove());
  });
  document.querySelectorAll(".nav-link[href^='#']").forEach(link => {
    link.addEventListener("click", () => {
      document.querySelectorAll(".nav-link").forEach(item => item.classList.remove("active"));
      link.classList.add("active");
      sidebar?.classList.remove("open");
    });
  });
})();
