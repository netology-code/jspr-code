console.log('js executed');

const form = document.getElementById('form');
form.addEventListener('submit', (evt) => {
  evt.preventDefault();

  // {
  //   // GET с Query URL
  //   const data = new URLSearchParams();
  //   Array.from(form.elements)
  //     .filter((el) => el.name !== '') // только с атрибутом name
  //     .forEach((el) => data.append(el.name, el.value));
  //
  //   const xhr = new XMLHttpRequest();
  //   xhr.open('GET', `/api?${data}`);
  //   xhr.send();
  //   form.reset(); // очистка формы
  // }

  // {
  //   // POST
  //   const data = new URLSearchParams();
  //   Array.from(form.elements)
  //     .filter((el) => el.name !== '') // только с атрибутом name
  //     .forEach((el) => data.append(el.name, el.value));
  //
  //   const xhr = new XMLHttpRequest();
  //   xhr.open('POST', `/api`);
  //   xhr.send(data);
  //   form.reset();
  // }

  // {
  //   // Multipart
  //   const data = new FormData(form);
  //   const xhr = new XMLHttpRequest();
  //   xhr.open('POST', `/api`);
  //   xhr.send(data);
  //   form.reset();
  // }

  // {
  //   const data = new Blob(["some data"]);
  //   const xhr = new XMLHttpRequest();
  //   xhr.open('POST', `/api`);
  //   xhr.send(data);
  // }

  // {
  //   const data = JSON.stringify({key: 'value'});
  //   const xhr = new XMLHttpRequest();
  //   xhr.open('POST', `/api`);
  //   xhr.setRequestHeader('Content-Type', 'application/json');
  //   xhr.send(data);
  // }

  {
    // SOP Demo
    const data = JSON.stringify({key: 'value'});
    const xhr = new XMLHttpRequest();
    xhr.open('POST', `http://localhost:9999/api`);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
  }
});

