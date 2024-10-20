
# Computer theory project

Este projeto foi desenvolvido como parte de uma atividade acadêmica da disciplina de Teoria da Computação no curso de Sistemas de Informação da Universidade Federal de Sergipe (UFS). O objetivo principal é implementar uma ferramenta para manipulação de Autômatos Finitos Determinísticos e Não Determinísticos (AFD/AFN), com base em diversas operações regulares, como união, interseção, concatenação, complemento, minimização de autômatos e conversão de AFN para AFD.

O projeto é dividido em duas partes principais:

1. **Backend**: Responsável pelo processamento das operações com os autômatos. Ele importa autômatos no formato `.jff` e executa as operações especificadas, retornando os resultados e permitindo exportar o autômato processado.
   
2. **Frontend**: Uma interface simples que permite ao usuário interagir com a aplicação, fazendo upload dos autômatos e visualizando os resultados das operações diretamente no navegador.

### Funcionalidades:

- Importação de arquivos `.jff` para representar autômatos.
- Operações regulares disponíveis: união, interseção, concatenação, complemento, estrela e minimização.
- Exportação dos resultados processados em arquivos `.jff`.

Este projeto foi construído utilizando Java no backend, com Spring Boot, e uma interface simples em HTML, CSS e JavaScript no frontend.

## Requisitos:
- **Backend**:
  - Java 8 ou superior
  - Maven instalado
- **Frontend**:
  - Navegador web atualizado (Google Chrome, Firefox, etc.)

## Como rodar o projeto

### 1. Clonar o repositório ou baixar o código
Faça o download ou clone o repositório para a sua máquina local.

### 2. Configuração do Backend

1. Acesse a pasta do backend:
   ```bash
   cd backend
   ```

2. Execução:
   ```bash
   Execute o App.java
   ```

   O servidor backend será iniciado na porta padrão (geralmente `8080`). Certifique-se de que nenhuma outra aplicação está usando essa porta.

### 3. Configuração do Frontend

1. Acesse a pasta do frontend:
   ```bash
   cd frontend
   ```

2. Abra o arquivo `index.html` em seu navegador. O frontend é um projeto estático e não necessita de um servidor para ser executado.

### 4. Testar a aplicação

Com o backend rodando e o frontend aberto no navegador, você pode interagir com a aplicação normalmente. O frontend se comunicará com o backend via as rotas da API.

---

## Estrutura do Projeto

```
project-root/
│
├── backend/        # Código do servidor Java Spring Boot
├── frontend/       # Arquivos HTML, CSS e JS do frontend
└── README.md       # Este arquivo
```

### Tecnologias Utilizadas

- **Backend**: Java, Spring Boot, Maven
- **Frontend**: HTML, CSS, JavaScript

---

## Licença

Este projeto é de código aberto e está sob a licença MIT.
