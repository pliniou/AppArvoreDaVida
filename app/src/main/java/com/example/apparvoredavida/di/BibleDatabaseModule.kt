package com.example.apparvoredavida.di

import android.content.Context
import androidx.room.Room
import com.example.apparvoredavida.data.bible.BibleDatabase // Usando a classe que criamos
import com.example.apparvoredavida.data.bible.dao.BibleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent // Ou ViewModelComponent se o escopo for menor
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Ou outro componente apropriado
object BibleDatabaseModule {

    // Este provider será mais complexo. Você não pode ter um @Singleton fixo
    // se o nome do arquivo do banco de dados muda dinamicamente com a tradução.
    // Uma abordagem é ter uma factory ou um provider que recebe o nome da tradução.
    // Por agora, vamos simplificar e assumir que o BibliaViewModel gerenciará qual DB está ativo.

    // O BibliaViewModel pode ser responsável por construir a instância do DB para a tradução selecionada.
    // E então prover o DAO a partir dessa instância.

    // Exemplo (simplificado, o ViewModel gerenciaria a instância do DB):
    // Esta abordagem de prover o DAO diretamente é mais fácil se o ViewModel
    // já tem uma instância do BibleTranslationDatabase para a tradução atual.

    // @Provides
    // fun provideBibleDao(bibleDatabase: BibleDatabase): BibleDao {
    //     return bibleDatabase.bibleDao()
    // }
    // O desafio é como injetar 'bibleDatabase' aqui se ele muda.
} 