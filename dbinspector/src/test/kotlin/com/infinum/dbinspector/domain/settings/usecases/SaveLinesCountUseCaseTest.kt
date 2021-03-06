package com.infinum.dbinspector.domain.settings.usecases

import com.infinum.dbinspector.domain.Repositories
import com.infinum.dbinspector.domain.UseCases
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

@DisplayName("SaveLinesCountUseCase tests")
internal class SaveLinesCountUseCaseTest : BaseTest() {

    override fun modules(): List<Module> = listOf(
        module {
            single { mockk<Repositories.Settings>() }
            factory<UseCases.SaveLinesCount> { SaveLinesCountUseCase(get()) }
        }
    )

    @Test
    fun `Invoking use case saves lines count in settings`() {
        val useCase: UseCases.SaveLinesCount = get()
        val settingsRepository: Repositories.Settings = get()

        coEvery { useCase.invoke(any()) } returns mockk()
        coEvery { settingsRepository.saveLinesCount(any()) } returns mockk()

        launch {
            useCase.invoke(any())
        }

        coVerify(exactly = 1) { settingsRepository.saveLinesCount(any()) }
    }
}
