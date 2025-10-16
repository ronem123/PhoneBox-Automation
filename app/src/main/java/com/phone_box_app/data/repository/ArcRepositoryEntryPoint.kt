package com.phone_box_app.data.repository

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


/**
 * Created by Ram Mandal on 16/10/2025
 * @System: Apple M1 Pro
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ArcRepositoryEntryPoint {
    fun repository(): ArcRepository
}
