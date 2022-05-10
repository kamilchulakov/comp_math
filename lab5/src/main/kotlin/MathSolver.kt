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

    fun tMult(t: Double, i: Int): Double {
        var temp = t
        for (j in 1 until i) {
            temp *= if (j % 2 == 1) {
                (t - j)
            } else
                (t + j)
        }
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

        var sum = otherY[(st.n/2)][0]
        val t = (st.interpolationParam - st.x[(st.n/2)]) / (st.x[1] - st.x[0])

        for (i in 1 until st.n) {
            sum += (tMult(t, i) * otherY[((st.n - i) / 2)][i]) / i
        }
        st.interpolationValues.add(InterpolationValue("Многочлен Гаусса", sum.prettyRound()))
    }
}

data class ResultFunc(val type: String, val funcString: String, val func: (Double) -> Double)
data class InterpolationValue(val method: String, val result: Double)