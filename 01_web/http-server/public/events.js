let count = 0;
const counterEl = document.querySelector('[data-id="counter"]');
const incEl = document.querySelector('[data-action="inc"]');

incEl.addEventListener('click', () => {
   count++;
   counterEl.textContent = `${count}`;
});
