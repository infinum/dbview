package im.dino.dbinspector.ui.schema.tables

import androidx.paging.PagingData
import im.dino.dbinspector.domain.UseCases
import im.dino.dbinspector.ui.schema.shared.SchemaSourceViewModel

internal class TablesViewModel(
    private val getSchema: UseCases.GetTables
) : SchemaSourceViewModel() {

    override fun dataSource(databasePath: String) = TablesDataSource(databasePath, getSchema)

    override fun observe(action: suspend () -> Unit) = Unit

    override fun query(
        databasePath: String,
        onData: suspend (value: PagingData<String>) -> Unit
    ) {
        launch {
            pageFlow(databasePath) {
                onData(it)
            }
        }
    }
}
