int foo(int m, int f) {
    return m + f;
}

int main(int argc, char** argv)
{
    int m;
    float t, f;

    m = foo(foo2()) * 2;

    return m + 4 + (6 * 99);
}