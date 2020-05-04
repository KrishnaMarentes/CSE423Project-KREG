.section .text

.global calc
calc:
	push %ebp
	mov %esp, %ebp
	sub $4, %esp
	movl $0, -4(%ebp)
	mov -4(%ebp), %eax
	leave
	ret
.global main
main:
	push %ebp
	mov %esp, %ebp
	sub $12, %esp
	movl $4, -4(%ebp)
	call calc
	mov %eax, %ecx
	mov $2, %edx
	add %ecx, %edx
	mov %edx, %eax
	leave
	ret
