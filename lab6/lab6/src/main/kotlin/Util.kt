import java.util.*
import kotlin.math.pow

//object Util {
//    private val random = Random()
//    fun getRandomIntInRange(a: Int, b: Int): Int {
//        return random.nextInt(a, b)
//    }
//}
//
//private fun Random.nextInt(a: Int, b: Int): Int {
//    return a+this.nextInt(b-a)
//}

fun Double.prettyRound(k: Int = 3): Double {
    return kotlin.math.round(this * 10.0.pow(k)) / 10.0.pow(k)
}