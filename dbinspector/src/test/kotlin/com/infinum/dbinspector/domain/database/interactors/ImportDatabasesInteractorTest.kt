package com.infinum.dbinspector.domain.database.interactors

import com.infinum.dbinspector.data.Sources
import com.infinum.dbinspector.domain.Interactors
import com.infinum.dbinspector.shared.BaseTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.get
import org.mockito.kotlin.any

@DisplayName("ImportDatabasesInteractor tests")
internal class ImportDatabasesInteractorTest : BaseTest() {

    override fun modules(): List<Module> = listOf(
        module {
            single { mockk<Sources.Raw>() }
            factory<Interactors.ImportDatabases> { ImportDatabasesInteractor(get()) }
        }
    )

    @Test
    fun `Invoking interactor invokes source importDatabases`() {
        val interactor: Interactors.ImportDatabases = get()
        val source: Sources.Raw = get()

        coEvery { source.importDatabases(any()) } returns mockk()

        launch {
            interactor.invoke(any())
        }

        coVerify(exactly = 1) { source.importDatabases(any()) }
    }
}
