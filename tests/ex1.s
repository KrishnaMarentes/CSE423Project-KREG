.section .text

.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $12, %esp
	movl $3, -4(%ebp)
	mov -4(%ebp), %ecx
	mov $1, %edx
	sub %ecx, %edx
	mov %edx, -8(%ebp)
	jmp KREG.3
KREG.4:
	mov -4(%ebp), %ebx
	mov -8(%ebp), %esi
	cmp %ebx, %esi
	jl KREG.7
	jmp KREG.8
KREG.7:
	mov -4(%ebp), %ecx
	mov $1, %edx
	add %ecx, %edx
	mov %edx, -4(%ebp)
	jmp KREG.9
KREG.8:
	mov -8(%ebp), %ecx
	mov $30, %edx
	add %ecx, %edx
	mov %edx, %eax
	mov -4(%ebp), %ecx
	xor %edx, %edx
	div %ecx
	mov %eax, -8(%ebp)
KREG.9:
KREG.3:
	mov -4(%ebp), %ebx
	mov $7, %esi
	cmp %ebx, %esi
	jl KREG.4
KREG.5:
	mov -4(%ebp), %eax
	leave
	ret
