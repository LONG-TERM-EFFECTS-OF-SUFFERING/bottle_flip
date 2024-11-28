package com.example.bottle_flip.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Module {

	@Singleton
	@Provides
	fun provideFirebaseAuth(): FirebaseAuth {
		return FirebaseAuth.getInstance()
	}

	@Singleton
	@Provides
	fun provideFirebaseDB(): FirebaseFirestore {
		return FirebaseFirestore.getInstance()
	}
}
