package com.infinum.dbinspector.ui.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infinum.dbinspector.R
import com.infinum.dbinspector.databinding.DbinspectorActivityEditBinding
import com.infinum.dbinspector.domain.shared.models.Cell
import com.infinum.dbinspector.extensions.setupAsTable
import com.infinum.dbinspector.ui.Presentation
import com.infinum.dbinspector.ui.content.shared.ContentAdapter
import com.infinum.dbinspector.ui.content.shared.ContentPreviewFactory
import com.infinum.dbinspector.ui.edit.history.HistoryDialog
import com.infinum.dbinspector.ui.shared.base.BaseActivity
import com.infinum.dbinspector.ui.shared.base.lifecycle.LifecycleConnection
import com.infinum.dbinspector.ui.shared.delegates.lifecycleConnection
import com.infinum.dbinspector.ui.shared.delegates.viewBinding
import com.infinum.dbinspector.ui.shared.edgefactories.bounce.BounceEdgeEffectFactory
import com.infinum.dbinspector.ui.shared.headers.HeaderAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class EditActivity : BaseActivity(), HistoryDialog.Listener {

    override val binding by viewBinding(DbinspectorActivityEditBinding::inflate)

    override val viewModel: EditViewModel by viewModel()

    private val connection: LifecycleConnection by lifecycleConnection()

    private lateinit var contentPreviewFactory: ContentPreviewFactory

    private lateinit var headerAdapter: HeaderAdapter

    private lateinit var contentAdapter: ContentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contentPreviewFactory = ContentPreviewFactory(this)

        binding.toolbar.setNavigationOnClickListener { finish() }

        if (connection.hasDatabaseData) {
            viewModel.databasePath = connection.databasePath!!
            viewModel.open()
            viewModel.keywords { keywords ->
                binding.editorInput.addKeywords(keywords)
            }

            setupUi(connection.databaseName!!)
        } else {
            showDatabaseParametersError()
        }
    }

    override fun onHistorySelected(statement: String) {
        with(binding) {
            editorInput.setContent(statement)
            editorInput.setSelection(editorInput.text?.length ?: 0)
        }
    }

    private fun setupUi(databaseName: String) {
        with(binding.toolbar) {
            subtitle = databaseName
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.clear -> clearInput()
                    R.id.history -> showHistory()
                    R.id.execute -> query()
                }
                true
            }
        }

        with(binding) {
            suggestionButton.setOnClickListener {
                editorInput.setContent(suggestionButton.text.toString())
                editorInput.setSelection(editorInput.text?.length ?: 0)
                suggestionButton.isVisible = false
            }
            editorInput.doOnTextChanged { text, _, _, _ ->
                toolbar.menu.findItem(R.id.clear).isEnabled = text.isNullOrBlank().not()
                toolbar.menu.findItem(R.id.execute).isEnabled = text.isNullOrBlank().not()
            }
            editorInput.doOnTextChanged { text, _, _, _ ->
                if (text?.toString()?.trim().orEmpty().isNotBlank()) {
                    viewModel.findSimilarExecution(lifecycleScope, text?.toString()?.trim().orEmpty()) {
                        suggestionButton.isVisible = it.executions.isNotEmpty() &&
                            text?.toString()?.trim().orEmpty().isNotBlank() &&
                            (binding.editorInput.text?.toString().orEmpty().trim() != suggestionButton.text)
                        suggestionButton.text = it.executions.firstOrNull()?.statement
                    }
                } else {
                    suggestionButton.isVisible = text?.toString()?.trim().orEmpty().isNotBlank()
                }
            }
        }

        binding.recyclerView.setupAsTable()
        viewModel.history {
            binding.toolbar.menu.findItem(R.id.history).isEnabled = it.executions.isNotEmpty()
        }
    }

    private fun clearInput() =
        binding.editorInput.text.clear()

    private fun showHistory() =
        connection.databasePath?.let {
            HistoryDialog.show(
                connection.databaseName,
                it,
                supportFragmentManager
            )
        }

    private fun query() {
        val query = binding.editorInput.text?.toString().orEmpty().trim()

        viewModel.header(
            query = query,
            onData = { tableHeaders ->
                if (tableHeaders.isNotEmpty()) {
                    binding.errorView.isVisible = false
                    binding.recyclerView.layoutManager = GridLayoutManager(
                        this@EditActivity,
                        tableHeaders.size,
                        RecyclerView.VERTICAL,
                        false
                    )
                    binding.recyclerView.edgeEffectFactory = BounceEdgeEffectFactory()

                    headerAdapter = HeaderAdapter(tableHeaders, false) {}

                    contentAdapter = ContentAdapter(
                        headersCount = tableHeaders.size,
                        onCellClicked = { cell -> contentPreviewFactory.showCell(cell) }
                    )

                    with(binding) {
                        recyclerView.adapter = ConcatAdapter(
                            headerAdapter,
                            contentAdapter
                        )
                    }

                    viewModel.query(
                        query = query,
                        onData = { showData(it) },
                        onError = { showError(it.message) }
                    )
                } else {
                    viewModel.affectedRows(
                        onData = { showAffectedRows(it) },
                        onError = { showError(it.message) }
                    )
                }
            },
            onError = { showError(it.message) }
        )
    }

    private suspend fun showData(cells: PagingData<Cell>) {
        viewModel.saveSuccessfulExecution(binding.editorInput.text?.toString().orEmpty().trim())
        with(binding) {
            recyclerView.isVisible = true
            affectedRowsView.isVisible = false
            errorView.isVisible = false
        }.also {
            contentAdapter.submitData(cells)
        }
    }

    private suspend fun showAffectedRows(rowCount: String) {
        viewModel.saveSuccessfulExecution(binding.editorInput.text?.toString().orEmpty().trim())
        with(binding) {
            recyclerView.isVisible = false
            affectedRowsView.isVisible = true
            errorView.isVisible = false
            affectedRowsView.text =
                resources.getQuantityString(
                    R.plurals.dbinspector_affected_rows,
                    rowCount.toInt(),
                    rowCount
                )
        }
        setResult(
            Activity.RESULT_OK,
            Intent().apply {
                putExtra(Presentation.Constants.Keys.SHOULD_REFRESH, true)
            }
        )
    }

    private fun showError(message: String?) {
        with(binding) {
            recyclerView.isVisible = false
            affectedRowsView.isVisible = false
            errorView.isVisible = true
            errorView.text = message
        }
        viewModel.saveFailedExecution(binding.editorInput.text?.toString().orEmpty().trim())
    }
}
