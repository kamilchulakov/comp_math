import java.util.*
import kotlin.math.pow

object Utils {
    private val random = Random()
    fun getRandomIntInRange(a: Int, b: Int): Int {
        return random.nextInt(a, b)
    }
}

fun Double.prettyRound(k: Int = LabConfiguration.roundParam): Double {
    return kotlin.math.round(this * 10.0.pow(k)) / 10.0.pow(k)
}