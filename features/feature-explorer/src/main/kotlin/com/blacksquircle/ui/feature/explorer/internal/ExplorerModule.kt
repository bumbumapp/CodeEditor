/*
 * Copyright 2022 Squircle IDE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.feature.explorer.internal

import com.blacksquircle.ui.data.repository.explorer.ExplorerRepositoryImpl
import com.blacksquircle.ui.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.domain.providers.coroutine.DispatcherProvider
import com.blacksquircle.ui.domain.repository.explorer.ExplorerRepository
import com.blacksquircle.ui.filesystem.base.Filesystem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object ExplorerModule {

    @Provides
    @ViewModelScoped
    fun provideExplorerRepository(
        dispatcherProvider: DispatcherProvider,
        settingsManager: SettingsManager,
        @Named("Local") filesystem: Filesystem
    ): ExplorerRepository {
        return ExplorerRepositoryImpl(
            dispatcherProvider = dispatcherProvider,
            settingsManager = settingsManager,
            filesystem = filesystem
        )
    }
}