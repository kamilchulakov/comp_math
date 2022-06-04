import MathSolver.tMult1

object MathSolver {
    fun lagrangeInterpolation(st: ProgramState) {
        val resPoints = mutableListOf<Double>()
        val n = st.x.size
        var result = 1.0
        for (j in 0 until n) {
            val c0 = st.interpolationParam - st.x[j]
            result *= c0
        }
        for (i in 0 until n) {
            var curr = result
            for (j in 0 until n) {
                curr /= if (i == j) {
                    val c0 = st.interpolationParam - st.x[j]
                    c0
                } else {
                    val c1 = st.x[i] - st.x[j]
                    c1
                }
            }
            resPoints.add(curr)
        }
        for (i in 0 until n) {
            resPoints[i] *= st.y[i]
        }

        st.interpolationValues.add(InterpolationValue("Многочлен Лагранжа", resPoints.sum().prettyRound()))
    }

    fun lagrangeInterpolationGraph(st: ProgramState, m: Double): Double {
        st.interpolationParam = m
        val resPoints = mutableListOf<Double>()
        val n = st.x.size
        var result = 1.0
        for (j in 0 until n) {
            val c0 = st.interpolationParam - st.x[j]
            result *= c0
        }
        for (i in 0 until n) {
            var curr = result
            for (j in 0 until n) {
                curr /= if (i == j) {
                    val c0 = st.interpolationParam - st.x[j]
                    c0
                } else {
                    val c1 = st.x[i] - st.x[j]
                    c1
                }
            }
            resPoints.add(curr)
        }
        for (i in 0 until n) {
            resPoints[i] *= st.y[i]
        }

        return resPoints.sum().prettyRound()
    }

    fun tMult1(curr: Double, t: Double, i: Int): Double {
        var temp = curr
        val j = (i-1)
        temp *= if (j % 2 == 1) {
            (t - j)
        } else (t + j)
        return temp
    }

    fun tMult2(curr: Double, t: Double, i: Int): Double {
        var temp = curr
        val j = (i-1)
        temp *= if (j % 2 == 0) {
            (t - j)
        } else (t + j)
        return temp
    }

    fun gaussInterpolation(st: ProgramState) {
        val otherY = mutableListOf<MutableList<Double>>()
//        И давно все узлы развязаны
//        А на дожде — все дороги радугой!
//        Быть беде. Нынче нам до смеха ли?
//        Но если есть колокольчик под дугой
//                Так, значит, все. Заряжай — поехали!
//        Загремим, засвистим, защелкаем!
//        Проберет до костей, до кончиков
//        Эй! братва! Чуете печенками грозный смех
//        Русских колокольчиков?
//        Век жуем. Матюги с молитвами
//                Век живем — хоть шары нам выколи
//        Спим да пьём. Сутками и литрами
//        Не поем. Петь уже отвыкли
//                Долго ждём. Все ходили грязные
//                Оттого сделались похожие
//        А под дождём оказались разные
//        Большинство — честные, хорошие
//        И, пусть разбит батюшка Царь-колокол
//        Мы пришли. Мы пришли с гитарами
//        Ведь биг-бит, блюз и рок-н-ролл
//        Околдовали нас первыми ударами
//                И в груди — искры электричества
//        Шапки в снег — и рваните звонче
//        Рок-н-ролл! Славное язычество
//        Я люблю время колокольчиков
        for (i in 0 until st.n) {
            otherY.add(mutableListOf())
            for (j in 0 until st.n - i) {
                otherY[i].add(0.0)
            }
        }
        for (i in 0 until st.n) {
            otherY[i][0]=st.y[i]
        }

        for (i in 1 until st.n) {
            for (j in 0 until st.n - i) {
                otherY[j][i]=
                    (otherY[j + 1][i - 1] - otherY[j][i - 1]).prettyRound()
            }
        }

        println("\nТаблица для многочлена Гаусса:")
        for (i in 0 until st.n) {
            println(otherY[i].joinToString(separator = " "))
        }

        var sum1 = otherY[(st.n/2)][0]
        var sum2 = otherY[(st.n/2)][0]
        val t = (st.interpolationParam - st.x[(st.n/2)]) / (st.x[1] - st.x[0])
        var t1 = 1.0
        var t2 = 1.0
        var fact = 1

        for (i in 1 until st.n) {
            fact *= i
            t1 = tMult1(t1, t, i)
            sum1 += (t1 * otherY[((st.n - i) / 2)][i]) / fact
            t2 = tMult2(t2, t, i)
            sum2 += (t2 * otherY[((st.n - i) / 2 - i%2)][i]) / fact
        }
        st.interpolationValues.add(InterpolationValue("Многочлен Гаусса 1", sum1.prettyRound()))
        st.interpolationValues.add(InterpolationValue("Многочлен Гаусса 2", sum2.prettyRound(k=4)))
        println("\n")
    }
}

fun gaussInterpolationGraph(st: ProgramState, m: Double): Double {
    st.interpolationParam = m
    val otherY = mutableListOf<MutableList<Double>>()
    for (i in 0 until st.n) {
        otherY.add(mutableListOf())
        for (j in 0 until st.n - i) {
            otherY[i].add(0.0)
        }
    }
    for (i in 0 until st.n) {
        otherY[i][0]=st.y[i]
    }

    for (i in 1 until st.n) {
        for (j in 0 until st.n - i) {
            otherY[j][i]=
                (otherY[j + 1][i - 1] - otherY[j][i - 1]).prettyRound()
        }
    }

//    println("\nТаблица для многочлена Гаусса:")
//    for (i in 0 until st.n) {
//        println(otherY[i].joinToString(separator = " "))
//    }

    var sum1 = otherY[(st.n/2)][0]
    val t = (st.interpolationParam - st.x[(st.n/2)]) / (st.x[1] - st.x[0])
    var t1 = 1.0
    var fact = 1

    for (i in 1 until st.n) {
        fact *= i
        t1 = tMult1(t1, t, i)
        sum1 += (t1 * otherY[((st.n - i) / 2)][i]) / fact
    }
    return sum1
}

data class ResultFunc(val type: String, val funcString: String, val func: (Double) -> Double)
data class InterpolationValue(val method: String, val result: Double)