int foo(int f, int m) {
    int m = 6 + f;
    return m * 4;
}

int main()
{
    int i;
    i = 12 * 18;
    i = foo(1, 25);
    return i;
}
