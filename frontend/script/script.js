document.querySelectorAll(".button-group button").forEach((button) => {
  button.addEventListener("click", () => {
    const operation = button.getAttribute("data-operation");
    exibirFormulario(operation);
    document.getElementById("default-message").style.display = "none"; // Esconder a mensagem padrão
  });
});
// Função para exibir o formulário correspondente
function exibirFormulario(operation) {
  const formContainer = document.getElementById("form-container");
  formContainer.innerHTML = ""; // Limpar o conteúdo anterior

  let formHtml = "";

  switch (operation) {
    case "uniao":
    case "intersecao":
    case "diferenca":
    case "diferenca-simetrica":
      formHtml = `
                <h2>${
                  operation.charAt(0).toUpperCase() + operation.slice(1)
                } de Automatos</h2>
                <input type="file" id="file1" accept=".jff" required>
                <input type="file" id="file2" accept=".jff" required>
                <button onclick="aplicarOperacao('${operation}')">Aplicar ${operation}</button>
            `;
      break;
    case "complemento":
    case "estrela":
    case "reverso":
    case "homomorfismo":
      formHtml = `
                <h2>${
                  operation.charAt(0).toUpperCase() + operation.slice(1)
                } de Automato</h2>
                <input type="file" id="file" accept=".jff" required>
                <button onclick="aplicarOperacao('${operation}')">Aplicar ${operation}</button>
            `;
      break;
    case "concatenacao":
      formHtml = `
                <h2>Concatenar Automatos</h2>
                <input type="file" id="file1" accept=".jff" required>
                <input type="file" id="file2" accept=".jff" required>
                <button onclick="aplicarOperacao('${operation}')">Concatenar</button>
            `;
      break;
  }

  formContainer.innerHTML = formHtml;
}
function aplicarOperacao(operation) {
  const formData = new FormData();

  if (
    operation === "uniao" ||
    operation === "intersecao" ||
    operation === "diferenca" ||
    operation === "diferenca-simetrica"
  ) {
    const file1Input = document.getElementById("file1");
    const file2Input = document.getElementById("file2");
    formData.append("file1", file1Input.files[0]);
    formData.append("file2", file2Input.files[0]);
  } else {
    const fileInput = document.getElementById("file");
    formData.append("file", fileInput.files[0]);
  }

  enviarArquivo(formData, `http://localhost:8080/api/automato/${operation}`);
}
///////////////////////////////
// Função para enviar arquivos para o servidor e exibir o resultado
// function enviarArquivo(formData, url, resultElement) {
//   // Limpar o conteúdo anterior antes de começar
//   resultElement.textContent = "Processando...";

//   fetch(url, {
//     method: "POST",
//     body: formData,
//   })
//     .then((response) => {
//       if (response.ok) {
//         return response.text();
//       } else {
//         return response.text().then((text) => {
//           throw new Error(text);
//         });
//       }
//     })
//     .then((data) => {
//       resultElement.textContent = data; // Exibe o resultado do automato retornado pela API
//       // Adicionar link para download se o caminho do arquivo estiver presente
//       const regex = /Arquivo salvo em: (.*)/;
//       const match = data.match(regex);
//       if (match) {
//         const filePath = match[1];
//         const downloadLink = document.createElement("a");
//         downloadLink.href = "http://localhost:8080/" + filePath; // Ajuste conforme o backend
//         downloadLink.textContent = "Baixar XML";
//         downloadLink.download = filePath.split("/").pop();
//         resultElement.appendChild(document.createElement("br"));
//         resultElement.appendChild(downloadLink);
//       }
//     })
//     .catch((error) => {
//       resultElement.textContent = "Erro: " + error.message;
//     });
// }
function enviarArquivo(formData, url) {
  fetch(url, {
    method: "POST",
    body: formData,
  })
    .then((response) => {
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        return response.json();
      } else {
        return response.text();
      }
    })
    .then((data) => {
      document.getElementById("xmlContent").textContent = JSON.stringify(
        data,
        null,
        2
      );
      // Aqui você deve chamar a função para exportar o XML
      exportarXML(data);
    })
    .catch((error) => {
      document.getElementById("xmlContent").textContent =
        "Erro: " + error.message;
    });
}


// Complementar Automato
document
  .getElementById("complementoForm")
  .addEventListener("submit", function (e) {
    e.preventDefault(); // Prevenir o comportamento padrão de submit do formulário
    const file = document.getElementById("fileComplemento").files[0];
    const formData = new FormData();
    formData.append("file", file);
    enviarArquivo(
      formData,
      "http://localhost:8080/api/automato/complemento",
      document.getElementById("complementoResult")
    );
  });

// Estrela de Automato
document.getElementById("estrelaForm").addEventListener("submit", function (e) {
  e.preventDefault(); // Prevenir o comportamento padrão de submit do formulário
  const file = document.getElementById("fileEstrela").files[0];
  const formData = new FormData();
  formData.append("file", file);
  enviarArquivo(
    formData,
    "http://localhost:8080/api/automato/estrela",
    document.getElementById("estrelaResult")
  );
});

// Concatenar Automatos
document
  .getElementById("concatenacaoForm")
  .addEventListener("submit", function (e) {
    e.preventDefault(); // Prevenir o comportamento padrão de submit do formulário
    const file1 = document.getElementById("file1").files[0];
    const file2 = document.getElementById("file2").files[0];
    const formData = new FormData();
    formData.append("file1", file1);
    formData.append("file2", file2);
    enviarArquivo(
      formData,
      "http://localhost:8080/api/automato/concatenacao",
      document.getElementById("concatResult")
    );
  });

// União de Automatos
document.getElementById("uniaoForm").addEventListener("submit", function (e) {
  e.preventDefault(); // Prevenir o comportamento padrão de submit do formulário
  const file1 = document.getElementById("fileUniao1").files[0];
  const file2 = document.getElementById("fileUniao2").files[0];
  const formData = new FormData();
  formData.append("file1", file1);
  formData.append("file2", file2);
  enviarArquivo(
    formData,
    "http://localhost:8080/api/automato/uniao",
    document.getElementById("uniaoResult")
  );
});
