.section .text

.global foo
foo:
	push %ebp
	mov %esp, %ebp
	sub $12, %esp
	movl $100, -4(%ebp)
	mov 8(%ebp), %ecx
	mov 12(%ebp), %edx
	add %ecx, %edx
	mov %edx, -8(%ebp)
	mov $100, %ecx
	mov -8(%ebp), %edx
	imul %ecx, %edx
	mov %edx, %eax
	leave
	ret
.global foobar
foobar:
	push %ebp
	mov %esp, %ebp
	sub $8, %esp
	movl $1040, -4(%ebp)
	movl $1052, %eax
	leave
	ret
.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $56, %esp
	movl $8, -4(%ebp)
	movl $2, -8(%ebp)
	movl $3, -12(%ebp)
	movl $100, -16(%ebp)
	movl $300, -32(%ebp)
	movl $302, -36(%ebp)
	movl $2416, -40(%ebp)
	movl $2416, -20(%ebp)
	call foobar
	mov %eax, -24(%ebp)
	push -24(%ebp)
	push -20(%ebp)
	call foo
	mov %eax, -28(%ebp)
	mov -48(%ebp), %ecx
	mov -44(%ebp), %edx
	imul %ecx, %edx
	mov %edx, %ecx
	mov $2416, %edx
	add %ecx, %edx
	mov %edx, %eax
	leave
	ret
