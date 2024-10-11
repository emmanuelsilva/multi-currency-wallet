package dev.emmanuel.wallet.events.infrastructure.repository

import dev.emmanuel.wallet.TestcontainersConfiguration
import dev.emmanuel.wallet.common.infrastructure.repository.JdbcCustomConversionsConfiguration
import dev.emmanuel.wallet.common.infrastructure.repository.MapReaderConverter
import dev.emmanuel.wallet.common.infrastructure.repository.MapWriterConverter
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Testcontainers

@Import(value = [
    TestcontainersConfiguration::class,
    MapReaderConverter::class,
    MapWriterConverter::class,
    JdbcCustomConversionsConfiguration::class
])
@Testcontainers
@DataJdbcTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WithRepositoryTest