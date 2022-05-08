import kotlin.math.pow

object Utils {
    fun round(n: Double, k: Int = 3): Double {
        return kotlin.math.round(n * 10.0.pow(k)) / 10.0.pow(k)
    }
}