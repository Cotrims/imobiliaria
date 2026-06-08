document.addEventListener("DOMContentLoaded", function () {
  const modal = document.getElementById("delete-modal");
  const cancelButton = document.getElementById("delete-modal-cancel");
  const confirmButton = document.getElementById("delete-modal-confirm");

  let deleteUrl = "";

  if (!modal || !cancelButton || !confirmButton) {
    return;
  }

  function openModal(url) {
    deleteUrl = url;
    modal.classList.remove("hidden");
    modal.classList.add("flex");
  }

  function closeModal() {
    deleteUrl = "";
    modal.classList.add("hidden");
    modal.classList.remove("flex");
  }

  document.querySelectorAll("[data-delete-url]").forEach(function (button) {
    button.addEventListener("click", function () {
      openModal(button.getAttribute("data-delete-url"));
    });
  });

  cancelButton.addEventListener("click", closeModal);

  confirmButton.addEventListener("click", function () {
    if (deleteUrl) {
      window.location.href = deleteUrl;
    }
  });

  modal.addEventListener("click", function (event) {
    if (event.target === modal) {
      closeModal();
    }
  });

  document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
      closeModal();
    }
  });
});
