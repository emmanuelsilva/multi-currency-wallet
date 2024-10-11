package dev.emmanuel.wallet.common.infrastructure.repository

import dev.emmanuel.wallet.common.Json
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class MapReaderConverter : Converter<PGobject, Map<*,*>> {

    override fun convert(source: PGobject): Map<*,*> {
        return Json.fromJson(source.value!!, Map::class.java)
    }
}