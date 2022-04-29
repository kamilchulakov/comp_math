from math import log2
import numpy as np
import matplotlib.pyplot as plt


def nice_value(num):
    return round(num * 1000) / 1000


data = [-0.53, -0.93, 0.48, -1.55, -1.34, -0.04, -0.84, 0.57, 0.76, 0.30,
        -0.87, -0.41, 0.81, -1.42, -0.61, -0.33, -1.33, 0.62, -0.48, -0.35]

data = sorted(data)
print("Вариационный ряд:")
for i in range(20):
    print(data[i], end=" ")

mn = data[0]
mx = data[-1]
print("\n\nЭкстремальные значения:")
print("Минимум: ", mn)
print("Максимум: ", mx)

print("\nРазмах:")
print(nice_value(abs(mx - mn)))

math_exp = nice_value(sum(data) / len(data))
print("\nМатематическое ожидание:")
print(math_exp)

disp = 0
for i in data:
    disp += (i - math_exp) ** 2
disp /= 20
print("\nСреднеквадратичное отклонение:")
print(nice_value(disp ** 0.5))


def function(x):
    count = 0
    for i in data:
        if i < x:
            count += 1
    return count / len(data)


x = np.linspace(mn - 0.1, mx + 0.1, 10000)
y = [function(i) for i in x]
fig = plt.figure()
ax = fig.add_subplot(1, 1, 1)
ax.spines['left'].set_position('center')
ax.spines['bottom'].set_position('zero')

plt.plot(x, y, 'b')
plt.title("Эмпирическая функция распределения")
plt.show()

# # emp
F = {}
count = 1
for x in data:
    if F.__contains__(x):
        count += 1
    F[x] = count
    count = 1


n, xvals, y = len(F.keys()), list(F.keys()), 0
print("\nЭмпирическая функция распределения")
print("0: x <=", nice_value(xvals[0]))
for i in range(n - 1):
    y += F[xvals[i]] / len(data)
    # plt.plot([xvals[i], xvals[i + 1]], [y, y], c="black")
    print(str(nice_value(y)) + ":", nice_value(xvals[i]), "< x <=", nice_value(xvals[i + 1]))
print("1: x >", nice_value(xvals[-1]))
# plt.title("Эмпирическая функция распределения")
# plt.show()

w = 1 + int(log2(len(data)))
h = abs(mx - mn) / w
left = mn
bins = [left + h * i for i in range(w + 1)]
plt.xticks(bins)
plt.hist(data, bins=bins, density=False, edgecolor='black')
locs, _ = plt.yticks() # для частости
plt.yticks(locs, np.round(locs / len(data), 3))
plt.title("Гистограмма")
plt.ylabel("Частость")
plt.xlabel("Значения")

chast = {}
count = 1
curr = mn
for x in data:
    if x > curr + h:
        curr += h
        count = 1
    chast[curr] = count
    count += 1
ln = 0
print("\nГруппированная выборка:")
print("Диапозон ", end=" ")
for key in chast.keys():
    st = "({:.2f} - {:.2f})".format(key, key + h)
    ln = max(ln, len(st))
    print(st, end=" ")
print()
print("Частота  ", end=" ")
for value in chast.values():
    st = str(value)
    print(st.ljust(ln), end=" ")
print()
print("Частость ", end=" ")
for value in chast.values():
    st = "{:.2f}".format(value / len(data))
    print(st.ljust(ln), end=" ")

plt.show()

uniq = sorted(set(data))
p_arr = {(2 * x + h) / 2: chast[x] / len(data) for x in chast.keys()}
y = [p_arr[i] for i in p_arr.keys()]

plt.plot(p_arr.keys(), p_arr.values(), marker="o")
plt.title("Полигон частости")
plt.show()
