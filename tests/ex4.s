.section .text
	.global _start

calc:
	movl %esp, %ebp
	popl %ebp
	ret
_start: 
	call calc
