int foo(int m, int f) {
    return m + f;
}

int main(int argc, char** argv)
{
    int m;
    float t, f;

    m = foo(foo2()) * 2;
    t = m * foo(m*5, f);

    return m + 4 + (6 * 99);
}