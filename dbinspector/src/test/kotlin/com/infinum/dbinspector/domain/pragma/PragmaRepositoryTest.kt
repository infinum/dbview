package com.infinum.dbinspector.domain.pragma

import com.infinum.dbinspector.domain.Control
import com.infinum.dbinspector.domain.Interactors
import com.infinum.dbinspector.domain.Repositories
import com.infinum.dbinspector.shared.BaseTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.get
import org.mockito.kotlin.any

@DisplayName("PragmaRepository tests")
internal class PragmaRepositoryTest : BaseTest() {

    override fun modules(): List<Module> = listOf(
        module {
            single { mockk<Interactors.GetUserVersion>() }
            single { mockk<Interactors.GetTableInfo>() }
            single { mockk<Interactors.GetForeignKeys>() }
            single { mockk<Interactors.GetIndexes>() }
            single { mockk<Control.Pragma>() }
            factory<Repositories.Pragma> { PragmaRepository(get(), get(), get(), get(), get()) }
        }
    )

    @Test
    fun `Get user version calls GetUserVersion interactor and Pragma control`() {
        val interactor: Interactors.GetUserVersion = get()
        val control: Control.Pragma = get()
        val repository: Repositories.Pragma = get()

        coEvery { interactor.invoke(any()) } returns mockk()
        coEvery { control.converter version any() } returns mockk()
        coEvery { control.mapper.invoke(any()) } returns mockk()

        launch {
            repository.getUserVersion(any())
        }

        coVerify(exactly = 1) { interactor.invoke(any()) }
        coVerify(exactly = 1) { control.converter version any() }
        coVerify(exactly = 1) { control.mapper.invoke(any()) }
    }

    @Test
    fun `Get table info calls GetTableInfo interactor and Pragma control`() {
        val interactor: Interactors.GetTableInfo = get()
        val control: Control.Pragma = get()
        val repository: Repositories.Pragma = get()

        coEvery { interactor.invoke(any()) } returns mockk()
        coEvery { control.converter pragma any() } returns mockk()
        coEvery { control.mapper.invoke(any()) } returns mockk()

        launch {
            repository.getTableInfo(any())
        }

        coVerify(exactly = 1) { interactor.invoke(any()) }
        coVerify(exactly = 1) { control.converter pragma any() }
        coVerify(exactly = 1) { control.mapper.invoke(any()) }
    }

    @Test
    @Disabled("This repository call is untestable and needs to be refactored first.")
    fun `Get trigger info uses TriggerInfoColumns enum and Pragma control mapper transformToHeader`() {
        val control: Control.Pragma = get()
        val repository: Repositories.Pragma = get()

        coEvery { control.converter pragma any() } returns mockk()
        every { control.mapper.transformToHeader() } returns mockk()
        coEvery { repository.getTriggerInfo() } returns mockk {
            every { cells } returns listOf()
        }

        launch {
            repository.getTriggerInfo()
        }

        coVerify(exactly = 0) { control.converter pragma any() }
        // verify(exactly = 0) { control.mapper.transformToHeader() }
    }

    @Test
    fun `Get foreign keys calls GetForeignKeys interactor and Pragma control`() {
        val interactor: Interactors.GetForeignKeys = get()
        val control: Control.Pragma = get()
        val repository: Repositories.Pragma = get()

        coEvery { interactor.invoke(any()) } returns mockk()
        coEvery { control.converter pragma any() } returns mockk()
        coEvery { control.mapper.invoke(any()) } returns mockk()

        launch {
            repository.getForeignKeys(any())
        }

        coVerify(exactly = 1) { interactor.invoke(any()) }
        coVerify(exactly = 1) { control.converter pragma any() }
        coVerify(exactly = 1) { control.mapper.invoke(any()) }
    }

    @Test
    fun `Get indexes calls GetIndexes interactor and Pragma control`() {
        val interactor: Interactors.GetIndexes = get()
        val control: Control.Pragma = get()
        val repository: Repositories.Pragma = get()

        coEvery { interactor.invoke(any()) } returns mockk()
        coEvery { control.converter pragma any() } returns mockk()
        coEvery { control.mapper.invoke(any()) } returns mockk()

        launch {
            repository.getIndexes(any())
        }

        coVerify(exactly = 1) { interactor.invoke(any()) }
        coVerify(exactly = 1) { control.converter pragma any() }
        coVerify(exactly = 1) { control.mapper.invoke(any()) }
    }
}
