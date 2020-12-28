package com.infinum.dbinspector.domain.schema.table.usecases

import com.infinum.dbinspector.domain.Repositories
import com.infinum.dbinspector.domain.UseCases
import com.infinum.dbinspector.domain.shared.models.Page
import com.infinum.dbinspector.domain.shared.models.Query

internal class GetTableUseCase(
    private val connectionRepository: Repositories.Connection,
    private val settingsRepository: Repositories.Settings,
    private val schemaRepository: Repositories.Schema
) : UseCases.GetTable {

    override suspend fun invoke(input: Query): Page {
        val connection = connectionRepository.open(input.databasePath)
        val settings = settingsRepository.load()
        return schemaRepository.getByName(
            input.copy(
                database = connection,
                blobPreviewType = settings.blobPreviewType
            )
        )
    }
}
