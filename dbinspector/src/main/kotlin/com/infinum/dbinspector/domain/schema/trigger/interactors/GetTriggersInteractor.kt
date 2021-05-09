package com.infinum.dbinspector.domain.schema.trigger.interactors

import com.infinum.dbinspector.data.Sources
import com.infinum.dbinspector.data.models.local.cursor.input.Query
import com.infinum.dbinspector.data.models.local.cursor.output.QueryResult
import com.infinum.dbinspector.domain.Interactors

internal class GetTriggersInteractor(
    private val source: Sources.Local.Schema
) : Interactors.GetTriggers {

    override suspend fun invoke(input: Query): QueryResult =
        source.getTriggers(input)
}
