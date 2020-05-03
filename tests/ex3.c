int global_var;

int foo(int m, int f) {
    return m + f;
}

int main(int argc, char** argv)
{
    int m, a;
    float t, f;
    goto hell;
    int unreachable = 10 * 10;

hell:
    ++a;
    a++;
    m = foo(foo()) * 2;
    a = m * a;
    t = m * foo(m*5, f);


    return m + 4 + (6 * 99);
}
