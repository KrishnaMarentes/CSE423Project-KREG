int foo(int a, int b) {
    int j = 100;
    return j * (a+b);
}

int foobar() {
    return 12 + 8 * 130;
}

int main(int argc) {
    int i = 8, j = 2;
    int k = 3;
    int f = 100;

    int a = i * (j + f * k);
    int b = foobar();
    int c = foo(a, b);

    return c * b + a;
}