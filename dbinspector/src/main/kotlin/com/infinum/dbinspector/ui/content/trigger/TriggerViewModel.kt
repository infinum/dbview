package com.infinum.dbinspector.ui.content.trigger

import com.infinum.dbinspector.domain.UseCases
import com.infinum.dbinspector.domain.shared.models.Sort
import com.infinum.dbinspector.domain.shared.models.Statements
import com.infinum.dbinspector.ui.content.shared.ContentViewModel

internal class TriggerViewModel(
    openConnection: UseCases.OpenConnection,
    closeConnection: UseCases.CloseConnection,
    triggerInfo: UseCases.GetTriggerInfo,
    trigger: UseCases.GetTrigger,
    dropTrigger: UseCases.DropTrigger
) : ContentViewModel(
    openConnection,
    closeConnection,
    triggerInfo,
    trigger,
    dropTrigger
) {

    override fun headerStatement(name: String) = ""

    override fun schemaStatement(name: String, orderBy: String?, sort: Sort) =
        Statements.Schema.trigger(name)

    override fun dropStatement(name: String) =
        Statements.Schema.dropTrigger(name)
}
