# Olimpíada de Questões

Sistema para cadastrar participantes, provas, questões e aplicar provas com correção automática.  
Esta é a **versão refatorada** do projeto, reorganizada para seguir os princípios SOLID.

## Principais Mudanças Realizadas

### Antes (Código Original)
- Toda a lógica do sistema estava concentrada na classe `App.java`
- Uma única classe era responsável por: menu, cadastros, aplicação de prova, cálculo de nota, impressão do tabuleiro e persistência dos dados
- Uso excessivo de variáveis estáticas
- Código difícil de entender, manter e modificar

### Depois (Versão Refatorada)
- O código foi dividido em partes bem organizadas
- Cada classe agora tem uma responsabilidade clara
- Introduzidas interfaces e serviços para separar regras de negócio
- Removido o acoplamento forte entre as classes
- O programa continua funcionando **exatamente igual** para o usuário final

### Mudanças Específicas:

- **App.java**: Agora só cuida do menu e do fluxo principal. Não faz mais persistência nem regras de negócio.
- **Domain**: As entidades (Participante, Prova, Questão, Resposta, Tentativa) foram movidas para a pasta `domain`.
- **Repository**: Criada camada responsável apenas por salvar e listar dados (em memória).
- **Service**: Criadas classes para regras de negócio (correção de respostas e busca de questões por prova).
- **Uso de Interfaces**: `Repository`, `QuestaoService` e `ProvaService` para tornar o código mais flexível.
- **Organização em pacotes**: O projeto agora está separado em `domain`, `repository` e `service`.

## Estrutura do Projeto
br.com.ucsal.olimpiadas/
├── App.java
├── domain/          ← Entidades do sistema
├── repository/      ← Persistência dos dados
└── service/         ← Regras de negócio

Princípios SOLID Aplicados

- **S – Single Responsibility**: Cada classe tem apenas uma responsabilidade principal.
- **O – Open/Closed**: É possível adicionar novas funcionalidades sem alterar código existente.
- **L – Liskov Substitution**: As implementações podem ser trocadas facilmente.
- **I – Interface Segregation**: Interfaces pequenas e focadas.
- **D – Dependency Inversion**: O código depende de abstrações (interfaces) e não de classes concretas.

## Como Executar

1. Compile o projeto (Java 23).
2. Execute a classe `br.com.ucsal.olimpiadas.App`.
3. O menu aparecerá normalmente, com o mesmo comportamento da versão original.
