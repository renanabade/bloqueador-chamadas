# Bloqueador de chamadas

App Android simples: recusa chamadas de números que não estão nos contatos. Um botão liga e desliga o bloqueio.

- Usa `CallScreeningService` (precisa ser o app de identificação de chamadas) e lê os contatos do sistema.
- Número oculto também é recusado. Emergência nunca é bloqueada.
- APK assinado com a chave de debug, para instalar direto (ver Releases).

Compilar: `JAVA_HOME=<JDK 21> ./gradlew assembleDebug`
