# App Árvore da Vida

Aplicativo Android para leitura da Bíblia, hinos, partituras e reprodução de músicas.

## Estrutura do Projeto

O projeto segue o padrão MVVM (Model-View-ViewModel) e está organizado nos seguintes pacotes principais em `app/src/main/java/com/example/apparvoredavida/`:

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