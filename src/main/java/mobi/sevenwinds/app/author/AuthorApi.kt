package mobi.sevenwinds.app.author

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route

fun NormalOpenAPIRoute.author() {
    route("/author") {
        route("/create").post<Unit, AuthorRecord, AuthorCreateRecord>(info("Добавить автора")) { _, body ->
            respond(AuthorService.addRecord(body))
        }
    }
}


data class AuthorCreateRecord(
    val fullName: String
)

data class AuthorRecord(
    val id: Int,
    val fullName: String,
    val cdat: String
)