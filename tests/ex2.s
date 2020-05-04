.section .text

.global foo
foo:
	push %ebp
	mov %esp, %ebp
	sub $28, %esp
	movl $2, -4(%ebp)
	movl $1, -8(%ebp)
	movl $3, -12(%ebp)
	movl $3, -16(%ebp)
	mov $2, %ecx
	mov 8(%ebp), %edx
	imul %ecx, %edx
	mov %edx, %ecx
	mov $3, %edx
	add %ecx, %edx
	mov %edx, %ecx
	mov 12(%ebp), %edx
	sub %ecx, %edx
	mov %edx, %eax
	leave
	ret
.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $8, %esp
	push $22
	push $5
	call foo
	mov %eax, -4(%ebp)
	leave
	ret
