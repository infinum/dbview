package com.infinum.dbinspector.domain.schema.trigger.usecases

import com.infinum.dbinspector.domain.Repositories
import com.infinum.dbinspector.domain.UseCases
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

@DisplayName("GetTriggerUseCase tests")
internal class GetTriggerUseCaseTest : BaseTest() {

    override fun modules(): List<Module> = listOf(
        module {
            single { mockk<Repositories.Schema>() }
            single { mockk<Repositories.Connection>() }
            factory<UseCases.GetTrigger> { GetTriggerUseCase(get(), get()) }
        }
    )

    @Test
    fun `Invoking use case invokes connection open and gets trigger`() {
        val given = ContentParameters(
            databasePath = "test.db",
            statement = "SELECT * FROM tables"
        )

        val useCase: UseCases.GetTrigger = get()
        val connectionRepository: Repositories.Connection = get()
        val schemaRepository: Repositories.Schema = get()

        coEvery { useCase.invoke(given) } returns mockk()
        coEvery { connectionRepository.open(any()) } returns mockk()
        coEvery { schemaRepository.getByName(any()) } returns mockk()

        launch {
            useCase.invoke(given)
        }

        coVerify(exactly = 1) { connectionRepository.open(any()) }
        coVerify(exactly = 1) { schemaRepository.getByName(any()) }
    }
}
