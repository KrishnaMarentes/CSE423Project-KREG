.section .text

.global foo
foo:
	push %ebp
	mov %esp, %ebp
	sub $4, %esp
	mov 8(%ebp), %ecx
	mov 12(%ebp), %edx
	add %ecx, %edx
	mov %edx, %eax
	leave
	ret
.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $68, %esp
	jmp KREG.1
	mov $10, %ecx
	mov $10, %edx
	imul %ecx, %edx
	mov %edx, -12(%ebp)
KREG.1:
	mov -8(%ebp), %ecx
	mov $1, %edx
	add %ecx, %edx
	mov %edx, -8(%ebp)
	mov -8(%ebp), %ebx
	mov %ebx, -24(%ebp)
	mov -8(%ebp), %ecx
	mov $1, %edx
	add %ecx, %edx
	mov %edx, -8(%ebp)
	mov -8(%ebp), %ebx
	mov %ebx, -28(%ebp)
	push $
	call foo
	mov %eax, -32(%ebp)
	push -32(%ebp)
	call foo
	mov %eax, %ecx
	mov $2, %edx
	imul %ecx, %edx
	mov %edx, -4(%ebp)
	mov -40(%ebp), %ecx
	mov -8(%ebp), %edx
	imul %ecx, %edx
	mov %edx, -8(%ebp)
	mov -40(%ebp), %ecx
	mov $5, %edx
	imul %ecx, %edx
	mov %edx, -48(%ebp)
	push $f
	push -48(%ebp)
	call foo
	mov %eax, -52(%ebp)
	mov -40(%ebp), %ecx
	mov -52(%ebp), %edx
	imul %ecx, %edx
	mov %edx, $t
	mov -40(%ebp), %ecx
	mov $4, %edx
	add %ecx, %edx
	mov %edx, -60(%ebp)
	movl $594, -64(%ebp)
	mov -60(%ebp), %ecx
	mov $594, %edx
	add %ecx, %edx
	mov %edx, %eax
	leave
	ret
