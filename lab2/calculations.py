import functions
import numpy as np
import matplotlib.pyplot as plt

some_small_num = 0.00000001


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


def half_div_method(function, a, b, error):
    x_current = 0
    a_current = a
    b_current = b
    iterations = 0
    x_current = (a + b) / 2
    while (abs(a - b) > error) & (function.__call__(x_current) > error):
        x_current = (a_current + b_current) / 2
        if function.__call__(a_current) * function.__call__(x_current) > 0:
            a_current = x_current
        else:
            b_current = x_current
        iterations = iterations + 1
    x_current = (a_current + b_current) / 2
    return x_current, iterations


def iterrations_method(function, a, b, error):
    dev_a = find_div(function)(a)
    dev_b = find_div(function)(b)

    print("Производная в точке A: " + str(dev_a))
    print("Производная в точке B: " + str(dev_b))

    lyambd_a = -(1 / dev_a)
    lyambd_b = - (1 / dev_b)

    print("Лямбда А = " + str(lyambd_a))
    print("Лямбда B = " + str(lyambd_b))

    if (dev_a > dev_b):
        lyambd = lyambd_a
    else:
        lyambd = lyambd_b

    fi = lambda x: x + lyambd * function(x)

    fi_s = find_div(fi)

    fi_s_a = fi_s(a)
    fi_s_b = fi_s(b)

    print("Производная фи в А: " + str(fi_s_a))
    print("Производная фи в B: " + str(fi_s_b))

    if (abs(fi_s(a)) > 1 or abs(fi_s(b)) > 1):
        print("Не удовлетворяет достаточному условию сходимости")
    else:
        print("Удовлетворяет достаточному условию сходимости")

    x_current = a
    x_prev = a * 1000 + 10

    iterations = 0

    while (abs(x_prev - x_current) > error) or (abs(function(x_current)) > error):

        x_prev = x_current
        x_current = x_prev + lyambd * function(x_prev)
        print(x_current, x_prev, function(x_current))
        iterations += 1
        if (iterations > 1000):
            print("Алгоритм расходится")
            exit()

    return x_current, iterations


def calc_dx(function, x, y, h=some_small_num):
    return (function(x + h, y) - function(x - h, y)) / (2 * h)


def calc_dy(function, x, y, h=some_small_num):
    return (function(x, y + h) - function(x, y - h)) / (2 * h)


def calc_j(function1, function2, x, y):
    return calc_dx(function1, x, y) * calc_dy(function2, x, y) - calc_dx(function2, x, y) * calc_dy(function1, x, y)


def calc_newton_system(function1, function2, start_x, start_y, error):
    x_current = start_x
    y_current = start_y
    y_prev = y_current * 1000 + 10
    x_prev = x_current * 1000 + 10
    iterations = 0

    while max(abs(x_current - x_prev), abs(y_current - y_prev)) > error:
        x_prev = x_current
        y_prev = y_current
        J = calc_j(function1, function2, x_prev, y_prev)
        A = function1(x_prev, y_prev) / J
        B = function2(x_prev, y_prev) / J
        x_current = x_prev - A * calc_dy(function2, x_prev, y_prev) + B * calc_dy(function1, x_prev, y_prev)
        y_current = y_prev + A * calc_dx(function2, x_prev, y_prev) - B * calc_dx(function1, x_prev, y_prev)
        iterations += 1
        if (iterations == 100):
            print("Расходится")
            exit()
    return x_current, y_current, abs(x_current - x_prev), abs(y_current - y_prev), iterations
