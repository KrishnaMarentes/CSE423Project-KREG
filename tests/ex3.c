int foo(int m, int f) {
    return m + f;
}

int main(int argc, char** argv)
{
    int m, a;
    float t, f;
    goto hell;
    int unreachable = 10;

hell:
    ++a;
    a++;
    m = foo(foo2()) * 2;
    a2 = m * a;
    t = m * foo(m*5, f);


    return m + 4 + (6 * 99);
}