package com.example.apparvoredavida.data

import com.example.apparvoredavida.data.bible.dao.BibleDao
import com.example.apparvoredavida.model.VersiculoDetails
import javax.inject.Inject
import javax.inject.Singleton
import com.example.apparvoredavida.data.bible.entity.VerseEntity
import com.example.apparvoredavida.data.bible.entity.BookEntity
import android.util.Log

@Singleton
class BibleRepositoryImpl @Inject constructor(
    private val bibleDao: BibleDao
) : BibleRepository {
    override suspend fun getVerseById(verseId: String): VersiculoDetails? {
        // A lógica de busca de versículo pelo ID usando bibleDao já está implementada.
        // O verseId tem o formato "TRADUCAO_ABREV_LIVRO_CAPITULO_VERSICULO", ex: "ACF_GN_1_1"
        return try {
            val parts = verseId.split("_")
            if (parts.size == 5) {
                // val traducaoAbbrev = parts[0] // Não precisamos da abreviação da tradução aqui para buscar o versículo no DB atual
                val livroAbbrev = parts[1]
                val capituloNum = parts[2].toInt()
                val versiculoNum = parts[3].toInt()

                val bookReferenceId = getBookReferenceId(livroAbbrev) // Função auxiliar para mapear abbrev para refId

                if (bookReferenceId != null) {
                    // Precisamos do bookId a partir do bookReferenceId para usar getSpecificVerse
                    val bookEntity = bibleDao.getBookByReferenceId(bookReferenceId)

                    bookEntity?.let { book ->
                        val verseEntity: VerseEntity? = bibleDao.getSpecificVerse(book.bookId, capituloNum, versiculoNum)
                        verseEntity?.let { entity ->
                            // Usando o nome do livro do BookEntity para VersiculoDetails
                            VersiculoDetails(
                                livroNome = book.name, // Usando o nome do livro do BookEntity
                                capituloNumero = entity.chapter,
                                versiculoNumero = entity.verseNumber,
                                texto = entity.text,
                                id = verseId // Manter o ID original
                            )
                        }
                    }
                } else {
                    Log.w("BibleRepository", "Abreviação de livro desconhecida: $livroAbbrev para verseId: $verseId")
                    null
                }
            } else {
                Log.w("BibleRepository", "Formato de verseId inválido: $verseId")
                null
            }
        } catch (e: Exception) {
            Log.e("BibleRepository", "Erro ao buscar versículo por ID $verseId: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // Função auxiliar para mapear abreviação do livro para book_reference_id
    // Mantida aqui por enquanto, idealmente viria de um local centralizado de metadados
    private fun getBookReferenceId(bookAbbrev: String): Int? {
         val bookAbbrevToRefId = mapOf(
            "GN" to 1, "ÊX" to 2, "LV" to 3, "NM" to 4, "DT" to 5, "JS" to 6, "JZ" to 7, "RT" to 8,
            "1SM" to 9, "2SM" to 10, "1RS" to 11, "2RS" to 12, "1CR" to 13, "2CR" to 14, "ED" to 15,
            "NE" to 16, "ET" to 17, "JÓ" to 18, "SL" to 19, "PV" to 20, "EC" to 21, "CT" to 22,
            "IS" to 23, "JR" to 24, "LM" to 25, "EZ" to 26, "DN" to 27, "OS" to 28, "JL" to 29,
            "AM" to 30, "OB" to 31, "JN" to 32, "MQ" to 33, "NA" to 34, "HC" to 35, "SF" to 36,
            "AG" to 37, "ZC" to 38, "ML" to 39, "MT" to 40, "MC" to 41, "LC" to 42, "JO" to 43,
            "AT" to 44, "RM" to 45, "1CO" to 46, "2CO" to 47, "GL" to 48, "EF" to 49, "FP" to 50,
            "CL" to 51, "1TS" to 52, "2TS" to 53, "1TM" to 54, "2TM" to 55, "TT" to 56, "FM" to 57,
            "HB" to 58, "TG" to 59, "1PE" to 60, "2PE" to 61, "1JO" to 62, "2JO" to 63, "3JO" to 64,
            "JD" to 65, "AP" to 66
         )
        return bookAbbrevToRefId[bookAbbrev]
    }
} 