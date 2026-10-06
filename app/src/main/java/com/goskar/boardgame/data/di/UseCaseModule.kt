package com.goskar.boardgame.data.di

import com.goskar.boardgame.data.useCase.BackupGuestUseCase
import com.goskar.boardgame.data.useCase.ClearDbUseCase
import com.goskar.boardgame.data.useCase.GetAllGameUseCase
import com.goskar.boardgame.data.useCase.GetAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.GetAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.GetAllPlayerUseCase
import com.goskar.boardgame.data.useCase.GetHistoryWithExpansionUseCase
import com.goskar.boardgame.data.useCase.GetLocalSnapshotUseCase
import com.goskar.boardgame.data.useCase.GetSyncStatusUseCase
import com.goskar.boardgame.data.useCase.GuestRestoreUseCase
import com.goskar.boardgame.data.useCase.MarkLocalDataSyncedUseCase
import com.goskar.boardgame.data.useCase.UpsertAllGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameExpansionUseCase
import com.goskar.boardgame.data.useCase.UpsertAllHistoryGameUseCase
import com.goskar.boardgame.data.useCase.UpsertAllPlayerUseCase
import com.goskar.boardgame.data.useCase.UploadToCloudUseCase
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.useCaseModule() = module {
    single { GetAllGameUseCase(get()) }
    single { GetAllPlayerUseCase(get()) }
    single { GetAllHistoryGameUseCase(get()) }
    single { UpsertAllGameUseCase(get()) }
    single { UpsertAllPlayerUseCase(get()) }
    single { UpsertAllHistoryGameUseCase(get()) }
    single { ClearDbUseCase(get(), get(), get()) }
    single { GetAllHistoryGameExpansionUseCase(get()) }
    single { UpsertAllHistoryGameExpansionUseCase(get()) }
    single { GetHistoryWithExpansionUseCase(get()) }
    single { GetLocalSnapshotUseCase(get(), get(), get(), get()) }
    single { UploadToCloudUseCase(get(), get(), get()) }
    single { GetSyncStatusUseCase(get(), get()) }
    single { MarkLocalDataSyncedUseCase(get(), get()) }
    single { BackupGuestUseCase(get(), get(), get()) }
    single { GuestRestoreUseCase(get(), get(), get(), get(), get(), get(), get()) }
}