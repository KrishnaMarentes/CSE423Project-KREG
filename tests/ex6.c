int main()
{
    char grade = 'B';
    int num, curvedgrade;

    if(grade == 'A') {
        num = 90;
    } else if(grade == 'B') {
        num = 80;
    } else if(grade == 'C') {
        num = 70;
    } else {
        num = 60;
    }

    curvedgrade = num + 10;

    return 0;
}
