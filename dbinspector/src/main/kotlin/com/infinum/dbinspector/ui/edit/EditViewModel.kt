package com.infinum.dbinspector.ui.edit

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.PagingData
import com.infinum.dbinspector.domain.UseCases
import com.infinum.dbinspector.domain.shared.models.Cell
import com.infinum.dbinspector.domain.shared.models.Sort
import com.infinum.dbinspector.domain.shared.models.Statements
import com.infinum.dbinspector.domain.shared.models.parameters.ConnectionParameters
import com.infinum.dbinspector.domain.shared.models.parameters.ContentParameters
import com.infinum.dbinspector.domain.shared.models.parameters.PragmaParameters
import com.infinum.dbinspector.ui.shared.headers.Header
import com.infinum.dbinspector.ui.shared.paging.PagingViewModel
import com.infinum.dbinspector.ui.shared.views.editor.Keyword
import com.infinum.dbinspector.ui.shared.views.editor.KeywordType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

@Suppress("LongParameterList")
internal class EditViewModel(
    private val openConnection: UseCases.OpenConnection,
    private val closeConnection: UseCases.CloseConnection,
    private val getRawQueryHeaders: UseCases.GetRawQueryHeaders,
    private val getRawQuery: UseCases.GetRawQuery,
    private val getAffectedRows: UseCases.GetAffectedRows,
    private val getTables: UseCases.GetTables,
    private val getTableInfo: UseCases.GetTableInfo
) : PagingViewModel() {

    lateinit var databasePath: String

    private var onError: suspend (value: Throwable) -> Unit = { throwable ->
        Timber.e(throwable)
    }

    override val errorHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
        showError(throwable)
    }

    override fun dataSource(databasePath: String, statement: String) =
        EditDataSource(
            databasePath = databasePath,
            statement = statement,
            getRawQuery = getRawQuery
        )

    fun open(lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch(errorHandler) {
            openConnection(ConnectionParameters(databasePath))
        }
    }

    fun close(lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch(errorHandler) {
            closeConnection(ConnectionParameters(databasePath))
        }
    }

    fun header(
        query: String,
        onData: suspend (value: List<Header>) -> Unit,
        onError: suspend (value: Throwable) -> Unit
    ) {
        this.onError = onError
        launch {
            val result = io {
                getRawQueryHeaders(
                    ContentParameters(
                        databasePath = databasePath,
                        statement = query
                    )
                ).cells
                    .map {
                        Header(
                            name = it.text.orEmpty(),
                            sort = Sort.ASCENDING
                        )
                    }
            }
            onData(result)
        }
    }

    fun query(
        query: String,
        onData: suspend (value: PagingData<Cell>) -> Unit,
        onError: suspend (value: Throwable) -> Unit
    ) {
        this.onError = onError
        launch {
            pageFlow(databasePath, query) {
                onData(it)
            }
        }
    }

    fun affectedRows(
        onData: suspend (value: String) -> Unit,
        onError: suspend (value: Throwable) -> Unit
    ) {
        this.onError = onError
        launch {
            val result = io {
                getAffectedRows(
                    ContentParameters(
                        databasePath = databasePath,
                        statement = Statements.RawQuery.affectedRows()
                    )
                )
            }
            result.cells.firstOrNull()?.text?.let {
                onData(it)
            } ?: this@EditViewModel.onError(IllegalStateException("Unknown error."))
        }
    }

    fun keywords(
        onData: suspend (value: List<Keyword>) -> Unit
    ) {
        launch {
            val result = io {
                val schema = getTables(
                    ContentParameters(
                        databasePath = databasePath,
                        statement = Statements.RawQuery.schemaNames(),
                        pageSize = Int.MAX_VALUE
                    )
                )
                    .cells
                    .chunked(2)
                    .map { Pair(it[0].text.orEmpty(), it[1].text.orEmpty()) }
                    .filter { it.first.isNotBlank() && it.second.isNotBlank() }

                val tableNames: List<String> = schema.filter { it.second == "table" }.map { it.first }
                val otherNames: List<String> = schema.filterNot { it.second == "table" }.map { it.first }
                val columnNames: List<String> = tableNames.map {
                    getTableInfo(
                        PragmaParameters.Info(
                            databasePath = databasePath,
                            statement = Statements.Pragma.tableInfo(it)
                        )
                    ).cells.mapNotNull { cell -> cell.text }
                }.flatten()

                listOf<String>().plus(tableNames).plus(otherNames).plus(columnNames)
                    .map {
                        Keyword(
                            value = it,
                            type = KeywordType.NAME
                        )
                    }
            }
            onData(result)
        }
    }

    private fun showError(throwable: Throwable) =
        launch {
            onError(throwable)
        }
}
