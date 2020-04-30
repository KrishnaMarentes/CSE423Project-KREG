.section .text
.global foo
foo:
	push %ebp
	mov %esp, %ebp
	sub $8, %esp
	mov 8(%ebp), %ecx
	mov 12(%ebp), %edx
	add %ecx, %edx
	mov %edx, -8(%ebp)
	mov -8(%ebp), %ebx
	mov %ebx, -4(%ebp)
	mov -4(%ebp), %eax
	leave
	ret
.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $32, %esp
	mov $3, %ecx
	mov $4, %edx
	shl %ecx, %edx
	mov %edx, -16(%ebp)
	mov -16(%ebp), %ebx
	mov %ebx, -8(%ebp)
	mov $16, %ecx
	mov -8(%ebp), %edx
	or %ecx, %edx
	mov %edx, -20(%ebp)
	mov -20(%ebp), %ebx
	mov %ebx, -4(%ebp)
	mov -4(%ebp), %eax
	xor 0xFFFFFFFF, %eax
	mov %eax, -4(%ebp)
	mov -4(%ebp), %ebx
	mov %ebx, -24(%ebp)
	mov $4, %ecx
	mov -24(%ebp), %edx
	add %ecx, %edx
	mov %edx, -28(%ebp)
	mov -28(%ebp), %ebx
	mov %ebx, -12(%ebp)
	push -12(%ebp)
	push -8(%ebp)
	call foo
	mov %eax, -32(%ebp)
	mov -4(%ebp), %ecx
	mov -32(%ebp), %edx
	add %ecx, %edx
	mov %edx, -4(%ebp)
	mov -4(%ebp), %eax
	leave
	ret
