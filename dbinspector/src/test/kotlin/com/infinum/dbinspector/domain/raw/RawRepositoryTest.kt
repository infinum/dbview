package com.infinum.dbinspector.domain.raw

import com.infinum.dbinspector.domain.Control
import com.infinum.dbinspector.domain.Interactors
import com.infinum.dbinspector.domain.Repositories
import com.infinum.dbinspector.domain.shared.models.parameters.ContentParameters
import com.infinum.dbinspector.shared.BaseTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.get

@DisplayName("RawRepository tests")
internal class RawRepositoryTest : BaseTest() {

    override fun modules(): List<Module> = listOf(
        module {
            single { mockk<Interactors.GetRawQuery>() }
            single { mockk<Interactors.GetRawQueryHeaders>() }
            single { mockk<Interactors.GetAffectedRows>() }
            single { mockk<Control.Content>() }
            factory<Repositories.RawQuery> { RawRepository(get(), get(), get(), get()) }
        }
    )

    @Test
    fun `Get page calls GetRawQuery interactor and Content control once`() {
        val given: ContentParameters = mockk()
        val interactor: Interactors.GetRawQuery = get()
        val control: Control.Content = get()
        val repository: Repositories.RawQuery = get()

        coEvery { interactor.invoke(any()) } returns mockk()
        coEvery { control.converter.invoke(any()) } returns mockk()
        coEvery { control.mapper.invoke(any()) } returns mockk()
        coEvery { repository.getPage(given) } returns mockk()

        launch {
            repository.getPage(given)
        }

        coVerify(exactly = 1) { interactor.invoke(any()) }
        coVerify(exactly = 1) { control.converter.invoke(any()) }
        coVerify(exactly = 1) { control.mapper.invoke(any()) }
    }

    @Test
    fun `Get headers calls GetRawQueryHeaders interactor and Content control once`() {
        val given: ContentParameters = mockk()
        val interactor: Interactors.GetRawQueryHeaders = get()
        val control: Control.Content = get()
        val repository: Repositories.RawQuery = get()

        coEvery { interactor.invoke(any()) } returns mockk()
        coEvery { control.converter.invoke(any()) } returns mockk()
        coEvery { control.mapper.invoke(any()) } returns mockk()
        coEvery { repository.getHeaders(given) } returns mockk()

        launch {
            repository.getHeaders(given)
        }

        coVerify(exactly = 1) { interactor.invoke(any()) }
        coVerify(exactly = 1) { control.converter.invoke(any()) }
        coVerify(exactly = 1) { control.mapper.invoke(any()) }
    }

    @Test
    fun `Get affected rows calls GetAffectedRows interactor and Content control once`() {
        val given: ContentParameters = mockk()
        val interactor: Interactors.GetAffectedRows = get()
        val control: Control.Content = get()
        val repository: Repositories.RawQuery = get()

        coEvery { interactor.invoke(any()) } returns mockk()
        coEvery { control.converter.invoke(any()) } returns mockk()
        coEvery { control.mapper.invoke(any()) } returns mockk()
        coEvery { repository.getAffectedRows(given) } returns mockk()

        launch {
            repository.getAffectedRows(given)
        }

        coVerify(exactly = 1) { interactor.invoke(any()) }
        coVerify(exactly = 1) { control.converter.invoke(any()) }
        coVerify(exactly = 1) { control.mapper.invoke(any()) }
    }
}
