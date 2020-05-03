int main()
{
    int i, j;
    i = 3;
    j = i - 1;
    while(i < 7) {
        if(i < j) {
            ++i;
        } else {
            j = (j + 30) / i;
        }
    }
    return i;
}