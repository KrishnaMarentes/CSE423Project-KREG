int main()
{
    char grade = 'B';
    int num, curvedgrade;

    switch(grade)
    {
        case 'A':
            num = 1;
            break;
        case 'B':
            num = 2;
            break;
        case 'C':
            num = 3;
            break;
        case 'D':
            num = 4;
            break;
        case 'F':
            num = 5;
            break;
        default:
            num = 6;
    }

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
