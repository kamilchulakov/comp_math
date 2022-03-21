import functions
import numpy as np
import matplotlib.pyplot as plt

import main
import util

some_small_num = 0.00000001


def calc_dx(function, x, y, h=some_small_num):
    return (function(x + h, y) - function(x - h, y)) / (2 * h)


def calc_dy(function, x, y, h=some_small_num):
    return (function(x, y + h) - function(x, y - h)) / (2 * h)


def calc(function1, function2, x, y):
    return calc_dx(function1, x, y) * calc_dy(function2, x, y) - calc_dx(function2, x, y) * calc_dy(function1, x, y)


def ask_continue():
    type = int(input("Введите 1, если хотите найти следующий интервал\nВведите 2, если не хотите:\n"))
    return type


def print_methods():
    print('1 - Метод половинного деления\n2 - Метод простой итерации\n')


def find_div(function, h=some_small_num):
    return lambda x: (function(x + h) - function(x - h)) / (2 * h)


def find_interval(function, step):
    prev = -100
    for i in np.arange(-100, 100, step):
        if (function(i) * function(prev) < 0):
            print("Найден интервал: [" + str(prev) + ", " + str(i) + "]")
            if ask_continue() == 2:
                return prev, i
        prev = i
    print("Интервал не найден")
    return None, None


def half_div_method(function, a, b, accuracy):
    x_current = 0
    a_current = a
    b_current = b
    iterations = 0
    x_current = (a + b) / 2
    while (abs(a_current - b_current) > accuracy) & (function(x_current) > accuracy):
        x_current = (a_current + b_current) / 2
        if function(a_current) * function(x_current) > 0:
            a_current = x_current
        else:
            b_current = x_current
        iterations = iterations + 1
    x_current = (a_current + b_current) / 2
    return x_current, iterations


def iterations2(function, a, b, accuracy):
    # 1 способ
    fi = lambda x: (1.0 / 9.21) * (x ** 3 - 4.5 * x ** 2 - 0.383)
        # case functions.get_function_lmbd(2):
        #     fi = lambda x: x ** 2 - 4 + np.e ** 2
        # case functions.get_function_lmbd(3):
        #     fi = lambda x: (-10) * (np.sin(x) - 5 * np.cos(x))
    fi_s = find_div(fi)
    if (abs(fi_s(a)) < 1) & (abs(fi_s(b)) < 1):
        print("OK")
        x_current = a
        x_prev = a * 1000 + 10

        iterations = 0

        while (abs(x_prev - x_current) > accuracy) or (abs(function(x_current)) > accuracy):

            x_prev = x_current
            x_current = fi(x_prev)
            # print(x_current, x_prev, function(x_current))
            iterations += 1
            if iterations > 1000:
                print("Итерационный процесс расходится")
                exit(-1)

        return x_current, iterations
    print("SHIT")
    return None, None


def iterations_method(function, a, b, accuracy):
    # 3 способ
    # print("Функция в A: ", function(a)) правильное
    dev_a = find_div(function)(a)
    dev_b = find_div(function)(b)

    print("Производная в точке A: " + util.pretty_round(dev_a))
    print("Производная в точке B: " + util.pretty_round(dev_b))

    lmbd = 0

    if dev_a > dev_b:
        lmbd = -(1 / dev_a)
        print("Лямбда А = " + util.pretty_round(lmbd))
    else:
        lmbd = -(1 / dev_b)
        print("Лямбда B = " + util.pretty_round(lmbd))

    fi = lambda x: x + lmbd * function(x)
    print("Фи A = " + util.pretty_round(fi(a)))

    fi_s = find_div(fi)

    fi_s_a = round(fi_s(a))
    fi_s_b = round(fi_s(b))

    # print("Производная фи в А: " + util.pretty_round(fi_s_a))
    # print("Производная фи в B: " + util.pretty_round(fi_s_b))

    if (abs(fi_s_a) >= 1) | (abs(fi_s_b) >= 1):
        print("Не удовлетворяет достаточному условию сходимости!\nПробую другой способ!\n")
        return iterations2(function, a, b, accuracy)
    else:
        print("Удовлетворяет достаточному условию сходимости!")

    x_current = a
    x_prev = a * 1000 + 10

    iterations = 0

    while (abs(x_prev - x_current) > accuracy) or (abs(function(x_current)) > accuracy):

        x_prev = x_current
        x_current = x_prev + lmbd * function(x_prev)
        # print(x_current, x_prev, function(x_current))
        iterations += 1
        if iterations > 1000:
            print("Итерационный процесс расходится")
            exit(-1)

    return x_current, iterations


def calc_newton_system(function1, function2, start_x, start_y, error):
    x_current = start_x
    y_current = start_y
    y_prev = y_current * 1000 + 10
    x_prev = x_current * 1000 + 10
    iterations = 0

    while max(abs(x_current - x_prev), abs(y_current - y_prev)) > error:
        x_prev = x_current
        y_prev = y_current
        С = calc(function1, function2, x_prev, y_prev)
        A = function1(x_prev, y_prev) / С
        B = function2(x_prev, y_prev) / С
        x_current = x_prev - A * calc_dy(function2, x_prev, y_prev) + B * calc_dy(function1, x_prev, y_prev)
        y_current = y_prev + A * calc_dx(function2, x_prev, y_prev) - B * calc_dx(function1, x_prev, y_prev)
        iterations += 1
        if iterations == 100:
            print("Итерационный процесс расходится")
            exit()
    return x_current, y_current, abs(x_current - x_prev), abs(y_current - y_prev), iterations
