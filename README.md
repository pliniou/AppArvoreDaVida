# App Arvore da Vida

Este projeto é um aplicativo Android nativo desenvolvido em Kotlin, focado em recursos relacionados à Bíblia, hinos e partituras, com funcionalidades de reprodução de áudio.

## Estado Atual do Projeto

O projeto está em desenvolvimento ativo, com a estrutura arquitetural e as principais tecnologias configuradas para suportar as funcionalidades planejadas. As camadas de UI, navegação, gerenciamento de estado (ViewModels) e acesso a dados estão sendo implementadas.

## Tecnologias e Arquitetura

*   **Linguagem:** Kotlin 1.9.22
*   **Framework UI:** Jetpack Compose (com Compose BOM 2024.02.00)
*   **Arquitetura:** MVVM (Model-View-ViewModel)
*   **Injeção de Dependência:** Hilt
*   **Navegação:** Navigation Compose 2.7.7
*   **Persistência de Preferências:** Jetpack DataStore Preferences 1.0.0
*   **Serialização de Dados:** Kotlinx Serialization 1.6.2 e Gson 2.10.1 (Nota: Verificar e padronizar o uso entre ambas)
*   **Reprodução de Mídia:** ExoPlayer Media3 1.2.1
*   **Renderização de PDF:** `androidx.pdf:pdf-renderer` 2.0.0
*   **Carregamento de Imagens:** Coil 2.5.0
*   **Testes:** Kotlin Coroutines Test 1.7.3

## Funcionalidades Implementadas (Baseado na Estrutura e Dependências)

*   Estrutura base da UI com Jetpack Compose.
*   Configuração inicial da navegação entre telas usando Navigation Compose.
*   Implementação de ViewModels para gerenciar o estado da UI e lógica de negócio.
*   Estrutura da camada de dados (`data/bible/dao`, `data/bible/entity`) para acesso aos conteúdos (presumivelmente lendo de assets/JSON).
*   Configuração do Hilt para injeção de dependências nas camadas do aplicativo.
*   Suporte à persistência de preferências usando DataStore.
*   Previsão e estrutura para:
    *   Leitura e exibição do conteúdo da Bíblia (de assets/JSON).
    *   Exibição de letras de hinos (de assets).
    *   Reprodução de arquivos de áudio MP3 (de assets).
    *   Renderização e exibição de partituras em formato PDF (de assets).

## Estrutura do Projeto

O projeto segue a estrutura de pacotes padrão para aplicativos Android com MVVM e Injeção de Dependência:

-   `ui/`: Contém os componentes de interface do usuário, telas (screens), temas e navegação.
-   `model/`: Define as classes de modelo de dados.
-   `viewmodel/`: Gerencia a lógica de negócio e o estado da UI.
-   `data/`: Inclui repositórios e fontes de dados para acesso e manipulação de informações.
-   `util/`: Contém classes utilitárias e helper functions.

Os assets estão organizados em `app/src/main/assets/`:

-   `bible/`: Arquivos JSON das versões da Bíblia.
-   `fonts/`: Fontes customizadas (.ttf).
-   `hymns/`: Arquivos PDF dos hinos.
-   `mp3/`: Arquivos de áudio (.mp3) das músicas.
-   `partituras/`: Arquivos PDF das partituras.

## Funcionalidades Principais

-   **Leitor Bíblico Offline:** Acesso a 6 versões da Bíblia sem internet, navegação por livro, capítulo e versículo, busca, configurações de leitura personalizáveis (fonte, tamanho, cor), e marcação de versículos favoritos.
-   **Hinário:** Visualização de hinos em formato PDF, navegação rápida, instruções de uso na primeira vez e controles de página/zoom, com suporte a favoritos.
-   **Visualizador de Partituras:** Visualização de partituras em formato PDF, navegação rápida, instruções de uso na primeira vez e controles de página/zoom, com suporte a favoritos.
-   **Player de Música:** Reprodução de músicas em MP3 organizadas por álbuns (pastas), controles de reprodução (play/pause, próximo/anterior), lista de reprodução e uma barra de reprodução dinâmica.
-   **Sistema Unificado de Favoritos:** Gerenciamento centralizado de versículos, hinos, partituras e músicas favoritos, com interface por abas e funcionalidades de exportação/importação.
-   **Configurações:** Opções para personalizar a experiência do usuário, incluindo configurações de leitura da Bíblia e outras preferências salvas usando DataStore.

