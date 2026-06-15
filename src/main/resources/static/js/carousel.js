document.querySelectorAll("[data-carousel]").forEach((carousel) => {
  const slides = carousel.querySelectorAll(".carousel-slide");
  const prevButton = carousel.querySelector("[data-carousel-prev]");
  const nextButton = carousel.querySelector("[data-carousel-next]");

  let currentIndex = 0;

  function showSlide(index) {
    slides[currentIndex].classList.add("hidden");

    currentIndex = (index + slides.length) % slides.length;

    slides[currentIndex].classList.remove("hidden");
  }

  prevButton?.addEventListener("click", (event) => {
    event.preventDefault();
    showSlide(currentIndex - 1);
  });

  nextButton?.addEventListener("click", (event) => {
    event.preventDefault();
    showSlide(currentIndex + 1);
  });
});
