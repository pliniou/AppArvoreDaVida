package com.example.apparvoredavida.util

import java.io.IOException

/**
 * Exceção lançada quando um asset não é encontrado.
 * @param message Mensagem descritiva do erro
 */
class AssetNotFoundException(message: String) : IOException(message)

/**
 * Exceção lançada quando há um erro ao carregar um asset.
 */
class AssetLoadException(message: String, cause: Throwable? = null) : IOException(message, cause)

/**
 * Exceção lançada quando há um erro ao processar um arquivo.
 */
class FileProcessingException(message: String, cause: Throwable? = null) : IOException(message, cause) 