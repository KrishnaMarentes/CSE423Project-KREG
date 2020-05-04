.section .text

.global get_curvedgrade
get_curvedgrade:
	push %ebp
	mov %esp, %ebp
	sub $8, %esp
	mov 8(%ebp), %ecx
	mov $10, %edx
	add %ecx, %edx
	mov %edx, -4(%ebp)
	mov -4(%ebp), %eax
	leave
	ret
.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $20, %esp
	movl $66, -4(%ebp)
	mov $66, %ebx
	mov $65, %esi
	cmp %ebx, %esi
	je KREG.7
	jmp KREG.8
KREG.7:
	movl $90, -8(%ebp)
	jmp KREG.13
KREG.8:
	mov -4(%ebp), %ebx
	mov $66, %esi
	cmp %ebx, %esi
	je KREG.9
	jmp KREG.10
KREG.9:
	movl $80, -8(%ebp)
	jmp KREG.13
KREG.10:
	mov -4(%ebp), %ebx
	mov $67, %esi
	cmp %ebx, %esi
	je KREG.11
	jmp KREG.12
KREG.11:
	movl $70, -8(%ebp)
	jmp KREG.13
KREG.12:
	movl $60, -8(%ebp)
KREG.13:
	push -4(%ebp)
	call get_curvedgrade
	mov %eax, -12(%ebp)
	jmp KREG.3
KREG.4:
	mov -4(%ebp), %ecx
	mov $1, %edx
	add %ecx, %edx
	mov %edx, -4(%ebp)
KREG.3:
	mov -4(%ebp), %ebx
	mov -12(%ebp), %esi
	cmp %ebx, %esi
	jl KREG.4
KREG.5:
	mov -4(%ebp), %eax
	leave
	ret
