.section .text

.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $16, %esp
	movl $0, -8(%ebp)
	jmp KREG.4
	movl $5, -4(%ebp)
KREG.4:
	jmp KREG.5
KREG.6:
	mov -8(%ebp), %ecx
	mov $2, %edx
	imul %ecx, %edx
	mov %edx, -8(%ebp)
	mov -4(%ebp), %ebx
	mov %ebx, -12(%ebp)
	mov -4(%ebp), %ecx
	mov $1, %edx
	add %ecx, %edx
	mov %edx, -4(%ebp)
	mov -4(%ebp), %ebx
	mov %ebx, -16(%ebp)
KREG.5:
	mov -4(%ebp), %ebx
	mov $10, %esi
	cmp %ebx, %esi
	jl KREG.6
KREG.7:
	mov -8(%ebp), %ebx
	mov $0, %esi
	cmp %ebx, %esi
	je KREG.10
	jmp KREG.11
KREG.10:
	movl $0, -8(%ebp)
	jmp KREG.16
KREG.11:
	mov -8(%ebp), %ebx
	mov $9, %esi
	cmp %ebx, %esi
	je KREG.12
	jmp KREG.13
KREG.12:
	movl $9, -8(%ebp)
	movl $200, -4(%ebp)
	mov $200, %ebx
	mov $200, %esi
	cmp %ebx, %esi
	jl KREG.17
	jmp KREG.18
KREG.17:
	movl $0, -4(%ebp)
	jmp KREG.19
KREG.18:
	movl $500, -4(%ebp)
KREG.19:
	jmp KREG.16
KREG.13:
	mov -8(%ebp), %ebx
	mov $8, %esi
	cmp %ebx, %esi
	je KREG.14
	jmp KREG.15
KREG.14:
	movl $8, -8(%ebp)
	jmp KREG.16
KREG.15:
	movl $0, -8(%ebp)
KREG.16:
	mov $0, %eax
	leave
	ret
