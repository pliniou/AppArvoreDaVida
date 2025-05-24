package com.example.apparvoredavida.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    // Os ViewModels são injetados diretamente pelo Hilt usando @HiltViewModel
} 