## Tecnologias e Dependências

O projeto utiliza **Kotlin** (1.9.22) e **Java** com foco em **Jetpack Compose** para a interface do usuário, seguindo as diretrizes do **Material Design 3**. A compilação é feita com o **Compose Compiler 1.5.8**. As principais dependências incluem:

-   **Compose BOM**: 2024.02.00
-   **Material3**
-   **Navigation Compose**: 2.7.7
-   **DataStore Preferences**: 1.0.0 (para persistência de preferências)
-   **Coil**: 2.5.0 (para carregamento de imagens)
-   **ExoPlayer (Media3)**: 1.2.1 (para reprodução de áudio)
-   **Android PDF Viewer**: 3.2.0-beta.1 (para visualização de PDFs)
-   **Kotlinx Serialization**: 1.6.2
-   **Coroutines**: 1.7.3

Outras tecnologias essenciais incluem ViewBinding para layouts XML existentes e KSP (1.9.22-1.0.17).

## Requisitos

-   **Linguagens:** Kotlin, Java
-   **SDKs:** `compileSdk = 34`, `targetSdk = 34`, `minSdk = 21`
-   **Ferramentas:** Android Studio Hedgehog | 2023.1.1 ou superior, JDK 17.
-   **Plugins Gradle:** Android Application (AGP 8.2.2), Kotlin Android, Parcelize, Serialization, KSP.

## Configuração do Ambiente

1.  Clone o repositório.
2.  Abra o projeto no Android Studio.
3.  Sincronize o projeto com os arquivos Gradle.
4.  Execute o aplicativo em um emulador ou dispositivo físico.

## Telas do Aplicativo

-   Tela Inicial (Menu Principal)
-   Visualizador de Partituras
-   Hinário
-   Leitor Bíblico
-   Player de Música (com barra de reprodução dinâmica)
-   Tela de Favoritos (com abas por tipo)
-   Tela de Configurações

## Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

# Visualizador de PDF

Este módulo implementa um visualizador de PDF robusto e otimizado para a aplicação, seguindo as melhores práticas de desenvolvimento Android moderno.

## Características

- Renderização eficiente de PDFs usando PdfRenderer
- Suporte a zoom e pan com gestos
- Navegação entre páginas
- Persistência de preferências do usuário (última página visualizada, nível de zoom)
- Cache de páginas para melhor desempenho
- Interface moderna com Material Design 3
- Arquitetura MVVM com Jetpack Compose

## Arquitetura

O módulo segue a arquitetura MVVM e está organizado nas seguintes camadas:

### UI (ui/)
- `PdfViewerScreen`: Tela principal do visualizador
- `PdfPageNavigator`: Componente reutilizável para navegação entre páginas

### ViewModel (viewmodel/)
- `PdfViewerViewModel`: Gerencia o estado da UI e a lógica de negócios

### Repository (data/repository/)
- `PdfRepository`: Gerencia o acesso aos PDFs e preferências do usuário

### DataStore (data/datastore/)
- `PdfPreferences`: Gerencia as preferências do usuário usando DataStore

## Dependências

- Jetpack Compose 2024.02.00
- Material Design 3
- Hilt 2.50
- DataStore Preferences 1.0.0
- Coroutines 1.7.3
- Coil 2.5.0
- PDFium Android 1.9.0

## Uso

Para abrir um PDF, navegue para a tela do visualizador passando o nome do arquivo:

```kotlin
navController.navigate(Screen.PdfViewer.createRoute("nome_do_arquivo.pdf"))
```

## Testes

O módulo inclui testes unitários para o ViewModel usando:
- JUnit 4
- MockK
- Coroutines Test
- Turbine

## Otimizações

- Cache de configuração do Gradle habilitado
- R8 full mode ativado
- D8 desugaring habilitado
- Build cache ativado
- Jetifier desativado

## Limitações

- Suporte apenas para PDFs não criptografados
- Tamanho máximo de página limitado pela memória do dispositivo
- Zoom limitado entre 0.5x e 3x

## Contribuição

1. Faça o fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request