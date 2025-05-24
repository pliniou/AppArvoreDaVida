package com.example.apparvoredavida.util

import java.io.IOException

/**
 * Exceção lançada quando um asset não é encontrado.
 * @param message Mensagem descritiva do erro
 */
class AssetNotFoundException(message: String) : IOException(message) 