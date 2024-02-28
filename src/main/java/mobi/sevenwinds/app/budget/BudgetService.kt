package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import mobi.sevenwinds.app.ilike
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetCreateRecordParams): BudgetCreateRecordParams = withContext(Dispatchers.IO) {
        transaction {
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.author =
                    if (body.authorId == null) null else AuthorTable.select { AuthorTable.id eq body.authorId}
                        .limit(1)
                        .singleOrNull()?.let { AuthorEntity.wrapRow(it) }

            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {

            val fullName = param.authorName
            val query = if (fullName != null)
                (BudgetTable innerJoin AuthorTable)
                    .select { BudgetTable.year eq param.year }
                    .andWhere { AuthorTable.fullName ilike "%$fullName%" }
                    .orderBy(BudgetTable.month)
                    .orderBy(BudgetTable.amount, SortOrder.DESC)
            else
                BudgetTable
                    .select { BudgetTable.year eq param.year }
                    .orderBy(BudgetTable.month)
                    .orderBy(BudgetTable.amount, SortOrder.DESC)

            val total = query.count()
            val totalData = BudgetEntity.wrapRows(query).map { it.toBudgetAuthorResponse() }
            println(totalData)

            query.limit(param.limit, param.offset)

            val paginatedData = BudgetEntity.wrapRows(query).map { it.toBudgetAuthorResponse() }
            val sumByType = totalData.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = paginatedData
            )
        }
    }
}