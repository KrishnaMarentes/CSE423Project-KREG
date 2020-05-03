.section .text

.global foo
foo:
	push %ebp
	mov %esp, %ebp
	sub $16, %esp
	movl $2, -4(%ebp)
	movl $1, -8(%ebp)
	movl $3, -12(%ebp)
	mov -12(%ebp), %ebx
	mov %ebx, -16(%ebp)
	mov -4(%ebp), %eax
	leave
	ret
.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $4, %esp
	push $
	call foo
	mov %eax, -4(%ebp)
	leave
	ret
