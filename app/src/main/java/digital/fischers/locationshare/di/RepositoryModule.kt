package digital.fischers.locationshare.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import digital.fischers.locationshare.data.database.daos.FriendDao
import digital.fischers.locationshare.data.database.daos.LocationDao
import digital.fischers.locationshare.data.remote.LocationApi
import digital.fischers.locationshare.data.repositories.FriendRepositoryImpl
import digital.fischers.locationshare.data.repositories.LocationRepositoryImpl
import digital.fischers.locationshare.data.repositories.UserRepositoryImpl
import digital.fischers.locationshare.domain.repositories.FriendRepository
import digital.fischers.locationshare.domain.repositories.LocationRepository
import digital.fischers.locationshare.domain.repositories.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext context: Context,
        locationApi: LocationApi,
        locationDao: LocationDao
    ): LocationRepository {
        return LocationRepositoryImpl(locationApi, context, locationDao)
    }

    @Provides
    @Singleton
    fun provideFriendRepository(
        @ApplicationContext context: Context,
        friendDao: FriendDao,
        locationDao: LocationDao,
        locationApi: LocationApi
    ): FriendRepository {
        return FriendRepositoryImpl(friendDao, locationDao, locationApi, context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        @ApplicationContext context: Context,
        locationApi: LocationApi
    ): UserRepository {
        return UserRepositoryImpl(locationApi, context)
    }
}