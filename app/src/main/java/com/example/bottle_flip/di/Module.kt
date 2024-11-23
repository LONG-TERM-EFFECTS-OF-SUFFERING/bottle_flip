package com.example.bottle_flip.di

import com.google.firebase.auth.FirebaseAuth
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
	fun provide_firebase_auth(): FirebaseAuth {
		return FirebaseAuth.getInstance()
	}

}