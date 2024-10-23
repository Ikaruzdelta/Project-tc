// Adicionar eventos aos botões e exibir os formulários
document.querySelectorAll(".button-group button").forEach((button) => {
  button.addEventListener("click", () => {
    const operation = button.getAttribute("data-operation");
    exibirFormulario(operation);
    document.getElementById("default-message").style.display = "none"; // Esconder a mensagem padrão
  });
});

// Função para exibir o formulário correspondente a cada operação
function exibirFormulario(operation) {
  const formContainer = document.getElementById("form-container");
  formContainer.innerHTML = "";

  let formHtml = "";

  switch (operation) {
    case "uniao":
    case "intersecao":
    case "diferenca":
    case "diferenca-simetrica":
      formHtml = `
                <h2>${
                  operation.charAt(0).toUpperCase() + operation.slice(1)
                } de Autômatos</h2>
                <input type="file" id="file1" accept=".jff" required>
                <input type="file" id="file2" accept=".jff" required>
                <button onclick="aplicarOperacao('${operation}')">Aplicar ${operation}</button>
                <div id="resultado"></div>
            `;
      break;
    case "complemento":
    case "estrela":
    case "reverso":
    case "homomorfismo":
    case "minimizacao":
    case "conversorAFN": 
      formHtml = `
                <h2>${
                  operation.charAt(0).toUpperCase() + operation.slice(1)
                } de Autômato</h2>
                <input type="file" id="file" accept=".jff" required>
                <button onclick="aplicarOperacao('${operation}')">Aplicar ${operation}</button>
                <div id="resultado"></div>
            `;
      break;
    case "concatenacao":
      formHtml = `
                <h2>Concatenar Autômatos</h2>
                <input type="file" id="file1" accept=".jff" required>
                <input type="file" id="file2" accept=".jff" required>
                <button onclick="aplicarOperacao('${operation}')">Concatenar</button>
                <div id="resultado"></div>
            `;
      break;
  }

  formContainer.innerHTML = formHtml;
}

// Função para processar o upload de arquivos e chamar o backend para executar as operações
function aplicarOperacao(operation) {
  const formData = new FormData();

  if (
    operation === "uniao" ||
    operation === "intersecao" ||
    operation === "diferenca" ||
    operation === "diferenca-simetrica" ||
    operation === "concatenacao"
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

// Função para enviar arquivos para o servidor e exibir o resultado
function enviarArquivo(formData, url) {
  fetch(url, {
    method: "POST",
    body: formData,
  })
    .then((response) => {
      const contentType = response.headers.get("content-type");
      console.log(response);
      if (contentType && contentType.includes("application/json")) {
        return response.json();
      } else {
        return response.text();
      }
    })
    .then((data) => {
      document.getElementById("resultado").innerHTML = `<pre>${JSON.stringify(data,null,2)}</pre>`;
      exportarXML(data);
    })
    .catch((error) => {
      document.getElementById("resultado").innerHTML =
        "Erro ao processar o arquivo: " + error.message;
    });
}

// Função para exportar o XML (ajuste conforme necessário)
function exportarXML(data) {
  // Implemente a lógica para exportar o XML, caso necessário.
}

// Configurar eventos para operações específicas

// Complemento de Automato
const complementoForm = document.getElementById("complementoForm");
if (complementoForm) {
  complementoForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const file = document.getElementById("fileComplemento").files[0];
    const formData = new FormData();
    formData.append("file", file);
    enviarArquivo(
      formData,
      "http://localhost:8080/api/automato/complemento",
      document.getElementById("complementoResult")
    );
  });
}

// Estrela de Automato
const estrelaForm = document.getElementById("estrelaForm");
if (estrelaForm) {
  estrelaForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const file = document.getElementById("fileEstrela").files[0];
    const formData = new FormData();
    formData.append("file", file);
    enviarArquivo(
      formData,
      "http://localhost:8080/api/automato/estrela",
      document.getElementById("estrelaResult")
    );
  });
}

// Conversor AFN-AFD
const conversorAFNForm = document.getElementById("conversorAFNForm");
if (conversorAFNForm) {
  conversorAFNForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const file = document.getElementById("fileConversorAFN").files[0];
    const formData = new FormData();
    formData.append("file", file);
    enviarArquivo(
      formData,
      "http://localhost:8080/api/automato/conversorAFN",
      document.getElementById("conversorAFNResult")
    );
  });
}

// União de Automatos
const uniaoForm = document.getElementById("uniaoForm");
if (uniaoForm) {
  uniaoForm.addEventListener("submit", function (e) {
    e.preventDefault();
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
}
