.section .text

.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $40, %esp
	movl $5, -4(%ebp)
	movl $20, -8(%ebp)
	mov $5, %ebx
	mov -8(%ebp), %esi
	cmp %ebx, %esi
	je KREG.0
	jmp KREG.1
KREG.0:
	mov -4(%ebp), %ecx
	mov -8(%ebp), %edx
	add %ecx, %edx
	mov %edx, -12(%ebp)
	jmp KREG.1
KREG.1:
	mov -4(%ebp), %ebx
	mov -8(%ebp), %esi
	cmp %ebx, %esi
	jle KREG.3
	jmp KREG.4
KREG.3:
	mov -4(%ebp), %ecx
	mov -8(%ebp), %edx
	imul %ecx, %edx
	mov %edx, -12(%ebp)
	jmp KREG.4
KREG.4:
	movl $0, -4(%ebp)
	movl $10, -8(%ebp)
	mov $0, %ebx
	mov -8(%ebp), %esi
	cmp %ebx, %esi
	jg KREG.6
	jmp KREG.7
KREG.6:
	mov -12(%ebp), %ebx
	mov %ebx, -24(%ebp)
	mov -12(%ebp), %ecx
	mov $1, %edx
	add %ecx, %edx
	mov %edx, -12(%ebp)
	mov -12(%ebp), %ebx
	mov %ebx, -28(%ebp)
	jmp KREG.8
KREG.7:
	mov -4(%ebp), %ecx
	mov -8(%ebp), %edx
	sub %ecx, %edx
	mov %edx, -12(%ebp)
KREG.8:
	mov -4(%ebp), %ebx
	mov -8(%ebp), %esi
	cmp %ebx, %esi
	je KREG.12
	jmp KREG.13
KREG.12:
	mov -12(%ebp), %ebx
	mov %ebx, -36(%ebp)
	mov -12(%ebp), %ecx
	mov $1, %edx
	add %ecx, %edx
	mov %edx, -12(%ebp)
	mov -12(%ebp), %ebx
	mov %ebx, -40(%ebp)
	jmp KREG.13
KREG.13:
	leave
	ret
