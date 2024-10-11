package dev.emmanuel.wallet.common.infrastructure.repository

import dev.emmanuel.wallet.common.Json
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@WritingConverter
class MapWriterConverter : Converter<Map<*,*>, PGobject> {

    override fun convert(source: Map<*,*>): PGobject {
        val jsonObject = PGobject()
        jsonObject.type = "jsonb"
        jsonObject.value = Json.toJson(source)

        return jsonObject
    }
}