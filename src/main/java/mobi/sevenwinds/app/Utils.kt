package mobi.sevenwinds.app

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ComparisonOp
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.QueryParameter
import org.jetbrains.exposed.sql.Op

class InsensitiveLikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "ILIKE")

infix fun<T:String?> ExpressionWithColumnType<T>.ilike(pattern: String): Op<Boolean> = InsensitiveLikeOp(this, QueryParameter(pattern, columnType))