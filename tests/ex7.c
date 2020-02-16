#include<stdio.h>

int main()
{
    // Local Variable Definition
    char grade;
    int num, curvedgrade;

    printf("Enter your grade:\n");
    scanf("%c", &grade);

    switch(grade)
    {
        case 'A':
            printf("Excellent\n");
            break;
        case 'B':
            printf("Keep it up!\n");
            break;
        case 'C':
            printf("Well done\n");
            break;
        case 'D':
            printf("You passed\n");
            break;
        case 'F':
            printf("Better luck next time\n");
            break;
        default:
            printf("Invalid grade\n");
    }
    printf("Your grade is %c\n",grade);

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