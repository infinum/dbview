package im.dino.dbinspector.ui.schema.views

import androidx.paging.PagingData
import im.dino.dbinspector.domain.UseCases
import im.dino.dbinspector.ui.schema.shared.SchemaSourceViewModel
import im.dino.dbinspector.ui.shared.bus.EventBus
import im.dino.dbinspector.ui.shared.bus.models.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest

internal class ViewsViewModel(
    private val getSchema: UseCases.GetViews
) : SchemaSourceViewModel() {

    override fun dataSource(databasePath: String) = ViewsDataSource(databasePath, getSchema)

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun observe(action: suspend () -> Unit) {
        launch {
            io {
                EventBus.receive<Event.RefreshViews>().collectLatest { action() }
            }
        }
    }

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
