package digital.fischers.locationshare.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import digital.fischers.locationshare.data.database.daos.LocationDao
import digital.fischers.locationshare.data.remote.LocationApi
import digital.fischers.locationshare.data.repositories.LocationRepositoryImpl
import digital.fischers.locationshare.data.repositories.RemoteRepositoryImpl
import digital.fischers.locationshare.domain.repositories.LocationRepository
import digital.fischers.locationshare.domain.repositories.RemoteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideRemoteRepository(
        @ApplicationContext context: Context,
        locationDao: LocationDao,
        api: LocationApi
    ): RemoteRepository {
        return RemoteRepositoryImpl(
            context = context,
            locationDao = locationDao,
            api = api
        )
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext context: Context
    ): LocationRepository {
        return LocationRepositoryImpl(context)
    }
}