int foo(int m, int f) {
    return m + f;
}

int main(int argc, char** argv)
{
    int m, a;
    float t, f;

    a = 1;
    m = foo(foo2()) * 2;
    t = m * foo(m*5, f);

    return m + 4 + (6 * 99);
}