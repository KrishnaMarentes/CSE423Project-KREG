.section .text

.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $32, %esp
	movl $33, -4(%ebp)
	movl $1089, -12(%ebp)
	movl $1093, -16(%ebp)
	movl $1093, -8(%ebp)
	jmp KREG.3
KREG.4:
	mov -4(%ebp), %ebx
	mov $6, %esi
	cmp %ebx, %esi
	jl KREG.8
	jmp KREG.9
KREG.8:
	mov -8(%ebp), %ecx
	mov $1, %edx
	add %ecx, %edx
	mov %edx, -8(%ebp)
	jmp KREG.10
KREG.9:
	mov $6, %ecx
	mov -8(%ebp), %edx
	imul %ecx, %edx
	mov %edx, %ecx
	mov $1, %edx
	add %ecx, %edx
	mov %edx, -28(%ebp)
	mov -8(%ebp), %ecx
	mov -28(%ebp), %edx
	add %ecx, %edx
	mov %edx, -8(%ebp)
KREG.10:
KREG.3:
	mov -8(%ebp), %ebx
	mov $22, %esi
	cmp %ebx, %esi
	jl KREG.4
KREG.5:
	mov -8(%ebp), %ecx
	mov $14, %edx
	imul %ecx, %edx
	mov %edx, %eax
	leave
	ret
