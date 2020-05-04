int main()
{
    int g, k;
    g = 33;
    k = g * g + 4;
    while(k < 22) {
        if(g < 6) {
            ++k;
        } else {
            k += 6 * k + 1;
        }
    }

    return k * 14;
}